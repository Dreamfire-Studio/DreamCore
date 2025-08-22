package com.dreamfirestudios.dreamcore.DreamItems;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import java.util.*;
import java.util.function.Function;

/**
 * Utilities for building, comparing and counting custom items.
 */
public final class DreamItemStacks {
    private DreamItemStacks() {}

    /** PDC key used to store the optional ID from {@link IDreamItemStack#id()}. */
    public static NamespacedKey keyId(Plugin plugin) {
        return new NamespacedKey(plugin, "dream_item_id");
    }

    /* --------------------------------------------------------------------- */
    /* Build                                                                  */
    /* --------------------------------------------------------------------- */

    /** Builds an ItemStack from a definition and writes its id (if any) to PDC. */
    public static ItemStack build(Plugin plugin, IDreamItemStack def) {
        Objects.requireNonNull(plugin, "plugin");
        Objects.requireNonNull(def, "definition");

        ItemStack stack = new ItemStack(def.type(), Math.max(1, def.amount()));
        ItemMeta meta = stack.getItemMeta();

        // Name
        Component name = def.displayName();
        if (name != null) meta.displayName(name);

        // Lore
        List<Component> lore = def.lore();
        if (lore != null && !lore.isEmpty()) meta.lore(lore);

        // Custom model data
        def.customModelData().ifPresent(meta::setCustomModelData);

        // Unbreakable, flags
        meta.setUnbreakable(def.unbreakable());
        Set<?> flags = def.flags();
        if (flags != null && !flags.isEmpty()) {
            meta.addItemFlags(def.flags().toArray(new org.bukkit.inventory.ItemFlag[0]));
        }

        // Attributes
        Map<?, ?> attrs = def.attributeModifiers();
        if (attrs != null && !attrs.isEmpty()) {
            def.attributeModifiers().forEach((attr, mods) -> {
                if (attr == null || mods == null) return;
                for (var mod : mods) if (mod != null) meta.addAttributeModifier(attr, mod);
            });
        }

        // PDC
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        def.id().ifPresent(id -> pdc.set(keyId(plugin), PersistentDataType.STRING, id));
        def.writePdc(plugin, pdc);

        stack.setItemMeta(meta);

        // Enchantments last
        Map<Enchantment, Integer> ench = def.enchantments();
        if (ench != null && !ench.isEmpty()) {
            ench.forEach((e, lvl) -> {
                if (e != null && lvl != null && lvl > 0) stack.addUnsafeEnchantment(e, lvl);
            });
        }

        return stack;
    }

    /* --------------------------------------------------------------------- */
    /* Resolve                                                                */
    /* --------------------------------------------------------------------- */

    /**
     * Reads the stored item id from PDC, if present.
     */
    public static Optional<String> readId(Plugin plugin, ItemStack stack) {
        if (stack == null || stack.getType() == Material.AIR) return Optional.empty();
        ItemMeta meta = stack.getItemMeta();
        if (meta == null) return Optional.empty();
        String id = meta.getPersistentDataContainer().get(keyId(plugin), PersistentDataType.STRING);
        return Optional.ofNullable(id);
    }

    /**
     * Resolve a definition by PDC id using a registry lookup.
     * If no id is present, returns empty.
     */
    public static Optional<IDreamItemStack> resolveById(
            Plugin plugin,
            ItemStack stack,
            Function<String, IDreamItemStack> registryLookup
    ) {
        Objects.requireNonNull(registryLookup, "registryLookup");
        return readId(plugin, stack).map(registryLookup);
    }

    /**
     * Resolve by id against a collection of defs (linear scan).
     */
    public static Optional<IDreamItemStack> resolveById(
            Plugin plugin,
            ItemStack stack,
            Collection<IDreamItemStack> registry
    ) {
        Optional<String> id = readId(plugin, stack);
        if (id.isEmpty()) return Optional.empty();
        String key = id.get();
        for (IDreamItemStack def : registry) {
            if (def != null && def.id().isPresent() && def.id().get().equals(key)) return Optional.of(def);
        }
        return Optional.empty();
    }

    /* --------------------------------------------------------------------- */
    /* Equality / counting                                                    */
    /* --------------------------------------------------------------------- */

    /**
     * Safer equality check:
     * 1) If both have a PDC id, compare ids.
     * 2) Otherwise falls back to {@link ItemStack#isSimilar(ItemStack)}.
     */
    public static boolean isSame(Plugin plugin, ItemStack a, ItemStack b) {
        if (a == b) return true;
        if (a == null || b == null) return false;

        Optional<String> idA = readId(plugin, a);
        Optional<String> idB = readId(plugin, b);
        if (idA.isPresent() && idB.isPresent()) {
            return idA.get().equals(idB.get());
        }
        return a.isSimilar(b);
    }

    /** Counts how many items similar to {@code probe} exist in an inventory. */
    public static int count(Plugin plugin, Inventory inv, ItemStack probe) {
        Objects.requireNonNull(inv, "inventory");
        Objects.requireNonNull(probe, "probe");
        int total = 0;
        for (ItemStack s : inv.getContents()) {
            if (s == null) continue;
            if (isSame(plugin, s, probe)) total += s.getAmount();
        }
        return total;
    }

    /** Counts items by material, null-safe. */
    public static int count(Inventory inv, Material material) {
        Objects.requireNonNull(inv, "inventory");
        Objects.requireNonNull(material, "material");
        int total = 0;
        for (ItemStack s : inv.getContents()) {
            if (s == null) continue;
            if (s.getType() == material) total += s.getAmount();
        }
        return total;
    }
}