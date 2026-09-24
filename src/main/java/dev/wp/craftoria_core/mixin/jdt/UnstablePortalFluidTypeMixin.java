package dev.wp.craftoria_core.mixin.jdt;

import com.direwolf20.justdirethings.common.fluids.unstableportalfluid.UnstablePortalFluidType;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Restriction(
        require = {
                @Condition("justdirethings")
        }
)
@Mixin(UnstablePortalFluidType.class)
public class UnstablePortalFluidTypeMixin {
    @Inject(method = "isVaporizedOnPlacement", at = @At("HEAD"), cancellable = true)
    public void dontVaporiseInEnd(Level level, BlockPos pos, FluidStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (level.dimension() == Level.END) cir.setReturnValue(false);
    }
}
