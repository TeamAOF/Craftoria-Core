package dev.wp.craftoria_core.mixin.remi;

import dev.wp.craftoria_core.util.RemiGroupReloadState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "com.evandev.remi.feature.stackgroup.StackGroupManager", remap = false)
public class StackGroupManagerMixin {
    @Inject(
            method = {
                    "toggleTagGroup(Lnet/minecraft/tags/TagKey;)V",
                    "toggleTagGroup(Lnet/minecraft/resources/ResourceLocation;)V"
            },
            at = @At("HEAD")
    )
    private static void craftoria$beginGroupReload(CallbackInfo ci) {
        RemiGroupReloadState.begin();
    }

    @Inject(
            method = {
                    "toggleTagGroup(Lnet/minecraft/tags/TagKey;)V",
                    "toggleTagGroup(Lnet/minecraft/resources/ResourceLocation;)V"
            },
            at = @At("RETURN")
    )
    private static void craftoria$endGroupReload(CallbackInfo ci) {
        RemiGroupReloadState.end();
    }
}
