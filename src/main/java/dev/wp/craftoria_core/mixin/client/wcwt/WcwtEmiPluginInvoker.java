package dev.wp.craftoria_core.mixin.client.wcwt;

import com.lhy.wcwt.compat.emi.WcwtEmiPlugin;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.widget.WidgetHolder;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Restriction(
        require = {
                @Condition(value = "wcwt", versionPredicates = "(,1.3.10]"),
                @Condition("emi")
        }
)
@Mixin(WcwtEmiPlugin.class)
public interface WcwtEmiPluginInvoker {
    @Invoker("decoratePullItems")
    static void decoratePullItems(EmiRecipe recipe, WidgetHolder widgets) {
        throw new AssertionError();
    }
}
