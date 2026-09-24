package dev.wp.craftoria_core.mixin.gametechbcsspellbooks;

import com.gametechbc.gametechbcsspellbooks.effects.Reversal.ReversalEffectHandler;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.world.entity.projectile.Projectile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;
import java.util.UUID;

@Restriction(require = @Condition("gametechbcs_spellbooks"))
@Mixin(value = ReversalEffectHandler.class, remap = false)
public interface ReversalEffectHandlerAccessor {
    @Accessor("storedDamageMap")
    static Map<UUID, Float> storedDamage() {
        throw new AssertionError();
    }

    @Accessor("lastProjectileHitMap")
    static Map<UUID, Projectile> lastProjectileHit() {
        throw new AssertionError();
    }
}
