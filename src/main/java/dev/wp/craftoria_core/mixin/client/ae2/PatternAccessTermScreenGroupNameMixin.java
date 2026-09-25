package dev.wp.craftoria_core.mixin.client.ae2;

import appeng.api.implementations.blockentities.PatternContainerGroup;
import appeng.client.gui.me.patternaccess.PatternAccessTermScreen;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

// The group name is a server snapshot, so animated name colors freeze; re-derive it from the live
// icon stack each frame. Pairs with PatternAccessTermGroupEqualityMixin.
@Restriction(require = @Condition("ae2"))
@Mixin(PatternAccessTermScreen.class)
public class PatternAccessTermScreenGroupNameMixin {
    @ModifyExpressionValue(
            method = "drawFG",
            at = @At(
                    value = "INVOKE",
                    target = "Lappeng/api/implementations/blockentities/PatternContainerGroup;name()Lnet/minecraft/network/chat/Component;"
            )
    )
    private Component refreshNameStyle(Component name, @Local(name = "group") PatternContainerGroup group) {
        if (group.icon() == null) {
            return name;
        }
        Component live = group.icon().getReadOnlyStack().getHoverName();
        // Only when the group is named after its machine, never over a player-given custom name.
        return live.getString().equals(name.getString()) ? live : name;
    }
}
