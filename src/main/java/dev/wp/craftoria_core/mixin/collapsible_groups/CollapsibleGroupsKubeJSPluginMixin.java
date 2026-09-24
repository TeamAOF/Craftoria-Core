package dev.wp.craftoria_core.mixin.collapsible_groups;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.starskyxiii.collapsible_groups.compat.kubejs.CollapsibleGroupsKubeJSPlugin;
import dev.latvian.mods.kubejs.recipe.viewer.RecipeViewerEntryType;
import dev.latvian.mods.rhino.type.TypeInfo;
import io.netty.buffer.ByteBuf;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.network.codec.StreamCodec;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.function.Consumer;

@Restriction(require = {@Condition("collapsible_groups"), @Condition("kubejs")})
@Mixin(CollapsibleGroupsKubeJSPlugin.class)
public class CollapsibleGroupsKubeJSPluginMixin {
    // Collapsible Groups registers placeholder entry types with null entry/predicate components,
    // NPEing anything that walks RecipeViewerEntryType.ALL_TYPES (ProbeJS's type dump).
    @Unique
    private static final RecipeViewerEntryType.Component<Object> craftoriaCore$placeholder = new RecipeViewerEntryType.Component<>(
            TypeInfo.OBJECT,
            StreamCodec.<ByteBuf, Object>of((buf, value) -> {}, buf -> null),
            value -> false
    );

    @WrapMethod(method = "registerRecipeViewerEntryTypes")
    private void fillNullEntryTypeComponents(Consumer<RecipeViewerEntryType> consumer, Operation<Void> original) {
        original.call((Consumer<RecipeViewerEntryType>) type -> consumer.accept(
                type.entryType == null || type.predicateType == null
                        ? new RecipeViewerEntryType(type.id, craftoriaCore$placeholder, craftoriaCore$placeholder, type.baseClass)
                        : type
        ));
    }
}
