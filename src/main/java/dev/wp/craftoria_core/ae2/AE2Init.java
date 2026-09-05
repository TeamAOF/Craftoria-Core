package dev.wp.craftoria_core.ae2;

import appeng.api.storage.StorageCells;
import dev.wp.craftoria_core.ae2.compat.AppFluxItems;
import dev.wp.craftoria_core.ae2.compat.AppMekItems;
import dev.wp.craftoria_core.ae2.init.AE2Components;
import dev.wp.craftoria_core.ae2.init.AE2Items;
import dev.wp.craftoria_core.ae2.item.cell.BlackHoleCellItem;
import dev.wp.craftoria_core.util.Utils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

public class AE2Init {
    public static void init(IEventBus bus) {
        AE2Items.init();
        AE2Components.init();

        if (Utils.isModLoaded("appflux")) AppFluxItems.init();
        if (Utils.isModLoaded("appmek")) AppMekItems.init();

        bus.addListener(AE2Init::initStorageCells);
    }

    private static void initStorageCells(FMLCommonSetupEvent event) {
        StorageCells.addCellHandler(BlackHoleCellItem.HANDLER);
    }
}
