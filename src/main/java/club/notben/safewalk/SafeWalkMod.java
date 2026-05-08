package club.notben.safewalk;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(SafeWalkMod.MOD_ID)
public final class SafeWalkMod {
    public static final String MOD_ID = "safewalk";

    public SafeWalkMod(FMLJavaModLoadingContext context) {
        context.registerConfig(ModConfig.Type.CLIENT, SafeWalkConfig.SPEC);
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientSafeWalk.init(context.getModEventBus()));
    }
}
