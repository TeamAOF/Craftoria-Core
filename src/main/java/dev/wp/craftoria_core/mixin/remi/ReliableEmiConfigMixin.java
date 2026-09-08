package dev.wp.craftoria_core.mixin.remi;

import dev.emi.emi.search.EmiSearch;
import dev.wp.craftoria_core.util.RemiGroupReloadState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(targets = "com.evandev.remi.config.ReliableEmiConfig", remap = false)
public class ReliableEmiConfigMixin {
    @Redirect(
            method = "save",
            at = @At(
                    value = "INVOKE",
                    target = "Ldev/emi/emi/search/EmiSearch;bake()V"
            )
    )
    private static void craftoria$skipGroupReloadBake() {
        if (!RemiGroupReloadState.isActive()) {
            EmiSearch.bake();
        }
    }
}
