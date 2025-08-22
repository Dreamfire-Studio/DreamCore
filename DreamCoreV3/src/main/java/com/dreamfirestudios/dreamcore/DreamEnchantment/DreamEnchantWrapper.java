package com.dreamfirestudios.dreamcore.DreamEnchantment;

import io.papermc.paper.enchantments.EnchantmentRarity;
import io.papermc.paper.registry.set.RegistryKeySet;
import net.kyori.adventure.text.Component;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget; // NOTE: Bukkit's, not NMS
import org.bukkit.entity.EntityCategory;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

/**
 * Thin adapter that exposes an {@link IDreamEnchantment} as a Paper/Bukkit {@link Enchantment}.
 * Paper 1.20.5+/1.21 uses a no-args super(); we supply identity via {@link #getKey()}.
 */
public final class DreamEnchantWrapper extends Enchantment {

    private final IDreamEnchantment delegate;

    public DreamEnchantWrapper(@NotNull IDreamEnchantment delegate) {
        super(); // key comes from getKey()
        this.delegate = delegate;
    }

    // ---- Core Paper/Bukkit surface ----

    @Override public @NotNull NamespacedKey getKey()                         { return delegate.getKey(); }
    @Override public int getMaxLevel()                                       { return delegate.getMaxLevel(); }
    @Override public int getStartLevel()                                     { return delegate.getStartLevel(); }
    @Override public boolean isTreasure()                                    { return delegate.isTreasure(); }
    @Override public boolean isCursed()                                      { return delegate.isCursed(); }
    @Override public boolean conflictsWith(@NotNull Enchantment other)       { return delegate.conflictsWith(other); }
    @Override public boolean canEnchantItem(@NotNull ItemStack itemStack)    { return delegate.canEnchantItem(itemStack); }
    @Override public @NotNull Component displayName(int level)               { return delegate.displayName(level); }
    @Override public boolean isTradeable()                                   { return delegate.isTradeable(); }
    @Override public boolean isDiscoverable()                                { return delegate.isDiscoverable(); }
    @Override public int getMinModifiedCost(int level)                       { return delegate.getMinModifiedCost(level); }
    @Override public int getMaxModifiedCost(int level)                       { return delegate.getMaxModifiedCost(level); }
    @Override public int getAnvilCost()                                      { return delegate.getAnvilCost(); }
    @Override public @NotNull Set<EquipmentSlotGroup> getActiveSlotGroups()  { return delegate.getActiveSlotGroups(); }
    @Override public @NotNull Component description()                        { return delegate.description(); }
    @Override public @NotNull RegistryKeySet<ItemType> getSupportedItems()   { return delegate.getSupportedItems(); }

    /** Paper allows null here to mean “no primaries”. */
    @Override public @Nullable RegistryKeySet<ItemType> getPrimaryItems()    { return delegate.getPrimaryItems(); }

    @Override public int getWeight()                                         { return delegate.getWeight(); }
    @Override public @NotNull RegistryKeySet<Enchantment> getExclusiveWith() { return delegate.getExclusiveWith(); }
    @Override public @NotNull String translationKey()                        { return delegate.translationKey(); }

    // ---- Legacy / deprecated hooks (still exposed on Paper) ----

    /** @deprecated legacy code path still present in Paper. */
    @Deprecated @Override @SuppressWarnings({"deprecation","removal"})
    public @NotNull String getName() { return delegate.getName(); }

    /** @deprecated legacy item target. */
    @Deprecated @Override @SuppressWarnings({"deprecation","removal"})
    public @NotNull EnchantmentTarget getItemTarget() { return delegate.getItemTarget(); }

    /** @deprecated Paper marks rarity as deprecated/for removal; keep until gone. */
    @Deprecated @Override @SuppressWarnings({"deprecation","removal"})
    public @NotNull EnchantmentRarity getRarity() { return delegate.getRarity(); }

    /** @deprecated legacy damage hook. */
    @Deprecated @Override @SuppressWarnings({"deprecation","removal"})
    public float getDamageIncrease(int level, @NotNull EntityCategory category) {
        return delegate.getDamageIncrease(level, category);
    }

    /** @deprecated legacy damage hook. */
    @Deprecated @Override @SuppressWarnings({"deprecation","removal"})
    public float getDamageIncrease(int level, @NotNull EntityType type) {
        return delegate.getDamageIncrease(level, type);
    }

    /** @deprecated legacy translation key. */
    @Deprecated @Override @SuppressWarnings({"deprecation","removal"})
    public @NotNull String getTranslationKey() { return delegate.getTranslationKey(); }
}