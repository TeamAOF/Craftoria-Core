package dev.wp.craftoria_core.mixin.minecraft;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.authlib.GameProfile;
import dev.wp.craftoria_core.util.SdlinkPlayerLimit;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Restriction(require = @Condition("sdlink"))
@Mixin(PlayerList.class)
public class PlayerListMixin {
    @ModifyExpressionValue(
            method = "canPlayerLogin",
            at = @At(value = "INVOKE", target = "Ljava/util/List;size()I")
    )
    private int countRegularPlayers(int original) {
        return SdlinkPlayerLimit.countRegularPlayers(((PlayerList) (Object) this).getPlayers().stream()
                .map(ServerPlayer::getUUID)
                .toList());
    }

    @Inject(method = "canBypassPlayerLimit", at = @At("HEAD"), cancellable = true)
    private void allowSdlinkBypass(GameProfile profile, CallbackInfoReturnable<Boolean> cir) {
        if (SdlinkPlayerLimit.hasBypassRole(profile)) cir.setReturnValue(true);
    }
}
