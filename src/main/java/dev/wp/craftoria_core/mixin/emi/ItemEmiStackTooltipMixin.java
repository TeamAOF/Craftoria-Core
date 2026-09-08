package dev.wp.craftoria_core.mixin.emi;

import dev.emi.emi.api.stack.ItemEmiStack;
import dev.wp.craftoria_core.util.EmiTooltipCache;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@SuppressWarnings("UnstableApiUsage")
@Mixin(ItemEmiStack.class)
public abstract class ItemEmiStackTooltipMixin {
    @Shadow
    public abstract ItemStack getItemStack();

    @Inject(method = "getTooltipText", at = @At("HEAD"), cancellable = true)
    private void craftoria$useCachedTooltip(CallbackInfoReturnable<List<Component>> cir) {
        try {
            List<Component> cached = EmiTooltipCache.get(getItemStack());
            if (cached != null) {
                cir.setReturnValue(cached);
            }
        } catch (RuntimeException ignored) {
            // Keep EMI's original tooltip path as the fallback.
        }
    }

    @Inject(method = "getTooltipText", at = @At("RETURN"), cancellable = true)
    private void craftoria$cacheTooltip(CallbackInfoReturnable<List<Component>> cir) {
        try {
            ItemStack stack = getItemStack();
            cir.setReturnValue(EmiTooltipCache.put(stack, cir.getReturnValue()));
        } catch (RuntimeException ignored) {
            // Keep EMI's original return value if caching fails.
        }
    }
}
