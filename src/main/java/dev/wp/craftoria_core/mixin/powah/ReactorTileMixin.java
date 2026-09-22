package dev.wp.craftoria_core.mixin.powah;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import owmii.powah.block.reactor.ReactorTile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

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
