package club.notben.safewalk;

import net.minecraftforge.common.ForgeConfigSpec;

public final class SafeWalkConfig {
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.BooleanValue CHAT_MESSAGES;
    public static final ForgeConfigSpec.BooleanValue AUTO_PLACE;
    public static final ForgeConfigSpec.BooleanValue DISABLE_ON_FALL;
    public static final ForgeConfigSpec.BooleanValue DISABLE_ON_JUMP;
    public static final ForgeConfigSpec.EnumValue<Mode> MODE;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        builder.push("safewalk");

        MODE = builder
                .comment("SafeWalk mode. SNEAK holds sneak at edges. BREEZILY slows at edges and may place while looking at a side face. SCAFFOLD places against the nearest neighbor block without moving the player.")
                .defineEnum("mode", Mode.SNEAK);

        AUTO_PLACE = builder
                .comment("Allow vanilla right-click block placement assistance. This never changes player position or sends custom packets.")
                .define("autoPlace", true);

        CHAT_MESSAGES = builder
                .comment("Show SafeWalk status messages in local chat.")
                .define("chatMessages", true);

        DISABLE_ON_FALL = builder
                .comment("Disable SafeWalk if the local player starts falling from an edge.")
                .define("disableOnFall", false);

        DISABLE_ON_JUMP = builder
                .comment("Disable SafeWalk when the local player jumps.")
                .define("disableOnJump", false);

        builder.pop();
        SPEC = builder.build();
    }

    private SafeWalkConfig() {
    }

    public enum Mode {
        SNEAK,
        BREEZILY,
        SCAFFOLD;

        public Mode next() {
            Mode[] values = values();
            return values[(ordinal() + 1) % values.length];
        }
    }
}
