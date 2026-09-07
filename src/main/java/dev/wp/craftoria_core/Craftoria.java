package dev.wp.craftoria_core;

import dev.wp.craftoria_core.ae2.AE2Init;
import dev.wp.craftoria_core.config.ServerConfig;
import dev.wp.craftoria_core.util.Utils;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(Craftoria.ID)
public class Craftoria {
    public static final String ID = "craftoria_core";
    public static final String NAME = "Craftoria Core";

    public static final Logger LOGGER = LoggerFactory.getLogger(NAME);

    public Craftoria(ModContainer container, IEventBus bus, Dist dist) {
        container.registerConfig(ModConfig.Type.SERVER, ServerConfig.SPEC);
        bus.addListener(this::onConfigReload);
//        init(bus);
//
//        if (dist.isClient()) container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    private void onConfigReload(ModConfigEvent event) {
        if (event instanceof ModConfigEvent.Unloading) return;
        if (event.getConfig().getType() == ModConfig.Type.SERVER) ServerConfig.init();
    }

    public static ResourceLocation id(@NotNull String path) {
        return ResourceLocation.fromNamespaceAndPath(ID, path);
    }

    private void init(IEventBus bus) {
        CItems.init(bus);
        CComponents.init(bus);
        CCreativeTab.init(bus);

        if (Utils.isModLoaded("ae2")) AE2Init.init(bus);
    }
}
