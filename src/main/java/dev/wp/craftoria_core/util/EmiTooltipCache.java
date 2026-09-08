package dev.wp.craftoria_core.util;

import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public final class EmiTooltipCache {
    private static final ConcurrentHashMap<Key, List<Component>> CACHE = new ConcurrentHashMap<>();

    private EmiTooltipCache() {
    }

    public static List<Component> get(ItemStack stack) {
        return CACHE.get(new Key(stack.getItem(), stack.getComponentsPatch()));
    }

    public static List<Component> put(ItemStack stack, List<Component> tooltip) {
        List<Component> immutable = List.copyOf(tooltip);
        CACHE.put(new Key(stack.getItem(), stack.getComponentsPatch()), immutable);
        return immutable;
    }

    public static void clear() {
        CACHE.clear();
    }

    private record Key(Item item, DataComponentPatch components) {
    }
}
