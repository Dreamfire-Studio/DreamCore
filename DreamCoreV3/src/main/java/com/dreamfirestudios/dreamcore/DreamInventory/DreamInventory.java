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

/**
 * Inventory utilities with Paper-friendly practices:
 * <ul>
 *   <li>No ItemStack as Map keys (mutable; equality is tricky). Use {@link ItemRef}.</li>
 *   <li>Durability via {@link Damageable} meta (no deprecated getDurability()).</li>
 *   <li>Null-safe scans, index checks, and partial removal helpers.</li>
 *   <li>Adventure-aware display name queries (supports both Component and plain text).</li>
 * </ul>
 *
 * Slot conventions:
 * <ul>
 *   <li>Backpack/hotbar use the raw {@link PlayerInventory#getContents()} index.</li>
 *   <li>Armor uses index 0..3 in the order returned by {@code getArmorContents()}.</li>
 *   <li>Main hand = {@value SLOT_MAIN_HAND}, Off hand = {@value SLOT_OFF_HAND}.</li>
 * </ul>
 */
public final class DreamInventory {

    private DreamInventory() {}

    /** Reserved slot index for main hand references in {@link ItemRef}. */
    public static final int SLOT_MAIN_HAND = -1;
    /** Reserved slot index for off hand references in {@link ItemRef}. */
    public static final int SLOT_OFF_HAND  = -2;

    // ---------------------------------------------------------------------
    // Scanning
    // ---------------------------------------------------------------------

    /**
     * Scans a living entity's equipment and returns item references with locations.
     * Armor slot indices are 0..3 in the order returned by {@link org.bukkit.inventory.EntityEquipment#getArmorContents()}.
     *
     * @param entity entity to scan (null returns empty list)
     */
    public static @NotNull List<ItemRef> scan(@Nullable LivingEntity entity) {
        final List<ItemRef> out = new ArrayList<>();
        if (entity == null || entity.getEquipment() == null) return out;

        // Armor
        final ItemStack[] armor = entity.getEquipment().getArmorContents();
        for (int i = 0; i < armor.length; i++) {
            final ItemStack s = armor[i];
            if (s != null) out.add(new ItemRef(ItemLocation.ENTITY_ARMOR, i, s));
        }

        // Hands (use reserved negative indices; Bukkit's EquipmentSlot has no public slot index)
        final ItemStack main = entity.getEquipment().getItemInMainHand();
        final ItemStack off  = entity.getEquipment().getItemInOffHand();
        if (main != null) out.add(new ItemRef(ItemLocation.ENTITY_MAIN_HAND, SLOT_MAIN_HAND, main));
        if (off  != null) out.add(new ItemRef(ItemLocation.ENTITY_OFF_HAND,  SLOT_OFF_HAND,  off));

        return out;
    }

    /**
     * Scans a player's full inventory (backpack + armor + hands) and returns item references.
     * Inventory slot indices are the raw Bukkit indices for {@link PlayerInventory#getContents()}.
     *
     * @param inv player inventory (null returns empty list)
     */
    public static @NotNull List<ItemRef> scan(@Nullable PlayerInventory inv) {
        final List<ItemRef> out = new ArrayList<>();
        if (inv == null) return out;

        // Main inventory/backpack (includes hotbar)
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

        // Hands (reserved negative indices)
        final ItemStack main = inv.getItemInMainHand();
        final ItemStack off  = inv.getItemInOffHand();
        if (main != null) out.add(new ItemRef(ItemLocation.ENTITY_MAIN_HAND, SLOT_MAIN_HAND, main));
        if (off  != null) out.add(new ItemRef(ItemLocation.ENTITY_OFF_HAND,  SLOT_OFF_HAND,  off));

        return out;
    }

    // ---------------------------------------------------------------------
    // Queries
    // ---------------------------------------------------------------------

    /** Counts total items similar to the probe across a player's inventory. */
    public static int totalCount(@NotNull PlayerInventory inv, @NotNull ItemStack probe) {
        Objects.requireNonNull(inv, "inventory");
        Objects.requireNonNull(probe, "itemStack");
        int total = 0;
        for (ItemStack s : inv.getContents()) {
            if (s != null && s.isSimilar(probe)) total += s.getAmount();
        }
        return total;
    }

    /**
     * Returns whether the item is "broken" by checking {@link Damageable} meta.
     * For unbreakable or non-damageable items, returns false.
     */
    public static boolean isBroken(@Nullable ItemStack item) {
        if (item == null) return false;
        final int max = item.getType().getMaxDurability();
        if (max <= 0) return false; // not damageable
        final ItemMeta meta = item.getItemMeta();
        if (!(meta instanceof Damageable dmg)) return false;
        return dmg.getDamage() >= max;
    }

    /**
     * Finds the first item whose display name equals the given plain text (case-insensitive).
     * Uses Adventure plain-text serialization of the display name component.
     */
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

    /** Finds the first item by comparing the plain text of a name component. */
    public static @Nullable ItemStack findByDisplayName(@NotNull PlayerInventory inv, @NotNull Component nameComponent) {
        final String plain = PlainTextComponentSerializer.plainText().serialize(nameComponent);
        return findByDisplayName(inv, plain);
    }

    /** Checks if the inventory has at least {@code amount} items similar to {@code probe}. */
    public static boolean hasAtLeast(@NotNull PlayerInventory inv, @NotNull ItemStack probe, int amount) {
        return totalCount(inv, probe) >= Math.max(0, amount);
    }

    // ---------------------------------------------------------------------
    // Mutations
    // ---------------------------------------------------------------------

    /**
     * Removes up to {@code amount} of items similar to {@code probe} from the inventory.
     *
     * @return how many items were actually removed
     */
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

    /** Removes all stacks similar to the given item (full clears of those entries). */
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

    /**
     * Adds an item, respecting available space. Returns true if fully added, false if partial or none.
     * (This uses Bukkit's stacking logic.)
     */
    public static boolean addItem(@NotNull PlayerInventory inv, @NotNull ItemStack stack) {
        Objects.requireNonNull(inv, "inventory");
        Objects.requireNonNull(stack, "itemStack");
        return inv.addItem(stack).isEmpty();
    }

    /** Swaps two slots if they are within bounds of the backing Inventory. */
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

    /** Puts an item in the player's main hand (replacing whatever is there) if it isn't already similar. */
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

    /** Finds first item matching a predicate. */
    public static @Nullable ItemStack findFirst(@NotNull PlayerInventory inv, @NotNull Predicate<ItemStack> predicate) {
        Objects.requireNonNull(inv, "inventory");
        Objects.requireNonNull(predicate, "predicate");
        for (ItemStack s : inv.getContents()) {
            if (s != null && predicate.test(s)) return s;
        }
        return null;
    }
}