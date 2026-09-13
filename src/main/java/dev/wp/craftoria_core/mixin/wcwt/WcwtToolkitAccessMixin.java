package dev.wp.craftoria_core.mixin.wcwt;

import appeng.menu.locator.ItemMenuHostLocator;
import com.lhy.wcwt.helpers.WcwtToolkitAccess;
import de.mari_023.ae2wtlib.api.registration.WTDefinition;
import de.mari_023.ae2wtlib.api.terminal.WUTHandler;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(WcwtToolkitAccess.class)
public class WcwtToolkitAccessMixin {
    /**
     * @author WP
     * @reason The original matches on WirelessComprehensiveWorkTerminalItem, so it never finds a WCWT
     * that has been combined into an ae2wtlib universal terminal. Defer to ae2wtlib's own lookup,
     * which resolves both the standalone item and the universal terminal's component.
     */
    @Overwrite
    public static ItemStack findTerminal(Player player) {
        ItemMenuHostLocator locator = WUTHandler.findTerminal(player, WTDefinition.of("wcwt"));
        return locator == null ? ItemStack.EMPTY : locator.locateItem(player);
    }
}
