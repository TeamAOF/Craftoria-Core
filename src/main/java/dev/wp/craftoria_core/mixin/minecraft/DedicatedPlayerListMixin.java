package dev.wp.craftoria_core.mixin.minecraft;

import com.mojang.authlib.GameProfile;
import dev.wp.craftoria_core.util.SdlinkPlayerLimit;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.server.dedicated.DedicatedPlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Restriction(
        require = {
                @Condition("sdlink")
        }
)
@Mixin(DedicatedPlayerList.class)
public class DedicatedPlayerListMixin {
    @Inject(method = "canBypassPlayerLimit", at = @At("HEAD"), cancellable = true)
    private void allowSdlinkBypass(GameProfile profile, CallbackInfoReturnable<Boolean> cir) {
        if (SdlinkPlayerLimit.hasBypassRole(profile)) cir.setReturnValue(true);
    }
}
