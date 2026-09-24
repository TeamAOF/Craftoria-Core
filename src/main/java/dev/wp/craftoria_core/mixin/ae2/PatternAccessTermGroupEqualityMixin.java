package dev.wp.craftoria_core.mixin.ae2;

import appeng.api.implementations.blockentities.PatternContainerGroup;
import appeng.api.stacks.AEItemKey;
import appeng.menu.implementations.PatternAccessTermMenu;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;
import java.util.Objects;

// Compare groups by text only; mods that animate an item name's color (Mekanism Extras cosmic/infinite
// tiers) otherwise make the group differ every tick, forcing a full resync that resets terminal scroll.
@Restriction(
        require = {
                @Condition("ae2")
        }
)
@Mixin(PatternAccessTermMenu.class)
public class PatternAccessTermGroupEqualityMixin {
    @WrapOperation(
            method = "visitPatternProviderHosts",
            at = @At(
                    value = "INVOKE",
                    target = "Lappeng/api/implementations/blockentities/PatternContainerGroup;equals(Ljava/lang/Object;)Z"
            )
    )
    private boolean ignoreNameStyle(PatternContainerGroup known, Object o, Operation<Boolean> original) {
        if (original.call(known, o)) {
            return true;
        }
        return o instanceof PatternContainerGroup(AEItemKey icon, Component name, List<Component> tooltip)
                && Objects.equals(known.icon(), icon)
                && craftoriaCore$sameText(known.name(), name)
                && craftoriaCore$sameText(known.tooltip(), tooltip);
    }

    @Unique
    private static boolean craftoriaCore$sameText(Component a, Component b) {
        return a.getString().equals(b.getString());
    }

    @Unique
    private static boolean craftoriaCore$sameText(List<Component> a, List<Component> b) {
        if (a.size() != b.size()) {
            return false;
        }
        for (int i = 0; i < a.size(); i++) {
            if (!craftoriaCore$sameText(a.get(i), b.get(i))) {
                return false;
            }
        }
        return true;
    }
}
