package dev.wp.craftoria_core.mixin.ae2;

import appeng.block.AEBaseEntityBlock;
import appeng.blockentity.AEBaseBlockEntity;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.BlockGetter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// Level#getBlockEntity blocking-loads the chunk, so AE2 shape/collision queries can force a sync load from
// inside a chunk unload (CableBusBlockEntity#onChunkUnloaded -> CablePart#markForUpdate), which deadlocks
// the server thread under C2ME. Callers already treat null as "nothing here".
@Restriction(require = @Condition("ae2"))
@Mixin(value = AEBaseEntityBlock.class, remap = false)
public class AEBaseEntityBlockChunkLoadGuardMixin {
    @Inject(
            method = "getBlockEntity(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;)"
                    + "Lappeng/blockentity/AEBaseBlockEntity;",
            at = @At("HEAD"),
            cancellable = true
    )
    private void avoidChunkLoad(BlockGetter level, BlockPos pos, CallbackInfoReturnable<AEBaseBlockEntity> cir) {
        if (level instanceof ServerLevel serverLevel
                && serverLevel.getChunkSource().getChunkNow(pos.getX() >> 4, pos.getZ() >> 4) == null) {
            cir.setReturnValue(null);
        }
    }
}
