package dev.wp.craftoria_core.mixin.extendedae;

import appeng.api.implementations.blockentities.PatternContainerGroup;
import com.glodblock.github.extendedae.client.gui.GuiExPatternTerminal;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

// ExtendedAE reimplements the terminal screen rather than extending AE2's, so it needs the same
// live-name refresh as PatternAccessTermScreenGroupNameMixin.
@Restriction(
        require = {
                @Condition("extendedae")
        }
)
@Mixin(GuiExPatternTerminal.class)
public class GuiExPatternTerminalGroupNameMixin {
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
