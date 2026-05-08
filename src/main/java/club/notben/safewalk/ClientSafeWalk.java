package club.notben.safewalk;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.brigadier.Command;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.lwjgl.glfw.GLFW;

public final class ClientSafeWalk {
    private static final Minecraft MC = Minecraft.getInstance();
    private static final long PLACE_DELAY_MS = 290L;
    private static final double FALL_DISABLE_DELTA = -0.4D;

    private static final KeyMapping TOGGLE_KEY = new KeyMapping(
            "key.safewalk.toggle",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_V,
            "key.categories.safewalk"
    );

    private static boolean enabled;
    private static boolean forcingSneak;
    private static boolean wasJumping;
    private static long lastPlaceMs;

    private ClientSafeWalk() {
    }

    public static void init(IEventBus modBus) {
        modBus.addListener(ClientSafeWalk::registerKeys);
        MinecraftForge.EVENT_BUS.register(ClientSafeWalk.class);
    }

    private static void registerKeys(RegisterKeyMappingsEvent event) {
        event.register(TOGGLE_KEY);
    }

    @SubscribeEvent
    public static void registerClientCommands(RegisterClientCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("safewalk")
                .then(Commands.literal("help").executes(context -> {
                    sendHelp();
                    return Command.SINGLE_SUCCESS;
                }))
                .then(Commands.literal("toggle").executes(context -> {
                    setEnabled(!enabled, "SafeWalk");
                    return Command.SINGLE_SUCCESS;
                }))
                .then(Commands.literal("mode").executes(context -> {
                    setMode(SafeWalkConfig.MODE.get().next());
                    return Command.SINGLE_SUCCESS;
                })
                        .then(Commands.literal("sneak").executes(context -> {
                            setMode(SafeWalkConfig.Mode.SNEAK);
                            return Command.SINGLE_SUCCESS;
                        }))
                        .then(Commands.literal("breezily").executes(context -> {
                            setMode(SafeWalkConfig.Mode.BREEZILY);
                            return Command.SINGLE_SUCCESS;
                        }))
                        .then(Commands.literal("scaffold").executes(context -> {
                            setMode(SafeWalkConfig.Mode.SCAFFOLD);
                            return Command.SINGLE_SUCCESS;
                        })))
                .then(booleanToggle("chat", SafeWalkConfig.CHAT_MESSAGES, "Chat Messages"))
                .then(booleanToggle("click", SafeWalkConfig.AUTO_PLACE, "AutoPlace"))
                .then(booleanToggle("fall", SafeWalkConfig.DISABLE_ON_FALL, "Disable on fall"))
                .then(booleanToggle("jump", SafeWalkConfig.DISABLE_ON_JUMP, "Disable on jump")));

