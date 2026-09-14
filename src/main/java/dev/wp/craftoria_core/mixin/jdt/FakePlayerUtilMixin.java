package dev.wp.craftoria_core.mixin.jdt;

import com.direwolf20.justdirethings.util.FakePlayerUtil;
import com.direwolf20.justdirethings.util.UsefulFakePlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// Port of https://github.com/Direwolf20-MC/JustDireThings/pull/530
@Mixin(FakePlayerUtil.class)
public class FakePlayerUtilMixin {
    // fake player is teleported, not ticked, so onGround/fallDistance never get set by physics
    @Inject(
            method = "processUseEntity",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/direwolf20/justdirethings/util/UsefulFakePlayer;attack(Lnet/minecraft/world/entity/Entity;)V"
            ))
    private static void craftoriaCore$forceGroundedBeforeAttack(UsefulFakePlayer player, Level world, Entity entity, HitResult result, FakePlayerUtil.InteractionType action, CallbackInfoReturnable<Boolean> cir) {
        player.setOnGround(true);
        player.fallDistance = 0;
    }
}
