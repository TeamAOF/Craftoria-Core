package dev.wp.craftoria_core.mixin.curios;

import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.event.level.BlockDropsEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.theillusivec4.curios.common.event.CuriosEventHandler;

@Restriction(
        require = {
                @Condition("curios")
        }
)
@Mixin(CuriosEventHandler.class)
public class CuriosEventHandlerMixin {
    @Inject(method = "onBreakBlock", at = @At("HEAD"), cancellable = true)
    private void onBreakBlock(BlockDropsEvent event, CallbackInfo ci) {
        if (event.getBreaker() instanceof FakePlayer) ci.cancel();
    }
}
