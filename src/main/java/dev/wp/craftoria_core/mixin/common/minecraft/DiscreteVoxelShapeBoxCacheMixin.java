package dev.wp.craftoria_core.mixin.common.minecraft;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.world.phys.shapes.BitSetDiscreteVoxelShape;
import net.minecraft.world.phys.shapes.DiscreteVoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.ArrayList;
import java.util.List;

/**
 * Memoizes BitSetDiscreteVoxelShape.forAllBoxes per shape identity - it recomputes from scratch on every call otherwise.
 * Added because Ars Nouveau's MobJarTile raycasts the same multiblock shape 8x/tick, tanking FPS.
 */
@Mixin(BitSetDiscreteVoxelShape.class)
public abstract class DiscreteVoxelShapeBoxCacheMixin {

    @Unique
    private static final Cache<DiscreteVoxelShape, List<int[]>> craftoriaCore$boxCache =
            CacheBuilder.newBuilder().weakKeys().build();

    @WrapMethod(method = "forAllBoxes")
    private static void cacheBoxes(DiscreteVoxelShape shape, DiscreteVoxelShape.IntLineConsumer consumer, boolean combine, Operation<Void> original) {
        if (!combine) {
            original.call(shape, consumer, combine);
            return;
        }

        List<int[]> cached = craftoriaCore$boxCache.getIfPresent(shape);
        if (cached != null) {
            for (int[] box : cached) {
                consumer.consume(box[0], box[1], box[2], box[3], box[4], box[5]);
            }
            return;
        }

        List<int[]> recorded = new ArrayList<>();
        DiscreteVoxelShape.IntLineConsumer recording = (x1, y1, z1, x2, y2, z2) -> {
            recorded.add(new int[]{x1, y1, z1, x2, y2, z2});
            consumer.consume(x1, y1, z1, x2, y2, z2);
        };
        original.call(shape, recording, true);
        craftoriaCore$boxCache.put(shape, recorded);
    }
}
