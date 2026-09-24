package dev.wp.craftoria_core.mixin.buildinggadgets2;

import com.direwolf20.buildinggadgets2.common.worlddata.BG2Data;
import com.direwolf20.buildinggadgets2.util.datatypes.StatePos;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.nbt.CompoundTag;
import org.spongepowered.asm.mixin.Mixin;

import java.util.ArrayList;

@Restriction(require = @Condition("buildinggadgets2"))
@Mixin(BG2Data.class)
public class BuildingGadgetsDataSaveGuard {
    @WrapMethod(method = "statePosListToNBTMapArray")
    private static CompoundTag save(ArrayList<StatePos> list, Operation<CompoundTag> original) {
        try {
            return original.call(list);
        } catch (RuntimeException ignored) {
            return new CompoundTag();
        }
    }
}
