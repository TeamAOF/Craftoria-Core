package dev.wp.craftoria_core.mixin.spectrum;

import de.dafuqs.spectrum.registries.SpectrumTooltips;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SpectrumTooltips.class)
public class SpectrumTooltipsMixin {
    @Inject(
            method = "addSpawnerTooltips",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void cancelSpawnerTooltips(CallbackInfo ci) {
        ci.cancel(); // We have apothic spawners handle this already.
    }
}
