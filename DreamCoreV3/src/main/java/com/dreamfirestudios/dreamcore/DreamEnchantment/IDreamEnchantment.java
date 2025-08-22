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
 * Contract for Dreamfire custom enchantments.
 * Implement this and register via {@link DreamEnchantmentRegistry#register(IDreamEnchantment)}.
 *
 * The methods mirror Paper's Enchantment surface so the runtime wrapper can delegate 1:1.
 * Deprecated members remain for now because Paper still exposes them.
 */
public interface IDreamEnchantment {

    // ---- Identity / i18n ----

    /** Stable key for registration & persistence. */
    @NotNull NamespacedKey getKey();

    /** Modern translation key. */
    @NotNull String translationKey();

    /** @deprecated Legacy translation key (kept while Paper exposes it). */
    @Deprecated @NotNull String getTranslationKey();

    /** @deprecated Legacy “name” (for very old code paths). */
    @Deprecated @NotNull String getName();

    // ---- Applicability / items ----

    /** All supported items for this enchantment. */
    @NotNull RegistryKeySet<ItemType> getSupportedItems();

    /** Primary items for table weighting; may be empty. */
    @NotNull RegistryKeySet<ItemType> getPrimaryItems();

    /** Equipment slots where the enchantment is active. */
    @NotNull Set<EquipmentSlotGroup> getActiveSlotGroups();

    /** @deprecated Legacy item target for very old hooks. */
    @Deprecated @NotNull EnchantmentTarget getItemTarget();

    // ---- Gameplay attributes ----

    int getMaxLevel();
    int getStartLevel();

    boolean isTreasure();
    boolean isCursed();
    boolean isTradeable();
    boolean isDiscoverable();

    int getMinModifiedCost(int level);
    int getMaxModifiedCost(int level);
    int getAnvilCost();
    int getWeight();

    /** @deprecated Rarity is deprecated upstream but still present on Paper. */
    @Deprecated @NotNull EnchantmentRarity getRarity();

    /** Extra melee damage vs entity group (legacy hook). */
    float getDamageIncrease(int level, @NotNull EntityCategory category);

    /** Extra melee damage vs concrete type (legacy hook). */
    float getDamageIncrease(int level, @NotNull EntityType type);

    // ---- Composition / conflicts ----

    boolean conflictsWith(Enchantment other);
    boolean canEnchantItem(@NotNull ItemStack itemStack);
    @NotNull RegistryKeySet<Enchantment> getExclusiveWith();

    // ---- UI / text ----

    /** Display name for this level (adventure). */
    @NotNull Component displayName(int level);

    /** Human-readable description (adventure). */
    @NotNull Component description();

    // ---- Convenience helpers ----

    /**
     * Build a Bukkit/Paper wrapper for this enchantment.
     * (Usually called by the registry; rarely needed directly.)
     */
    default @NotNull Enchantment returnEnchantment() {
        return new DreamEnchantWrapper(this);
    }

    /**
     * Add this enchantment to an item with control over level and level restrictions.
     *
     * @return true if the item now has a non-zero level of this enchantment.
     */
    default boolean addToItem(@NotNull ItemStack stack, int level, boolean ignoreLevelRestriction) {
        if (stack.getItemMeta() == null) return false;
        int clamped = Math.max(1, Math.min(level, getMaxLevel()));
        var meta = stack.getItemMeta();
        meta.addEnchant(returnEnchantment(), clamped, ignoreLevelRestriction);
        stack.setItemMeta(meta);
        return stack.getEnchantmentLevel(returnEnchantment()) > 0;
    }

    /** Shorthand: add at level 1, ignoring caps. */
    default boolean addToItem(@NotNull ItemStack stack) {
        return addToItem(stack, 1, true);
    }
}