package dev.wp.craftoria_core.mixin.wcwt;

import com.lhy.wcwt.compat.emi.WcwtEmiPlugin;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.widget.WidgetHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(WcwtEmiPlugin.class)
public interface WcwtEmiPluginInvoker {
    @Invoker("decoratePullItems")
    static void craftoriaCore$decoratePullItems(EmiRecipe recipe, WidgetHolder widgets) {
        throw new AssertionError();
    }
}
