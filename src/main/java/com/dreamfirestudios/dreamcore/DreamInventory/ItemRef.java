package com.dreamfirestudios.dreamcore.DreamInventory;

import org.bukkit.inventory.ItemStack;

/// <summary>
/// Immutable reference to an item found during an inventory scan.
/// </summary>
/// <param name="location">Logical location (inventory, armor, hand, etc.).</param>
/// <param name="slot">Numeric slot index (armor indices are implementation-defined).</param>
/// <param name="stack">Non-null item stack reference.</param>
/// <remarks>
/// Used to avoid using mutable <see cref="ItemStack"/> directly as keys.
/// </remarks>
/// <example>
/// <code>
/// for (ItemRef ref : DreamInventory.scan(player.getInventory())) {
///     System.out.println("Found " + ref.stack().getType() + " at " + ref.location());
/// }
/// </code>
/// </example>
public record ItemRef(ItemLocation location, int slot, ItemStack stack) {}