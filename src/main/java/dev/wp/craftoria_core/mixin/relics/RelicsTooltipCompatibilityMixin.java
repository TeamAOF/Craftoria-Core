package dev.wp.craftoria_core.mixin.relics;

import com.bawnorton.mixinsquared.TargetHandler;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(value = Item.class, priority = 1500)
public class RelicsTooltipCompatibilityMixin {
    @TargetHandler(
            mixin = "it.hurts.sskirillss.relics.mixin.ItemMixin",
            name = "appendHoverText"
    )
    @WrapMethod(method = "@MixinSquared:Handler")
    private void disableRelicsTooltip(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag, CallbackInfo ci, Operation<Void> original) {
        try {
            original.call(stack, context, tooltip, flag, ci);
        } catch (NoSuchMethodError ignored) {
        }
    }
}
