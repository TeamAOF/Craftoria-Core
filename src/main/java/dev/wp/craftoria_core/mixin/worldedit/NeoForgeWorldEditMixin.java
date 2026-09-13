package dev.wp.craftoria_core.mixin.worldedit;

import com.sk89q.worldedit.neoforge.NeoForgeWorldEdit;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.util.FakePlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(NeoForgeWorldEdit.class)
public abstract class NeoForgeWorldEditMixin {

    @Inject(
            method = "skipInteractionEvent(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;)Z",
            at = @At("RETURN"),
            cancellable = true
    )
    private void skipInteractionEventMixin(
            Player player,
            InteractionHand hand,
            CallbackInfoReturnable<Boolean> cir
    ) {
        if (player instanceof FakePlayer) {
            if (cir != null) cir.setReturnValue(true);
        }
    }
}
