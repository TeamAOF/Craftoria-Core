package dev.wp.craftoria_core.mixin.client.ae2;

import appeng.api.stacks.AEKey;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import dev.wp.craftoria_core.ae2.KeySorters;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Comparator;

@Restriction(
        require = {
                @Condition("ae2"),
                @Condition("emi")
        }
)
@Mixin(targets = "appeng.client.gui.me.common.KeySorters")
abstract class KeySortersMixin {
    @ModifyExpressionValue(
            method = {"<clinit>"},
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/Comparator;comparing(Ljava/util/function/Function;Ljava/util/Comparator;)Ljava/util/Comparator;",
                    ordinal = 1
            )
    )
    private static Comparator<AEKey> compareByNumberID(Comparator<AEKey> original) {
        return KeySorters.comparator();
    }
}
