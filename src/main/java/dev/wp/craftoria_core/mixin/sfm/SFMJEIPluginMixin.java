package dev.wp.craftoria_core.mixin.sfm;

import ca.teamdman.sfm.client.jei.FallingAnvilDisenchantRecipe;
import ca.teamdman.sfm.client.jei.FallingAnvilExperienceShardRecipe;
import ca.teamdman.sfm.client.jei.SFMJEIPlugin;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IRecipeRegistration;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(SFMJEIPlugin.class)
public class SFMJEIPluginMixin {
    // The disenchant/experience-shard falling anvil recipes are unconditionally registered here
    // regardless of whether the pack still allows them; filter them out before JEI/EMI ever sees
    // the recipe entry, instead of just cancelling their layout after the fact.
    @Redirect(
            method = "registerRecipes",
            at = @At(
                    value = "INVOKE",
                    target = "Lmezz/jei/api/registration/IRecipeRegistration;addRecipes(Lmezz/jei/api/recipe/RecipeType;Ljava/util/List;)V",
                    ordinal = 1
            )
    )
    private void craftoriaCore$hideDisabledFallingAnvilRecipes(IRecipeRegistration registration, RecipeType<Object> type, List<Object> recipes) {
        recipes.removeIf(recipe -> recipe instanceof FallingAnvilDisenchantRecipe || recipe instanceof FallingAnvilExperienceShardRecipe);
        registration.addRecipes(type, recipes);
    }
}
