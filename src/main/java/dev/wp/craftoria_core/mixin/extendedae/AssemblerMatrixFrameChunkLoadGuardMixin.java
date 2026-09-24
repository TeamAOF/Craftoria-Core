package dev.wp.craftoria_core.mixin.extendedae;

import com.glodblock.github.extendedae.common.blocks.matrix.BlockAssemblerMatrixFrame;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.LevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// Same deadlock as extendedae_plus.SuperMatrixFrameChunkLoadGuardMixin; ExtendedAE-Plus copied this
// class verbatim, so the base Assembler Matrix force-loads chunks from updateShape too.
@Restriction(require = @Condition("extendedae"))
@Mixin(value = BlockAssemblerMatrixFrame.class, remap = false)
public class AssemblerMatrixFrameChunkLoadGuardMixin {
    @Inject(method = "isFrame", at = @At("HEAD"), cancellable = true)
    private static void avoidChunkLoad(LevelAccessor level, int x, int y, int z, CallbackInfoReturnable<Boolean> cir) {
        if (level instanceof ServerLevel serverLevel && !serverLevel.getChunkSource().hasChunk(x >> 4, z >> 4)) {
            cir.setReturnValue(false);
        }
    }
}
