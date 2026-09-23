package dev.wp.craftoria_core.mixin.gametechbcsspellbooks;

import com.gametechbc.gametechbcsspellbooks.effects.Reversal.ReversalEffectHandler;
import net.minecraft.world.entity.projectile.Projectile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;
import java.util.UUID;

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
