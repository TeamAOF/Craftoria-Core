package dev.wp.craftoria_core.mixin.transmog;

import com.hidoni.transmog.RenderUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(RenderUtils.class)
public interface RenderUtilsAccessor {
    @Accessor("inventoryExcludedCount")
    static int craftoriaCore$getInventoryExcludedCount() {
        throw new AssertionError();
    }
}
