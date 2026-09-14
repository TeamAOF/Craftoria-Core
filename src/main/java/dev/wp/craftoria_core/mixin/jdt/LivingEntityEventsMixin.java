package dev.wp.craftoria_core.mixin.jdt;

import com.direwolf20.justdirethings.common.blockentities.ItemCollectorBE;
import com.direwolf20.justdirethings.common.events.LivingEntityEvents;
import dev.wp.craftoria_core.util.jdt.ItemCollectorAccess;
import dev.wp.craftoria_core.util.jdt.ItemCollectorPositions;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

// Pickup half of https://github.com/Direwolf20-MC/JustDireThings/pull/389, see ItemCollectorBEMixin
@Mixin(LivingEntityEvents.class)
public class LivingEntityEventsMixin {
    @Inject(method = "blockJoin", at = @At("TAIL"))
    private static void craftoriaCore$instantPickup(EntityJoinLevelEvent e, CallbackInfo ci) {
        if (!(e.getEntity() instanceof ItemEntity itemEntity)) return;

        Level level = e.getLevel();
        Optional<ItemCollectorBE> found = ItemCollectorPositions.find(level, itemEntity.position());
        if (found.isEmpty()) return;

        ItemCollectorBE collector = found.get();
        IItemHandler handler = ((ItemCollectorAccess) collector).craftoriaCore$getAttachedInventory();
        if (handler == null) return;

        if (collector.respectPickupDelay && itemEntity.hasPickUpDelay()) return;

        ItemStack stack = itemEntity.getItem();
        if (stack.isEmpty() || !collector.isStackValidFilter(stack)) return;

        ItemStack leftover = ItemHandlerHelper.insertItemStacked(handler, stack, false);
        if (leftover.isEmpty()) {
            e.setCanceled(true);
        } else {
            itemEntity.setItem(leftover);
        }
    }
}
