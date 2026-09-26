package dev.wp.craftoria_core.mixin.client.spectrum;

import de.dafuqs.spectrum.registries.SpectrumTooltips;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Restriction(require = @Condition("spectrum"))
@Mixin(SpectrumTooltips.class)
public class SpectrumTooltipsMixin {
    @Inject(
            method = "addSpawnerTooltips",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void cancelSpawnerTooltips(CallbackInfo ci) {
        // Apothic Spawners already adds spawner tooltips; Spectrum's would duplicate them.
        ci.cancel();
    }
}
