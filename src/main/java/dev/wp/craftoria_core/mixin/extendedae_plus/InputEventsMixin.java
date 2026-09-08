package dev.wp.craftoria_core.mixin.extendedae_plus;

import com.extendedae_plus.client.InputEvents;
import com.extendedae_plus.compat.EmiHelper;
import com.extendedae_plus.compat.JeiRuntimeCompat;
import dev.emi.emi.config.EmiConfig;
import dev.emi.emi.runtime.EmiReloadManager;
import dev.emi.emi.screen.EmiScreenBase;
import mezz.jei.api.runtime.IJeiRuntime;
import net.neoforged.neoforge.client.event.ScreenEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InputEvents.class)
public class InputEventsMixin {
    @Inject(method = "onMouseButtonPre", at = @At("HEAD"), cancellable = true)
    private static void skipWhenOverlayHidden(ScreenEvent.MouseButtonPressed.Pre event, CallbackInfo ci) {
        if (!craftoriaCore$isOverlayVisible()) ci.cancel();
    }

    @Inject(method = "onMouseButtonReleasedPre", at = @At("HEAD"), cancellable = true)
    private static void skipWhenOverlayHidden(ScreenEvent.MouseButtonReleased.Pre event, CallbackInfo ci) {
        if (!craftoriaCore$isOverlayVisible()) ci.cancel();
    }

    @Unique private static boolean craftoriaCore$isOverlayVisible() {
        if (EmiHelper.isLoaded()) {
            return EmiReloadManager.isLoaded() && EmiConfig.enabled && !EmiScreenBase.getCurrent().isEmpty();
        }

        IJeiRuntime runtime = JeiRuntimeCompat.getRuntime();
        return runtime != null && runtime.getIngredientListOverlay().isListDisplayed();
    }
}
