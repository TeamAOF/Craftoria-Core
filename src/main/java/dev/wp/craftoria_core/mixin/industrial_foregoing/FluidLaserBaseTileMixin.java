package dev.wp.craftoria_core.mixin.industrial_foregoing;

import com.buuz135.industrial.block.resourceproduction.tile.FluidLaserBaseTile;
import com.buuz135.industrial.block.resourceproduction.tile.ILaserBase;
import com.buuz135.industrial.block.tile.IndustrialMachineTile;
import com.buuz135.industrial.recipe.data.EntityData;
import com.hollingsworth.arsnouveau.common.block.tile.MobJarTile;
import com.hrznstudio.titanium.module.BlockWithTile;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

/** Ars Nouveau mob jar support for the fluid laser's entity target. */
@Mixin(FluidLaserBaseTile.class)
public abstract class FluidLaserBaseTileMixin extends IndustrialMachineTile<FluidLaserBaseTile> implements ILaserBase<FluidLaserBaseTile> {
    public FluidLaserBaseTileMixin(BlockWithTile basicTileBlock, BlockPos blockPos, BlockState blockState) {
        super(basicTileBlock, blockPos, blockState);
    }

    @Unique
    private LivingEntity craftoria$jarredCandidate;

    @ModifyExpressionValue(
            method = "onWork",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/Level;getEntitiesOfClass(Ljava/lang/Class;Lnet/minecraft/world/phys/AABB;Ljava/util/function/Predicate;)Ljava/util/List;"))
    private List<LivingEntity> craftoria$useMobJarEntity(List<LivingEntity> found, @Local(name = "entityData") EntityData entityData) {
        craftoria$jarredCandidate = null;
        if (!(this.level.getBlockEntity(this.worldPosition.below()) instanceof MobJarTile jar)) return found;

        Entity jarred = jar.getEntity();
        if (!(jarred instanceof LivingEntity livingEntity) || !entityData.getEntity().test(livingEntity)) return found;

        craftoria$jarredCandidate = livingEntity;
        return List.of(livingEntity);
    }

    @Redirect(method = "onWork", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/LivingEntity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"))
    private boolean craftoria$skipHurtForJarredEntity(LivingEntity entity, DamageSource source, float amount) {
        return entity == craftoria$jarredCandidate || entity.hurt(source, amount);
    }
}
