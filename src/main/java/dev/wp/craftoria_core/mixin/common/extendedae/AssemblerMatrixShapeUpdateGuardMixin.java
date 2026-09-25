package dev.wp.craftoria_core.mixin.common.extendedae;

import com.glodblock.github.extendedae.common.tileentities.matrix.TileAssemblerMatrixBase;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Restriction(require = @Condition("extendedae"))
@Mixin(value = TileAssemblerMatrixBase.class, remap = false)
public class AssemblerMatrixShapeUpdateGuardMixin {
    @ModifyArg(
            method = "updateSubType",
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
