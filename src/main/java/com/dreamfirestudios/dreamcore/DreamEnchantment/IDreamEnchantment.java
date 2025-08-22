package com.dreamfirestudios.dreamcore.DreamEnchantment;

import io.papermc.paper.enchantments.EnchantmentRarity;
import io.papermc.paper.registry.set.RegistryKeySet;
import net.kyori.adventure.text.Component;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget; // legacy but still exposed
import org.bukkit.entity.EntityCategory;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 /// <summary>
 /// Contract for Dreamfire custom enchantments.
 /// </summary>
 /// <remarks>
 /// Implement this and register via <see cref="DreamEnchantmentRegistry.register(IDreamEnchantment)"/>.
 /// Methods mirror Paper’s <see cref="Enchantment"/> surface so the runtime wrapper can delegate 1:1.
 /// Deprecated members remain while Paper still exposes them.
 /// </remarks>
 */
public interface IDreamEnchantment {

    // ---- Identity / i18n ----

    /**
     /// <summary>Stable key for registration &amp; persistence.</summary>
     */
    @NotNull NamespacedKey getKey();

    /**
     /// <summary>Modern translation key.</summary>
     */
    @NotNull String translationKey();

    /**
     /// <summary>Legacy translation key (kept while Paper exposes it).</summary>
     /// <remarks>Deprecated upstream.</remarks>
     */
    @Deprecated @NotNull String getTranslationKey();

    /**
     /// <summary>Legacy “name” (for very old code paths).</summary>
     /// <remarks>Deprecated upstream.</remarks>
     */
    @Deprecated @NotNull String getName();

    // ---- Applicability / items ----

    /**
     /// <summary>All supported items for this enchantment.</summary>
     */
    @NotNull RegistryKeySet<ItemType> getSupportedItems();

    /**
     /// <summary>Primary items for table weighting; may be empty.</summary>
     /// <remarks>Return <c>null</c> to indicate “no primaries” per Paper convention.</remarks>
     */
    @NotNull RegistryKeySet<ItemType> getPrimaryItems();

    /**
     /// <summary>Equipment slots where the enchantment is active.</summary>
     */
    @NotNull Set<EquipmentSlotGroup> getActiveSlotGroups();

    /**
     /// <summary>Legacy item target for very old hooks.</summary>
     /// <remarks>Deprecated upstream.</remarks>
     */
    @Deprecated @NotNull EnchantmentTarget getItemTarget();

    // ---- Gameplay attributes ----

    /// <summary>Maximum level allowed by this enchantment.</summary>
    int getMaxLevel();

    /// <summary>Starting level when applied via typical means (e.g., table/loot).</summary>
    int getStartLevel();

    /// <summary>Whether the enchantment is a treasure-only enchant.</summary>
    boolean isTreasure();

    /// <summary>Whether the enchantment is cursed.</summary>
    boolean isCursed();

    /// <summary>Whether this enchantment can be obtained via villager trading.</summary>
    boolean isTradeable();

    /// <summary>Whether this enchantment can appear via standard discovery (loot tables, enchanting table).</summary>
    boolean isDiscoverable();

    /// <summary>Minimum modified enchanting cost for a given level.</summary>
    int getMinModifiedCost(int level);

    /// <summary>Maximum modified enchanting cost for a given level.</summary>
    int getMaxModifiedCost(int level);

    /// <summary>Additional anvil cost when combining.</summary>
    int getAnvilCost();

    /// <summary>Table/loot weight.</summary>
    int getWeight();

    /**
     /// <summary>Rarity is deprecated upstream but still present on Paper.</summary>
     */
    @Deprecated @NotNull EnchantmentRarity getRarity();

    /**
     /// <summary>Extra melee damage vs. entity group (legacy hook).</summary>
     /// <param name="level">Enchantment level.</param>
     /// <param name="category">Entity category.</param>
     /// <returns>Additional damage.</returns>
     */
    float getDamageIncrease(int level, @NotNull EntityCategory category);

    /**
     /// <summary>Extra melee damage vs. concrete type (legacy hook).</summary>
     /// <param name="level">Enchantment level.</param>
     /// <param name="type">Entity type.</param>
     /// <returns>Additional damage.</returns>
     */
    float getDamageIncrease(int level, @NotNull EntityType type);

    // ---- Composition / conflicts ----

    /**
     /// <summary>Whether this enchantment conflicts with another.</summary>
     /// <param name="other">Other enchantment.</param>
     /// <returns><c>true</c> if they conflict.</returns>
     */
    boolean conflictsWith(Enchantment other);

    /**
     /// <summary>Whether this enchantment can be applied to the given item.</summary>
     /// <param name="itemStack">Target item.</param>
     /// <returns><c>true</c> if applicable.</returns>
     */
    boolean canEnchantItem(@NotNull ItemStack itemStack);

    /**
     /// <summary>Set of enchantments that are exclusive (mutually incompatible) with this one.</summary>
     */
    @NotNull RegistryKeySet<Enchantment> getExclusiveWith();

    // ---- UI / text ----

    /**
     /// <summary>Display name for a given level (Adventure component).</summary>
     /// <param name="level">Level to display.</param>
     /// <returns>Formatted name.</returns>
     */
    @NotNull Component displayName(int level);

    /**
     /// <summary>Human‑readable description (Adventure component).</summary>
     /// <returns>Description text.</returns>
     */
    @NotNull Component description();

    // ---- Convenience helpers ----

    /**
     /// <summary>
     /// Builds a Bukkit/Paper wrapper for this enchantment.
     /// </summary>
     /// <remarks>
     /// Usually invoked by the registry; rarely needed directly.
     /// Current default constructs a <see cref="DreamEnchantWrapper"/> on each call.
     /// </remarks>
     /// <returns>New wrapper instance.</returns>
     */
    default @NotNull Enchantment returnEnchantment() {
        return new DreamEnchantWrapper(this);
    }

    /**
     /// <summary>
     /// Adds this enchantment to an item with control over level and level restrictions.
     /// </summary>
     /// <param name="stack">Target item stack.</param>
     /// <param name="level">Requested level (will be clamped to [1, max]).</param>
     /// <param name="ignoreLevelRestriction">If true, bypasses normal level caps.</param>
     /// <returns><c>true</c> if the item now has a non‑zero level of this enchantment.</returns>
     */
    default boolean addToItem(@NotNull ItemStack stack, int level, boolean ignoreLevelRestriction) {
        if (stack.getItemMeta() == null) return false;
        int clamped = Math.max(1, Math.min(level, getMaxLevel()));
        var meta = stack.getItemMeta();
        meta.addEnchant(returnEnchantment(), clamped, ignoreLevelRestriction);
        stack.setItemMeta(meta);
        return stack.getEnchantmentLevel(returnEnchantment()) > 0;
    }

    /**
     /// <summary>
     /// Shorthand: add at level 1, ignoring caps.
     /// </summary>
     /// <param name="stack">Target item stack.</param>
     /// <returns><c>true</c> if applied successfully.</returns>
     */
    default boolean addToItem(@NotNull ItemStack stack) {
        return addToItem(stack, 1, true);
    }
}