package dev.wp.craftoria_core.mixin.common.extendedae_plus;

import com.extendedae_plus.content.matrix.supermatrix.SuperAssemblerMatrixFrameBlock;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.LevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// isFrame probes neighbours 1 block out and force-loads the chunk; deadlocks the server thread when
// a shape update fires inside a chunk-unload callback (AE2 grid teardown -> setBlock -> updateShape).
@Restriction(require = @Condition("extendedae_plus"))
@Mixin(value = SuperAssemblerMatrixFrameBlock.class, remap = false)
public class SuperMatrixFrameChunkLoadGuardMixin {
    @Inject(method = "isFrame", at = @At("HEAD"), cancellable = true)
    private static void avoidChunkLoad(LevelAccessor level, int x, int y, int z, CallbackInfoReturnable<Boolean> cir) {
        if (level instanceof ServerLevel serverLevel && !serverLevel.getChunkSource().hasChunk(x >> 4, z >> 4)) {
            cir.setReturnValue(false);
        }
    }
}
