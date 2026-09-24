package dev.wp.craftoria_core.mixin.sound_physics;

import com.sonicether.soundphysics.Loggers;
import com.sonicether.soundphysics.SoundPhysics;
import com.sonicether.soundphysics.profiling.TaskProfiler;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Restriction(
        require = {
                @Condition("sound_physics_remastered")
        }
)
@Mixin(SoundPhysics.class)
public class SoundPhysicsMixin {
    @Unique
    private static boolean craftoriaCore$enabled;

    @Shadow
    private static Minecraft minecraft;
    @Shadow
    private static TaskProfiler profiler;
    @Shadow
    static native void setupEFX();


    /**
     * @author WP
     * @reason Fix crash when audio isn't enabled
     */
    @Overwrite
    public static void init() {
        Loggers.log("Initializing Sound Physics");

        try {
            setupEFX();
            Loggers.log("EFX ready");
            craftoriaCore$enabled = true;
        } catch (Throwable var1) {
            Loggers.error("Failed to setup EFX", var1);
            Loggers.error("Disabling sound physics");
            craftoriaCore$enabled = false;
        }

        minecraft = Minecraft.getInstance();
        profiler = new TaskProfiler("Sound Physics");
    }

    @Inject(
            method = "syncReverbParams",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void syncReverbParamsInject(CallbackInfo ci) {
        if (!craftoriaCore$enabled) ci.cancel();
    }
}
