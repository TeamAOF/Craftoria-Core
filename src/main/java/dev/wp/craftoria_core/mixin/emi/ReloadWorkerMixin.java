package dev.wp.craftoria_core.mixin.emi;

import dev.wp.craftoria_core.Craftoria;
import dev.wp.craftoria_core.util.CreativeSearchOrder;
import dev.wp.craftoria_core.util.Utils;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "dev.emi.emi.runtime.EmiReloadManager$ReloadWorker")
public class ReloadWorkerMixin {

    @Inject(method = "run", at = @At("HEAD"))
    private void onRunHead(CallbackInfo ci) {
        Utils.updateBlockedByEmi = true;
        Utils.emiReloading = true;
    }

    @Inject(method = "run", at = @At("RETURN"))
    private void onRunReturn(CallbackInfo ci) {
        if (Utils.updateBlockedByEmi) {
            Minecraft.getInstance().execute(() -> {
                Craftoria.LOGGER.debug("EMI reload finished; updating search order");
                CreativeSearchOrder.scheduleUpdateIfNeeded();
            });
        }
        Utils.emiReloading = false;
    }
}
