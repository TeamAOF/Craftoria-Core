package dev.wp.craftoria_core.mixin.ae2wtlib;

import de.mari_023.ae2wtlib.AE2wtlibForge;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AE2wtlibForge.class)
public class AE2wtlibForgeMixin {
    @Inject(
            method = "handle(Lnet/neoforged/neoforge/event/entity/living/LivingEntityUseItemEvent$Finish;)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void cancel(LivingEntityUseItemEvent.Finish event, CallbackInfo ci) {
        if (event.getEntity() instanceof FakePlayer) ci.cancel();
    }

    @Inject(
            method = "handle(Lnet/neoforged/neoforge/event/entity/player/PlayerInteractEvent$RightClickBlock;)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void cancel(PlayerInteractEvent.RightClickBlock event, CallbackInfo ci) {
        if (event.getEntity() instanceof FakePlayer) ci.cancel();
    }
}
