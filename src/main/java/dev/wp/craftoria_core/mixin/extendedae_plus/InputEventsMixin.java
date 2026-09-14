package dev.wp.craftoria_core.mixin.extendedae_plus;

import com.extendedae_plus.client.InputEvents;
import net.neoforged.neoforge.client.event.ScreenEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InputEvents.class)
public class InputEventsMixin {
    @Inject(method = "onMouseButtonPre", at = @At("HEAD"), cancellable = true)
    private static void skip(ScreenEvent.MouseButtonPressed.Pre event, CallbackInfo ci) {
        ci.cancel();
    }

    @Inject(method = "onKeyPressedPre", at=@At("HEAD"), cancellable = true)
    private static void skip(ScreenEvent.KeyPressed.Pre event, CallbackInfo ci) {
        ci.cancel();
    }

    @Inject(method = "onMouseButtonReleasedPre", at = @At("HEAD"), cancellable = true)
    private static void skip(ScreenEvent.MouseButtonReleased.Pre event, CallbackInfo ci) {
        ci.cancel();
    }
}
