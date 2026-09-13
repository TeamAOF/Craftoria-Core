package dev.wp.craftoria_core.mixin.animusnv;

import com.breakinblocks.animusnv.advancements.AltarTierAdvancementHandler;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.event.level.BlockEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AltarTierAdvancementHandler.class)
public class AltarTierAdvancementHandlerMixin {
    @Inject(
            method = "onBlockPlaced",
            at = @At("HEAD"),
            cancellable = true)
    private static void onBlockPlaced(BlockEvent.EntityPlaceEvent event, CallbackInfo ci) {
        if (event.getEntity() instanceof FakePlayer) ci.cancel();
    }
}
