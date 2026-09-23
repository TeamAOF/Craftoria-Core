package dev.wp.craftoria_core.util;

import dev.wp.craftoria_core.mixin.gametechbcsspellbooks.ReversalEffectHandlerAccessor;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.EntityLeaveLevelEvent;

// Reversal's static UUID->damage/projectile maps are only cleared on the happy path, so they grow forever
// and keep dead projectiles alive. Drop an entity's entries when it leaves the level.
public class ReversalLeakFix {
    @SubscribeEvent
    public static void onEntityLeaveLevel(EntityLeaveLevelEvent event) {
        if (event.getLevel().isClientSide || !(event.getEntity() instanceof LivingEntity entity)) return;
        ReversalEffectHandlerAccessor.storedDamage().remove(entity.getUUID());
        ReversalEffectHandlerAccessor.lastProjectileHit().remove(entity.getUUID());
    }
}