        event.getDispatcher().register(Commands.literal("sw").redirect(event.getDispatcher().getRoot().getChild("safewalk")));
        event.getDispatcher().register(Commands.literal("sb").redirect(event.getDispatcher().getRoot().getChild("safewalk")));
        event.getDispatcher().register(Commands.literal("speedbridge").redirect(event.getDispatcher().getRoot().getChild("safewalk")));
    }

    private static com.mojang.brigadier.builder.LiteralArgumentBuilder<net.minecraft.commands.CommandSourceStack> booleanToggle(
            String command,
            ForgeConfigSpec.BooleanValue value,
            String label
    ) {
        return Commands.literal(command).executes(context -> {
                    value.set(!value.get());
                    value.save();
                    sendToggle(label, value.get());
                    return Command.SINGLE_SUCCESS;
                })
                .then(Commands.literal("on").executes(context -> {
                    value.set(true);
                    value.save();
                    sendToggle(label, true);
                    return Command.SINGLE_SUCCESS;
                }))
                .then(Commands.literal("off").executes(context -> {
                    value.set(false);
                    value.save();
                    sendToggle(label, false);
                    return Command.SINGLE_SUCCESS;
                }));
    }

    @SubscribeEvent
    public static void clientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        while (TOGGLE_KEY.consumeClick()) {
            setEnabled(!enabled, "SafeWalk");
        }

        LocalPlayer player = MC.player;
        Level level = MC.level;
        if (player == null || level == null || MC.screen != null) {
            releaseSneak();
            return;
        }

        boolean jumpingNow = MC.options.keyJump.isDown();
        if (enabled && SafeWalkConfig.DISABLE_ON_JUMP.get() && jumpingNow && !wasJumping) {
            setEnabled(false, SafeWalkConfig.MODE.get().name());
        }
        wasJumping = jumpingNow;

        if (!enabled) {
            releaseSneak();
            return;
        }

        if (SafeWalkConfig.DISABLE_ON_FALL.get() && player.getDeltaMovement().y < FALL_DISABLE_DELTA) {
            setEnabled(false, SafeWalkConfig.MODE.get().name());
            releaseSneak();
            return;
        }

        boolean overEdge = isOverEdge(player, level);
        applyMode(player, level, overEdge);
    }

    private static void applyMode(LocalPlayer player, Level level, boolean overEdge) {
        boolean userSneaking = MC.options.keyShift.isDown() && !forcingSneak;

        if (!overEdge || userSneaking || !player.onGround()) {
            releaseSneak();
            return;
        }

        switch (SafeWalkConfig.MODE.get()) {
            case SNEAK -> {
                holdSneak();
                if (SafeWalkConfig.AUTO_PLACE.get()) {
                    tryPlaceAtCrosshair(player);
                }
            }
            case BREEZILY -> {
                releaseSneak();
                player.setDeltaMovement(player.getDeltaMovement().multiply(0.5D, 1.0D, 0.5D));
                if (SafeWalkConfig.AUTO_PLACE.get() && !tryPlaceAtCrosshair(player)) {
                    holdSneak();
                }
            }
            case SCAFFOLD -> {
                releaseSneak();
                if (SafeWalkConfig.AUTO_PLACE.get() && !tryScaffoldPlace(player, level)) {
                    holdSneak();
                }
            }
        }
    }

    private static boolean isOverEdge(LocalPlayer player, Level level) {
        BlockPos feet = BlockPos.containing(player.getX(), player.getY() - 1.0D, player.getZ());
        return level.isEmptyBlock(feet);
    }

    private static boolean tryPlaceAtCrosshair(LocalPlayer player) {
        if (!(MC.hitResult instanceof BlockHitResult hit) || hit.getType() != HitResult.Type.BLOCK) {
            return false;
        }
        if (hit.getDirection() == Direction.UP || hit.getDirection() == Direction.DOWN) {
            return false;
        }
        return place(player, hit);
    }

    private static boolean tryScaffoldPlace(LocalPlayer player, Level level) {
        BlockPos emptyBelow = BlockPos.containing(player.getX(), player.getY() - 1.0D, player.getZ());

        for (Direction direction : Direction.values()) {
            if (direction == Direction.DOWN) {
                continue;
            }

            BlockPos neighbor = emptyBelow.relative(direction);
            if (level.isEmptyBlock(neighbor)) {
                continue;
            }

            Direction clickedFace = direction.getOpposite();
            Vec3 hitLocation = Vec3.atCenterOf(neighbor).add(Vec3.atLowerCornerOf(clickedFace.getNormal()).scale(0.5D));
            return place(player, new BlockHitResult(hitLocation, clickedFace, neighbor, false));
        }

        return false;
    }

    private static boolean place(LocalPlayer player, BlockHitResult hit) {
        if (MC.gameMode == null || System.currentTimeMillis() - lastPlaceMs < PLACE_DELAY_MS) {
            return false;
        }

        ItemStack stack = player.getItemInHand(InteractionHand.MAIN_HAND);
        if (stack.isEmpty() || !(stack.getItem() instanceof BlockItem)) {
            return false;
        }

        lastPlaceMs = System.currentTimeMillis();
        MC.gameMode.useItemOn(player, InteractionHand.MAIN_HAND, hit);
        player.swing(InteractionHand.MAIN_HAND);
        return true;
    }

    private static void holdSneak() {
        forcingSneak = true;
        MC.options.keyShift.setDown(true);
    }

    private static void releaseSneak() {
        if (forcingSneak) {
            MC.options.keyShift.setDown(false);
            forcingSneak = false;
        }
    }

    private static void setEnabled(boolean value, String label) {
        enabled = value;
        if (!enabled) {
            releaseSneak();
        }
        sendToggle(label, enabled);
    }

    private static void setMode(SafeWalkConfig.Mode mode) {
        SafeWalkConfig.MODE.set(mode);
        SafeWalkConfig.MODE.save();
        send(Component.literal("[Mode] ").withStyle(ChatFormatting.LIGHT_PURPLE)
                .append(Component.literal("set to ").withStyle(ChatFormatting.YELLOW))
                .append(Component.literal(mode.name()).withStyle(mode == SafeWalkConfig.Mode.SCAFFOLD ? ChatFormatting.RED : ChatFormatting.GREEN)));
    }

    private static void sendToggle(String label, boolean state) {
        send(Component.literal("[").withStyle(ChatFormatting.GRAY)
                .append(Component.literal(label).withStyle(ChatFormatting.LIGHT_PURPLE))
                .append(Component.literal("] ").withStyle(ChatFormatting.GRAY))
                .append(Component.literal(state ? "on" : "off").withStyle(state ? ChatFormatting.GREEN : ChatFormatting.RED)));
    }

    private static void sendHelp() {
        send(Component.literal("SafeWalk v3.0.0").withStyle(ChatFormatting.WHITE));
        send(Component.literal("Aliases: /sw, /safewalk, /sb, /speedbridge").withStyle(ChatFormatting.GRAY));
        send(Component.literal("/sw toggle, /sw mode [sneak|breezily|scaffold]").withStyle(ChatFormatting.AQUA));
        send(Component.literal("/sw chat|click|fall|jump [on|off]").withStyle(ChatFormatting.AQUA));
    }

    private static void send(Component message) {
        if (!SafeWalkConfig.CHAT_MESSAGES.get() && !message.getString().contains("Chat Messages")) {
            return;
        }
        if (MC.player != null) {
            MC.player.displayClientMessage(message, false);
        }
    }
}
