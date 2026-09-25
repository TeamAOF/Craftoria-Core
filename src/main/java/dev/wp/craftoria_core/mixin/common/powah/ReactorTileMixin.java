package dev.wp.craftoria_core.mixin.common.powah;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import owmii.powah.block.reactor.ReactorTile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Restriction(require = @Condition("powah"))
@Mixin(ReactorTile.class)
public class ReactorTileMixin {

    @ModifyExpressionValue(
            method = "processTemperature",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/OptionalInt;orElse(I)I"
            )
    )
    private int craftoria_core$invertColdness(int original) {
        return -original;
    }
}
