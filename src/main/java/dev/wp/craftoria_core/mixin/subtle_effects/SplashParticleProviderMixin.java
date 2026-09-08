package dev.wp.craftoria_core.mixin.subtle_effects;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import einstein.subtle_effects.particle.option.SplashParticleOptions;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(targets = "einstein.subtle_effects.particle.SplashParticle$Provider")
public class SplashParticleProviderMixin {
    @WrapMethod(method = "createParticle(Leinstein/subtle_effects/particle/option/SplashParticleOptions;Lnet/minecraft/client/multiplayer/ClientLevel;DDDDDD)Lnet/minecraft/client/particle/Particle;")
    private Particle craftoriaCore$skipBrokenLavaSplash(
            SplashParticleOptions options,
            ClientLevel level,
            double x,
            double y,
            double z,
            double xSpeed,
            double ySpeed,
            double zSpeed,
            Operation<Particle> original
    ) {
        try {
            return original.call(options, level, x, y, z, xSpeed, ySpeed, zSpeed);
        } catch (IllegalStateException exception) {
            return null;
        }
    }
}
