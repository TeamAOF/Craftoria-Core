package dev.wp.craftoria_core.mixin.common.gametechbcsspellbooks;

import com.gametechbc.gametechbcsspellbooks.effects.Reversal.ReversalEffect;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Collection;
import java.util.List;

@Restriction(require = @Condition("gametechbcs_spellbooks"))
@Mixin(value = ReversalEffect.class, remap = false)
public class ReversalEffectMixin {
    // Reversal clears harmful effects while iterating getActiveEffects() itself -> CME. Iterate a copy.
    @Redirect(
            method = {"reflectProjectile", "performRaycastAndApplyDamage"},
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;getActiveEffects()Ljava/util/Collection;"),
            remap = false
    )
    private Collection<MobEffectInstance> copyActiveEffects(Player player) {
        return List.copyOf(player.getActiveEffects());
    }
}
