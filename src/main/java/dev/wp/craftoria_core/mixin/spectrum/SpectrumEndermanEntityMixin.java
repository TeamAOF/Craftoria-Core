package dev.wp.craftoria_core.mixin.spectrum;

import com.bawnorton.mixinsquared.TargetHandler;
import dev.wp.craftoria_core.Craftoria;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = EnderMan.class, priority = 1500)
public class SpectrumEndermanEntityMixin {
    @TargetHandler(
            mixin = "de.dafuqs.spectrum.mixin.EndermanEntityMixin",
            name = "init"
    )
    @Redirect(
            method = "@MixinSquared:Handler",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getRandom()Lnet/minecraft/util/RandomSource;")
    )
    private RandomSource craftoriaCore$safeWorldRandom(Level level) {
        Craftoria.LOGGER.debug("Redirecting world random to safe world random");
        return RandomSource.create();
    }
}
