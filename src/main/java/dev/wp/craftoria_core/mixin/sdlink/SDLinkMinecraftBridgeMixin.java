package dev.wp.craftoria_core.mixin.sdlink;

import com.hypherionmc.sdlink.server.SDLinkMinecraftBridge;
import com.hypherionmc.sdlink.server.ServerEvents;
import dev.wp.craftoria_core.util.SdlinkPlayerLimit;
import org.apache.commons.lang3.tuple.Pair;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = SDLinkMinecraftBridge.class, remap = false)
public class SDLinkMinecraftBridgeMixin {
    @Inject(method = "getPlayerCounts", at = @At("RETURN"), cancellable = true)
    private void countRegularPlayers(CallbackInfoReturnable<Pair<Integer, Integer>> cir) {
        Pair<Integer, Integer> original = cir.getReturnValue();
        int regularPlayers = SdlinkPlayerLimit.countRegularPlayers(
                ServerEvents.getInstance().getMinecraftServer().getPlayers().stream()
                        .map(player -> player.getGameProfile().getId())
                        .toList());
        cir.setReturnValue(Pair.of(regularPlayers, original.getRight()));
    }
}
