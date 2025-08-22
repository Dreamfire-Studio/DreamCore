package com.dreamfirestudios.dreamcore.DreamItems;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.plugin.Plugin;

import java.util.*;

/**
 * Definition for a custom item. Override only what you need.
 * Paper-friendly (Adventure Components, PDC, correct attribute mapping).
 */
public interface IDreamItemStack {

    /* ------------------ Identity ------------------ */

    /** Optional stable id. If present, it's written to PDC for reliable matching. */
    default Optional<String> id() { return Optional.empty(); }

    /** Display name (Adventure). Return null to keep vanilla name. */
    default Component displayName() { return Component.text(getClass().getSimpleName()); }

    /** Material type. */
    default Material type() { return Material.DIAMOND_PICKAXE; }

    /** Stack amount (clamped to ≥1). */
    default int amount() { return 1; }

    /** Custom model data. Empty = none. */
    default OptionalInt customModelData() { return OptionalInt.empty(); }

    /** Unbreakable flag. */
    default boolean unbreakable() { return false; }

    /* ------------------ Visuals & extras ------------------ */

    /** Lore lines (Adventure). */
    default List<Component> lore() { return List.of(); }

    /** Item flags. */
    default Set<ItemFlag> flags() { return Set.of(); }

    /** Enchantments. */
    default Map<Enchantment, Integer> enchantments() { return Map.of(); }

    /**
     * Attribute modifiers. One attribute may have multiple modifiers.
     * (Direction matches Bukkit API).
     */
    default Map<Attribute, Collection<AttributeModifier>> attributeModifiers() { return Map.of(); }

    /**
     * Hook to write any PDC. Called after meta is prepared, before meta is set.
     * Use plugin to create namespaced keys if needed.
     */
    default void writePdc(Plugin plugin, PersistentDataContainer pdc) { /* no-op */ }
}