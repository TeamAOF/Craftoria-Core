package dev.wp.craftoria_core.mixin.common.jdt;

import com.direwolf20.justdirethings.common.entities.CreatureCatcherEntity;
import com.llamalad7.mixinextras.sugar.Local;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Restriction(require = @Condition("justdirethings"))
@Mixin(CreatureCatcherEntity.class)
public class CreatureCatcherEntityMixin {
    @Inject(method = "canCapture", at = @At(value = "RETURN"), cancellable = true)
    private void onCanCapture(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        if (entity.getTags().contains("being_captured")) cir.setReturnValue(false);
        else entity.addTag("being_captured");
    }

    @Inject(method = "releaseEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;moveTo(Lnet/minecraft/world/phys/Vec3;)V"))
    private void onReleaseEntity(ItemStack itemStack, CallbackInfo ci, @Local Entity entity) {
        entity.removeTag("being_captured");
    }
}
