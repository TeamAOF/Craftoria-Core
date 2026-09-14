package dev.wp.craftoria_core.util.jdt;

import com.direwolf20.justdirethings.common.blockentities.ItemCollectorBE;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;

import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

// ItemCollectorBE positions per dimension, for LivingEntityEventsMixin's instant-pickup lookup.
public final class ItemCollectorPositions {
    private ItemCollectorPositions() {
    }

    private static final Map<ResourceKey<Level>, Set<BlockPos>> POSITIONS = new ConcurrentHashMap<>();

    public static void add(Level level, BlockPos pos) {
        POSITIONS.computeIfAbsent(level.dimension(), k -> ConcurrentHashMap.newKeySet()).add(pos);
    }

    public static Optional<ItemCollectorBE> find(Level level, Vec3 vec) {
        Set<BlockPos> positions = POSITIONS.get(level.dimension());
        if (positions == null) return Optional.empty();

        Iterator<BlockPos> iterator = positions.iterator();
        while (iterator.hasNext()) {
            BlockPos pos = iterator.next();
            if (!level.isLoaded(pos)) continue;

            BlockEntity be = level.getBlockEntity(pos);
            if (!(be instanceof ItemCollectorBE collector)) {
                iterator.remove();
                continue;
            }

            if (collector.getAABB(pos).contains(vec) && collector.isActiveRedstone()) {
                return Optional.of(collector);
            }
        }

        return Optional.empty();
    }
}
