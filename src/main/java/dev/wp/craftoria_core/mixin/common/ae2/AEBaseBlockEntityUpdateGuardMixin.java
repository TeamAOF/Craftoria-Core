package dev.wp.craftoria_core.mixin.common.ae2;

import appeng.blockentity.AEBaseBlockEntity;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// AE2's own notLoaded() guard uses hasChunkAt, which still passes for a chunk that is mid-unload, so the
// block update re-enters the chunk system; see AEBaseEntityBlockChunkLoadGuardMixin.
@Restriction(require = @Condition("ae2"))
@Mixin(value = AEBaseBlockEntity.class, remap = false)
public class AEBaseBlockEntityUpdateGuardMixin {
    @Inject(method = "markForUpdate", at = @At("HEAD"), cancellable = true)
    private void avoidChunkLoad(CallbackInfo ci) {
        BlockEntity self = (BlockEntity) (Object) this;
        if (self.getLevel() instanceof ServerLevel serverLevel) {
            var pos = self.getBlockPos();
            if (serverLevel.getChunkSource().getChunkNow(pos.getX() >> 4, pos.getZ() >> 4) == null) {
                ci.cancel();
            }
        }
    }
}
