package dev.wp.craftoria_core.mixin.common.spectrum;

import com.bawnorton.mixinsquared.TargetHandler;
import dev.wp.craftoria_core.Craftoria;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Restriction(require = @Condition("spectrum"))
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
    private RandomSource safeWorldRandom(Level level) {
        Craftoria.LOGGER.debug("Redirecting world random to safe world random");
        return RandomSource.create();
    }
}
