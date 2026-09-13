package dev.wp.craftoria_core.mixin.wcwt;

import com.lhy.wcwt.helpers.WcwtToolkitHotbarState;
import com.lhy.wcwt.helpers.WcwtToolkitHotbarState.Bar;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.util.FakePlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WcwtToolkitHotbarState.class)
public class WcwtToolkitHotbarStateMixin {
    @Inject(
            method = "hasToolkitCard",
            at=@At("HEAD"),
            cancellable = true
    )
    private static void cancelToolkitCard(Player player, CallbackInfoReturnable<Boolean> cir) {
        if (player instanceof FakePlayer) cir.setReturnValue(false);
    }

    /**
     * @author WP
     * @reason Test the cheap selected-bar first. The original ran hasToolkitCard, which scans every
     * curios slot and the whole inventory, before the bar check that makes it moot for most players.
     */
    @Overwrite
    public static boolean isToolkitSelected(Player player) {
        return WcwtToolkitHotbarState.getBar(player) != Bar.CENTER && WcwtToolkitHotbarState.hasToolkitCard(player);
    }
}
