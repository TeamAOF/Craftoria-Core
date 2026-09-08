package dev.wp.craftoria_core.mixin.extendedae_plus;

import com.extendedae_plus.network.OpenCraftFromJeiC2SPacket;
import com.extendedae_plus.util.wireless.WirelessTerminalLocator;
import com.llamalad7.mixinextras.sugar.Local;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = OpenCraftFromJeiC2SPacket.class, remap = false)
public class OpenCraftFromJeiC2SPacketMixin {
    @Inject(
            method = "lambda$handle$2",
            at = @At(
                    value = "INVOKE_ASSIGN",
                    target = "Lcom/extendedae_plus/util/wireless/WirelessTerminalLocator;find(Lnet/minecraft/world/entity/player/Player;)Lcom/extendedae_plus/util/wireless/WirelessTerminalLocator$LocatedTerminal;"
            ),
            cancellable = true
    )
    private static void guardMissingWirelessTerminal(IPayloadContext ctx, OpenCraftFromJeiC2SPacket msg, CallbackInfo ci, @Local(name = "located") WirelessTerminalLocator.LocatedTerminal located) {
        if (located == null || located.isEmpty()) ci.cancel();
    }
}
