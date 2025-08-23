/*
 * MIT License
 *
 * Copyright (c) 2025 Dreamfire Studio
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.dreamfirestudios.dreamcore.DreamInventory;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

/// <summary>
/// Utility class providing safe and Paper-friendly inventory manipulation helpers.
/// </summary>
/// <remarks>
/// <list type="bullet">
///   <item><description>Uses <see cref="ItemRef"/> for safe references instead of mutable <see cref="ItemStack"/> as keys.</description></item>
///   <item><description>Respects durability via <see cref="Damageable"/> instead of deprecated APIs.</description></item>
///   <item><description>Supports null-safety, partial removals, and Adventure text serialization.</description></item>
/// </list>
/// Slot conventions:
/// <list type="bullet">
///   <item><description>Backpack/hotbar use raw <c>PlayerInventory#getContents()</c> indices.</description></item>
///   <item><description>Armor uses indices 0–3 as returned by <c>getArmorContents()</c>.</description></item>
///   <item><description>Main hand = <see cref="SLOT_MAIN_HAND"/>, Off hand = <see cref="SLOT_OFF_HAND"/>.</description></item>
/// </list>
/// </remarks>
/// <example>
/// <code>
/// // Count arrows in a player's inventory
/// int arrows = DreamInventory.totalCount(player.getInventory(), new ItemStack(Material.ARROW));
/// </code>
/// </example>
public final class DreamInventory {

    private DreamInventory() {}

    /// <summary>Reserved slot index for main hand references in <see cref="ItemRef"/>.</summary>
    public static final int SLOT_MAIN_HAND = -1;

    /// <summary>Reserved slot index for off hand references in <see cref="ItemRef"/>.</summary>
    public static final int SLOT_OFF_HAND  = -2;

    // ---------------------------------------------------------------------
    // Scanning
    // ---------------------------------------------------------------------

    /// <summary>
    /// Scans a living entity’s equipment (armor + hands) and returns logical item references.
    /// </summary>
    /// <param name="entity">Entity to scan (null returns empty list).</param>
    /// <returns>List of <see cref="ItemRef"/>s representing equipped items.</returns>
    /// <remarks>Armor slot indices are 0–3 following Bukkit’s <c>getArmorContents()</c> ordering.</remarks>
    public static @NotNull List<ItemRef> scan(@Nullable LivingEntity entity) {
        final List<ItemRef> out = new ArrayList<>();
        if (entity == null || entity.getEquipment() == null) return out;

        // Armor
        final ItemStack[] armor = entity.getEquipment().getArmorContents();
        for (int i = 0; i < armor.length; i++) {
            final ItemStack s = armor[i];
            if (s != null) out.add(new ItemRef(ItemLocation.ENTITY_ARMOR, i, s));
        }

        // Hands
        final ItemStack main = entity.getEquipment().getItemInMainHand();
        final ItemStack off  = entity.getEquipment().getItemInOffHand();
        if (main != null) out.add(new ItemRef(ItemLocation.ENTITY_MAIN_HAND, SLOT_MAIN_HAND, main));
        if (off  != null) out.add(new ItemRef(ItemLocation.ENTITY_OFF_HAND,  SLOT_OFF_HAND,  off));

        return out;
    }

    /// <summary>
    /// Scans a player’s full inventory (backpack + armor + hands).
    /// </summary>
    /// <param name="inv">Player inventory (null returns empty list).</param>
    /// <returns>List of <see cref="ItemRef"/>s representing items.</returns>
    /// <remarks>Backpack slot indices match <c>getContents()</c> order.</remarks>
    public static @NotNull List<ItemRef> scan(@Nullable PlayerInventory inv) {
        final List<ItemRef> out = new ArrayList<>();
        if (inv == null) return out;

        // Backpack
        final ItemStack[] contents = inv.getContents();
        for (int slot = 0; slot < contents.length; slot++) {
            final ItemStack s = contents[slot];
            if (s != null) out.add(new ItemRef(ItemLocation.ENTITY_INVENTORY, slot, s));
        }

        // Armor
        final ItemStack[] armor = inv.getArmorContents();
        for (int i = 0; i < armor.length; i++) {
            final ItemStack s = armor[i];
            if (s != null) out.add(new ItemRef(ItemLocation.ENTITY_ARMOR, i, s));
        }

        // Hands
        final ItemStack main = inv.getItemInMainHand();
        final ItemStack off  = inv.getItemInOffHand();
        if (main != null) out.add(new ItemRef(ItemLocation.ENTITY_MAIN_HAND, SLOT_MAIN_HAND, main));
        if (off  != null) out.add(new ItemRef(ItemLocation.ENTITY_OFF_HAND,  SLOT_OFF_HAND,  off));

        return out;
    }

    // ---------------------------------------------------------------------
    // Queries
    // ---------------------------------------------------------------------

    /// <summary>
    /// Counts total items similar to the probe across a player’s inventory.
    /// </summary>
    /// <param name="inv">Inventory to search.</param>
    /// <param name="probe">Item prototype for similarity checks.</param>
    /// <returns>Total count of matching items.</returns>
    public static int totalCount(@NotNull PlayerInventory inv, @NotNull ItemStack probe) {
        Objects.requireNonNull(inv, "inventory");
        Objects.requireNonNull(probe, "itemStack");
        int total = 0;
        for (ItemStack s : inv.getContents()) {
            if (s != null && s.isSimilar(probe)) total += s.getAmount();
        }
        return total;
    }

    /// <summary>
    /// Determines whether the item is fully broken (at max durability).
    /// </summary>
    /// <param name="item">Item to test.</param>
    /// <returns>True if broken; false otherwise.</returns>
    /// <remarks>Non-damageable or unbreakable items always return false.</remarks>
    public static boolean isBroken(@Nullable ItemStack item) {
        if (item == null) return false;
        final int max = item.getType().getMaxDurability();
        if (max <= 0) return false;
        final ItemMeta meta = item.getItemMeta();
        if (!(meta instanceof Damageable dmg)) return false;
        return dmg.getDamage() >= max;
    }

    /// <summary>
    /// Finds the first item whose display name matches the given plain text.
    /// </summary>
    /// <param name="inv">Inventory to scan.</param>
    /// <param name="plainName">Target plain string name (case-insensitive).</param>
    /// <returns>First matching item, or null if none.</returns>
    /// <remarks>Uses Adventure <c>PlainTextComponentSerializer</c> for comparison.</remarks>
    public static @Nullable ItemStack findByDisplayName(@NotNull PlayerInventory inv, @NotNull String plainName) {
        Objects.requireNonNull(inv, "inventory");
        Objects.requireNonNull(plainName, "plainName");
        final String target = plainName.trim();
        for (ItemStack s : inv.getContents()) {
            if (s == null) continue;
            final ItemMeta meta = s.getItemMeta();
            if (meta == null || !meta.hasDisplayName()) continue;
            final Component name = meta.displayName();
            if (name == null) continue;
            final String plain = PlainTextComponentSerializer.plainText().serialize(name);
            if (plain.equalsIgnoreCase(target)) return s;
        }
        return null;
    }

    /// <summary>
    /// Finds the first item matching the given component display name.
    /// </summary>
    /// <param name="inv">Inventory to scan.</param>
    /// <param name="nameComponent">Component name to match.</param>
    /// <returns>First matching item, or null if none.</returns>
    public static @Nullable ItemStack findByDisplayName(@NotNull PlayerInventory inv, @NotNull Component nameComponent) {
        final String plain = PlainTextComponentSerializer.plainText().serialize(nameComponent);
        return findByDisplayName(inv, plain);
    }

    /// <summary>
    /// Checks if the inventory has at least <paramref name="amount"/> of the probe item.
    /// </summary>
    /// <param name="inv">Inventory to check.</param>
    /// <param name="probe">Probe item for similarity.</param>
    /// <param name="amount">Minimum count required.</param>
    /// <returns>True if threshold met; false otherwise.</returns>
    public static boolean hasAtLeast(@NotNull PlayerInventory inv, @NotNull ItemStack probe, int amount) {
        return totalCount(inv, probe) >= Math.max(0, amount);
    }

    // ---------------------------------------------------------------------
    // Mutations
    // ---------------------------------------------------------------------

    /// <summary>
    /// Removes up to a certain amount of matching items from the inventory.
    /// </summary>
    /// <param name="inv">Inventory to mutate.</param>
    /// <param name="probe">Probe item for similarity.</param>
    /// <param name="amount">Desired removal count.</param>
    /// <returns>Actual number removed.</returns>
    public static int removeAmount(@NotNull PlayerInventory inv, @NotNull ItemStack probe, int amount) {
        Objects.requireNonNull(inv, "inventory");
        Objects.requireNonNull(probe, "itemStack");
        int toRemove = Math.max(0, amount);
        if (toRemove == 0) return 0;

        ItemStack[] contents = inv.getContents();
        for (int slot = 0; slot < contents.length && toRemove > 0; slot++) {
            ItemStack s = contents[slot];
            if (s == null || !s.isSimilar(probe)) continue;

            int take = Math.min(s.getAmount(), toRemove);
            s.setAmount(s.getAmount() - take);
            toRemove -= take;

            if (s.getAmount() <= 0) contents[slot] = null;
        }
        inv.setContents(contents);
        return amount - toRemove;
    }

    /// <summary>
    /// Removes all stacks similar to the probe.
    /// </summary>
    /// <param name="inv">Inventory to mutate.</param>
    /// <param name="probe">Probe item for similarity.</param>
    /// <returns>Total number of items removed.</returns>
    public static int removeAllSimilar(@NotNull PlayerInventory inv, @NotNull ItemStack probe) {
        Objects.requireNonNull(inv, "inventory");
        Objects.requireNonNull(probe, "itemStack");
        int removed = 0;
        ItemStack[] contents = inv.getContents();
        for (int slot = 0; slot < contents.length; slot++) {
            ItemStack s = contents[slot];
            if (s != null && s.isSimilar(probe)) {
                removed += s.getAmount();
                contents[slot] = null;
            }
        }
        inv.setContents(contents);
        return removed;
    }

    /// <summary>
    /// Attempts to add an item to inventory.
    /// </summary>
    /// <param name="inv">Target inventory.</param>
    /// <param name="stack">Stack to insert.</param>
    /// <returns>True if fully added, false if partial or rejected.</returns>
    public static boolean addItem(@NotNull PlayerInventory inv, @NotNull ItemStack stack) {
        Objects.requireNonNull(inv, "inventory");
        Objects.requireNonNull(stack, "itemStack");
        return inv.addItem(stack).isEmpty();
    }

    /// <summary>
    /// Swaps two slots in an inventory.
    /// </summary>
    /// <param name="inv">Inventory to mutate.</param>
    /// <param name="slot1">First slot index.</param>
    /// <param name="slot2">Second slot index.</param>
    /// <exception cref="IllegalArgumentException">Thrown if indices are out of bounds.</exception>
    public static void swap(@NotNull Inventory inv, int slot1, int slot2) {
        Objects.requireNonNull(inv, "inventory");
        final int size = inv.getSize();
        if (slot1 < 0 || slot1 >= size || slot2 < 0 || slot2 >= size) {
            throw new IllegalArgumentException("Invalid slot(s): " + slot1 + ", " + slot2 + " (size=" + size + ")");
        }
        ItemStack a = inv.getItem(slot1);
        ItemStack b = inv.getItem(slot2);
        inv.setItem(slot1, b);
        inv.setItem(slot2, a);
    }

    /// <summary>
    /// Ensures the player’s main hand contains the given stack (if not already similar).
    /// </summary>
    /// <param name="player">Target player.</param>
    /// <param name="stack">Stack to equip.</param>
    public static void ensureMainHand(@NotNull Player player, @NotNull ItemStack stack) {
        Objects.requireNonNull(player, "player");
        Objects.requireNonNull(stack, "itemStack");
        ItemStack current = player.getInventory().getItemInMainHand();
        if (current != null && current.isSimilar(stack)) return;
        player.getInventory().setItemInMainHand(stack);
    }

    // ---------------------------------------------------------------------
    // Search helpers
    // ---------------------------------------------------------------------

    /// <summary>
    /// Finds the first item in an inventory matching a predicate.
    /// </summary>
    /// <param name="inv">Inventory to search.</param>
    /// <param name="predicate">Predicate applied to each non-null stack.</param>
    /// <returns>First matching item, or null.</returns>
    /// <typeparam name="T">Always <see cref="ItemStack"/>, provided for generic search pattern consistency.</typeparam>
    /// <example>
    /// <code>
    /// ItemStack pickaxe = DreamInventory.findFirst(inv, s -> s.getType() == Material.DIAMOND_PICKAXE);
    /// </code>
    /// </example>
    public static @Nullable ItemStack findFirst(@NotNull PlayerInventory inv, @NotNull Predicate<ItemStack> predicate) {
        Objects.requireNonNull(inv, "inventory");
        Objects.requireNonNull(predicate, "predicate");
        for (ItemStack s : inv.getContents()) {
            if (s != null && predicate.test(s)) return s;
        }
        return null;
    }
}