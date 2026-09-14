package dev.wp.craftoria_core.mixin.jdt;

import com.direwolf20.justdirethings.common.blockentities.basebe.FilterableBE;
import com.direwolf20.justdirethings.common.containers.handlers.FilterBasicHandler;
import com.direwolf20.justdirethings.common.entities.CreatureCatcherEntity;
import com.direwolf20.justdirethings.common.items.CreatureCatcher;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

// Port of https://github.com/Direwolf20-MC/JustDireThings/pull/490
@Mixin(FilterableBE.class)
public interface FilterableBEMixin extends FilterableBE {
    /**
     * @author WP
     * @reason ItemEntity was never checked against the item filter, so it always fell through to the allowlist default
     */
    @Overwrite
    default boolean isEntityValidFilter(Entity entity, Level level) {
        if (getFilterData().entityCache.containsKey(entity)) {
            return getFilterData().entityCache.get(entity);
        }

        FilterBasicHandler filteredItems = getFilterHandler();
        for (int i = 0; i < filteredItems.getSlots(); i++) {
            ItemStack stack = filteredItems.getStackInSlot(i);
            if (stack.isEmpty()) continue;

            if (stack.getItem() instanceof SpawnEggItem) {
                Item entityEgg = SpawnEggItem.byId(entity.getType());
                if (entityEgg != null && stack.is(entityEgg)) {
                    getFilterData().entityCache.put(entity, getFilterData().allowlist);
                    return getFilterData().allowlist;
                }
            } else if (stack.getItem() instanceof CreatureCatcher) {
                Mob mob = CreatureCatcherEntity.getEntityFromItemStack(stack, level);
                if (mob != null && entity.getType().equals(mob.getType())) {
                    if (!getFilterData().compareNBT) {
                        getFilterData().entityCache.put(entity, getFilterData().allowlist);
                        return getFilterData().allowlist;
                    }

                    CompoundTag filterTag = getNormalizedTag(mob);
                    CompoundTag targetTag = getNormalizedTag(entity);
                    if (filterTag.equals(targetTag)) {
                        getFilterData().entityCache.put(entity, getFilterData().allowlist);
                        return getFilterData().allowlist;
                    }
                }
            }
        }

        if (entity instanceof ItemEntity itemEntity) {
            getFilterData().entityCache.put(entity, isStackValidFilter(itemEntity.getItem()));
            return getFilterData().entityCache.get(entity);
        }

        getFilterData().entityCache.put(entity, !getFilterData().allowlist);
        return !getFilterData().allowlist;
    }
}
