package dev.wp.craftoria_core.mixin.client.transmog;

import com.bawnorton.mixinsquared.TargetHandler;
import com.hidoni.transmog.RenderUtils;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Restriction(require = @Condition("transmog"))
@Mixin(value = Player.class, priority = 1500)
public class PlayerMixinCompatibility {
    @TargetHandler(
            mixin = "com.hidoni.transmog.mixin.PlayerMixin",
            name = "transmogItemBySlot"
    )
    @WrapMethod(method = "@MixinSquared:Handler")
    private void keepOriginalItemBySlotDuringGuiRender(
            EquipmentSlot slot,
            CallbackInfoReturnable<ItemStack> cir,
            Operation<Void> original
    ) {
        if (RenderUtils.isCalledForRendering() && RenderUtilsAccessor.getInventoryExcludedCount() > 0) {
            original.call(slot, cir);
        }
    }

    @TargetHandler(
            mixin = "com.hidoni.transmog.mixin.PlayerMixin",
            name = "transmogInventory"
    )
    @WrapMethod(method = "@MixinSquared:Handler")
    private void keepOriginalInventoryDuringGuiRender(
            CallbackInfoReturnable<Inventory> cir,
            Operation<Void> original
    ) {
        if (RenderUtils.isCalledForRendering() && RenderUtilsAccessor.getInventoryExcludedCount() > 0) {
            original.call(cir);
        }
    }
}
