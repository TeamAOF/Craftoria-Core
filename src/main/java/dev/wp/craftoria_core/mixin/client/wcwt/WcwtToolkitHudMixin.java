package dev.wp.craftoria_core.mixin.client.wcwt;

import com.lhy.wcwt.client.WcwtToolkitHud;
import com.lhy.wcwt.helpers.WcwtToolkitHotbarState;
import com.lhy.wcwt.helpers.WcwtToolkitHotbarState.Bar;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Restriction(require = @Condition(value = "wcwt", versionPredicates = "(,1.3.9]"))
@Mixin(WcwtToolkitHud.class)
public class WcwtToolkitHudMixin {
    @Final @Shadow
    private static Bar[] CYCLE_ORDER;

    @Shadow
    private static void setSelection(Bar bar, int slot) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    private static boolean isHudVisible(Minecraft minecraft) {
        throw new UnsupportedOperationException();
    }

    /**
     * @author WP
     * @reason The original ran at default priority and canceled every in-world scroll whenever a
     * toolkit card was merely carried, with no regard for other mods on the same bus. Drop it to
     * LOWEST and bail out if the event is already canceled, so it stops stealing scroll input other
     * mods handled first.
     */
    @Overwrite
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onMouseScroll(InputEvent.MouseScrollingEvent event) {
        if (event.isCanceled()) return;

        Minecraft minecraft = Minecraft.getInstance();
        if (isHudVisible(minecraft) && !minecraft.player.isSpectator()) {
            int delta = (int) Math.signum(event.getScrollDeltaY() != 0.0 ? event.getScrollDeltaY() : -event.getScrollDeltaX());
            if (delta != 0) {
                LocalPlayer player = minecraft.player;
                int barIndex = 1;

                for (int i = 0; i < CYCLE_ORDER.length; i++) {
                    if (CYCLE_ORDER[i] == WcwtToolkitHotbarState.getBar(player)) {
                        barIndex = i;
                        break;
                    }
                }

                int index = Math.floorMod(barIndex * 9 + WcwtToolkitHotbarState.getSlot(player) - delta, 27);
                setSelection(CYCLE_ORDER[index / 9], index % 9);
                event.setCanceled(true);
            }
        }
    }
}
