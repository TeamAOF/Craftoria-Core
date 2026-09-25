package dev.wp.craftoria_core.mixin.client.wcwt;

import com.lhy.wcwt.client.WcwtToolkitHud;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Restriction(require = @Condition("wcwt"))
@Mixin(WcwtToolkitHud.class)
public class WcwtToolkitHudVisibilityMixin {
    @Inject(
            method = "isHudVisible",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void checkForTheDamnGUIFirst(Minecraft minecraft, CallbackInfoReturnable<Boolean> cir) {
        if (minecraft.options.hideGui) cir.setReturnValue(false);
    }
}
