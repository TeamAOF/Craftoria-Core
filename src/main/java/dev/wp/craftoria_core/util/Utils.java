package dev.wp.craftoria_core.util;

import mekanism.api.MekanismAPI;
import mekanism.api.chemical.Chemical;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.LoadingModList;
import net.neoforged.fml.loading.moddiscovery.ModInfo;

import java.util.List;

public class Utils {
    public static volatile boolean emiReloading = false;
    public static volatile boolean updateBlockedByEmi = false;

    private final static int fluidOffset = Integer.MAX_VALUE / 1000;
    private final static int gasOffset = fluidOffset * 10;

    public static int getItemPosition(ItemStack item) {
        if (emiReloading) return BuiltInRegistries.ITEM.getId(item.getItem());
        else return CreativeSearchOrder.getItemIndex(item);
    }

    public static int getFluidPos(Fluid fluid) {
        return fluidOffset + BuiltInRegistries.FLUID.getId(fluid);
    }

    public static int getChemicalPos(Chemical chemical) {
        return gasOffset + MekanismAPI.CHEMICAL_REGISTRY.getId(chemical);
    }

    public static boolean isModLoaded(String modId) {
        return ModList.get().isLoaded(modId);
    }

    public static List<String> getModList() {
        return LoadingModList.get().getMods().stream().map(ModInfo::getModId).toList();
    }
}
