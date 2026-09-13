package dev.wp.craftoria_core.mixin.relics;

import com.google.common.collect.Lists;
import it.hurts.sskirillss.relics.config.RelicsConfigData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(RelicsConfigData.class) public class RelicsConfigMixin {

    @Inject(method = "getRingOfSDSGluttonyAttributesBlacklist", at = @At("HEAD"), cancellable = true) public void craftoria$addGluttonyAttributes(CallbackInfoReturnable<List<String>> cir) {
        List<String> list = Lists.newArrayList(
                "minecraft:friction_modifier",
                "minecraft:air_drag_modifier",
                "minecraft:generic.gravity",
                "ars_nouveau:ars_nouveau.perk.weight", "ars_nouveau:ars_nouveau.perk.wixie", "caelus:fall_flying",
                "minecraft:generic.scale", "minecraft:generic.burning_time", "minecraft:generic.movement_speed",
                "minecraft:generic.step_height", "minecraft:generic.jump_strength", "minecraft:player.sneaking_speed", "minecraft:player.block_interaction_range",
                "minecraft:player.entity_interaction_range", "additionalentityattributes:generic.width", "additionalentityattributes:generic.height",
                "additionalentityattributes:generic.hitbox_scale", "additionalentityattributes:generic.hitbox_width",
                "additionalentityattributes:generic.hitbox_height", "additionalentityattributes:generic.model_scale",
                "additionalentityattributes:generic.model_width", "additionalentityattributes:generic.model_height",
                "additionalentityattributes:generic.water_speed"
        );
        cir.setReturnValue(list);
    }
}
