package com.dreamfirestudios.dreamcore.DreamInventory;

import org.bukkit.inventory.ItemStack;

/**
 * Immutable reference to an item found during a scan.
 * @param location logical location (main hand, off hand, armor, inventory, etc.)
 * @param slot     numeric slot index (armor indexes are implementation-defined; see scan methods)
 * @param stack    the item stack (never null)
 */
public record ItemRef(ItemLocation location, int slot, ItemStack stack) {}