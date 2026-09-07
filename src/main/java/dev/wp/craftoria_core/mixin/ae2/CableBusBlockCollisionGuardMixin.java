package dev.wp.craftoria_core.mixin.ae2;

import appeng.block.networking.CableBusBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CableBusBlock.class)
public class CableBusBlockCollisionGuardMixin {
    @Inject(method = "getShape", at = @At("HEAD"), cancellable = true)
    private void avoidChunkLoad(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context,
                                CallbackInfoReturnable<VoxelShape> cir) {
        if (level instanceof ServerLevel serverLevel
                && !serverLevel.getChunkSource().hasChunk(pos.getX() >> 4, pos.getZ() >> 4)) {
            cir.setReturnValue(Shapes.empty());
        }
    }
}
