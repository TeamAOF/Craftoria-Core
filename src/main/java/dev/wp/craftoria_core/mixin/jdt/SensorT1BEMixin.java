package dev.wp.craftoria_core.mixin.jdt;

import com.direwolf20.justdirethings.common.blockentities.SensorT1BE;
import com.direwolf20.justdirethings.common.blockentities.basebe.BaseMachineBE;
import com.direwolf20.justdirethings.common.blockentities.basebe.FilterableBE;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

// Port of https://github.com/Direwolf20-MC/JustDireThings/pull/490
@Mixin(SensorT1BE.class)
public abstract class SensorT1BEMixin extends BaseMachineBE implements FilterableBE {
    protected SensorT1BEMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Shadow
    public SensorT1BE.SENSE_TARGET sense_target;

    /**
     * @author WP
     * @reason LIVING never narrowed past "any entity"; ITEM never checked the item filter
     */
    @Overwrite
    public boolean isValidEntity(Entity entity) {
        if (sense_target.equals(SensorT1BE.SENSE_TARGET.HOSTILE) && !(entity instanceof Monster)) {
            return false;
        }
        if ((sense_target.equals(SensorT1BE.SENSE_TARGET.PASSIVE) || sense_target.equals(SensorT1BE.SENSE_TARGET.ADULT) || sense_target.equals(SensorT1BE.SENSE_TARGET.CHILD)) && !(entity instanceof Animal)) {
            return false;
        }

        if (sense_target.equals(SensorT1BE.SENSE_TARGET.ADULT) && entity instanceof Animal animal && animal.isBaby()) {
            return false;
        }
        if (sense_target.equals(SensorT1BE.SENSE_TARGET.CHILD) && entity instanceof Animal animal && !animal.isBaby()) {
            return false;
        }
        if (sense_target.equals(SensorT1BE.SENSE_TARGET.PLAYER) && !(entity instanceof Player)) {
            return false;
        }
        if (sense_target.equals(SensorT1BE.SENSE_TARGET.ITEM) && !(entity instanceof ItemEntity)) {
            return false;
        }
        if (sense_target.equals(SensorT1BE.SENSE_TARGET.LIVING) && !(entity instanceof Player) && !(entity instanceof Animal) && !(entity instanceof Monster)) {
            return false;
        }

        return isEntityValidFilter(entity, level);
    }
}
