package dev.wp.craftoria_core.mixin.common.extendedae_plus;

import com.extendedae_plus.content.matrix.supermatrix.SuperAssemblerMatrixBlockEntity;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Restriction(require = @Condition("extendedae_plus"))
@Mixin(value = SuperAssemblerMatrixBlockEntity.class, remap = false)
public class SuperMatrixShapeUpdateGuardMixin {
    @ModifyArg(
            method = "eap$updateSuperMatrixStatus",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/Level;setBlock(Lnet/minecraft/core/BlockPos;"
                            + "Lnet/minecraft/world/level/block/state/BlockState;I)Z"
            ),
            index = 2
    )
    private int avoidShapeUpdate(int flags) {
        return flags | Block.UPDATE_KNOWN_SHAPE;
    }
}
