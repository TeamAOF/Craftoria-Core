package dev.wp.craftoria_core.mixin.transmog;

import com.hidoni.transmog.RenderUtils;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Restriction(require = @Condition("transmog"))
@Mixin(RenderUtils.class)
public interface RenderUtilsAccessor {
    @Accessor("inventoryExcludedCount")
    static int getInventoryExcludedCount() {
        throw new AssertionError();
    }
}
