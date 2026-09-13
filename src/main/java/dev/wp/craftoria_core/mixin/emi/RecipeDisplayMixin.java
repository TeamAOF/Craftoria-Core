package dev.wp.craftoria_core.mixin.emi;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.config.EmiConfig;
import dev.emi.emi.screen.RecipeDisplay;
import dev.emi.emi.screen.WidgetGroup;
import dev.wp.craftoria_core.mixin.wcwt.WcwtEmiPluginInvoker;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RecipeDisplay.class)
public class RecipeDisplayMixin {
    @Shadow @Final public EmiRecipe recipe;

    // wcwt ships its pull-items button as an EmiRecipeDecorator, but EMI only runs decorators under
    // dev.show-recipe-decorators, which is off for players. Run wcwt's decorator on its own rather
    // than enabling the flag, which would also turn on every other mod's debug decorators.
    @Inject(method = "getWidgets", at = @At("RETURN"))
    private void craftoriaCore$addWcwtPullItemsButton(
            int x,
            int y,
            int availableWidth,
            int availableHeight,
            CallbackInfoReturnable<WidgetGroup> cir
    ) {
        if (recipe != null && !EmiConfig.showRecipeDecorators) {
            WcwtEmiPluginInvoker.craftoriaCore$decoratePullItems(recipe, cir.getReturnValue());
        }
    }
}
