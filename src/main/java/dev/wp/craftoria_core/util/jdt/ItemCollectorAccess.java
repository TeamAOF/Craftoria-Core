package dev.wp.craftoria_core.util.jdt;

import net.neoforged.neoforge.items.IItemHandler;

// exposes ItemCollectorBE's private getAttachedInventory() across mixins
public interface ItemCollectorAccess {
    IItemHandler craftoriaCore$getAttachedInventory();
}
