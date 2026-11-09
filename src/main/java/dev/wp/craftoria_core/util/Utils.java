package dev.wp.craftoria_core.util;

import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.LoadingModList;
import net.neoforged.fml.loading.moddiscovery.ModInfo;

import java.util.List;

public class Utils {
    public static boolean isModLoaded(String modId) {
        return ModList.get().isLoaded(modId);
    }

    public static List<String> getModList() {
        return LoadingModList.get().getMods().stream().map(ModInfo::getModId).toList();
    }
}
