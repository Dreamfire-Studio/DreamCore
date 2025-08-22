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
 /// <summary>
 /// Thin adapter that exposes an <see cref="IDreamEnchantment"/> as a Paper/Bukkit <see cref="Enchantment"/>.
 /// </summary>
 /// <remarks>
 /// Paper 1.20.5+/1.21+ uses a no‑args <c>super()</c>; identity is provided via <see cref="#getKey()"/>.
 /// All methods delegate 1:1 to the <see cref="IDreamEnchantment"/> implementation.
 /// </remarks>
 */
public final class DreamEnchantWrapper extends Enchantment {

    private final IDreamEnchantment delegate;

    /**
     /// <summary>
     /// Constructs a wrapper around a custom enchantment implementation.
     /// </summary>
     /// <param name="delegate">The custom enchantment implementation.</param>
     */
    public DreamEnchantWrapper(@NotNull IDreamEnchantment delegate) {
        super(); // key comes from getKey()
        this.delegate = delegate;
    }

    // ---- Core Paper/Bukkit surface ----

    /// <inheritdoc/>
    @Override public @NotNull NamespacedKey getKey()                         { return delegate.getKey(); }
    /// <inheritdoc/>
    @Override public int getMaxLevel()                                       { return delegate.getMaxLevel(); }
    /// <inheritdoc/>
    @Override public int getStartLevel()                                     { return delegate.getStartLevel(); }
    /// <inheritdoc/>
    @Override public boolean isTreasure()                                    { return delegate.isTreasure(); }
    /// <inheritdoc/>
    @Override public boolean isCursed()                                      { return delegate.isCursed(); }
    /// <inheritdoc/>
    @Override public boolean conflictsWith(@NotNull Enchantment other)       { return delegate.conflictsWith(other); }
    /// <inheritdoc/>
    @Override public boolean canEnchantItem(@NotNull ItemStack itemStack)    { return delegate.canEnchantItem(itemStack); }
    /// <inheritdoc/>
    @Override public @NotNull Component displayName(int level)               { return delegate.displayName(level); }
    /// <inheritdoc/>
    @Override public boolean isTradeable()                                   { return delegate.isTradeable(); }
    /// <inheritdoc/>
    @Override public boolean isDiscoverable()                                { return delegate.isDiscoverable(); }
    /// <inheritdoc/>
    @Override public int getMinModifiedCost(int level)                       { return delegate.getMinModifiedCost(level); }
    /// <inheritdoc/>
    @Override public int getMaxModifiedCost(int level)                       { return delegate.getMaxModifiedCost(level); }
    /// <inheritdoc/>
    @Override public int getAnvilCost()                                      { return delegate.getAnvilCost(); }
    /// <inheritdoc/>
    @Override public @NotNull Set<EquipmentSlotGroup> getActiveSlotGroups()  { return delegate.getActiveSlotGroups(); }
    /// <inheritdoc/>
    @Override public @NotNull Component description()                        { return delegate.description(); }
    /// <inheritdoc/>
    @Override public @NotNull RegistryKeySet<ItemType> getSupportedItems()   { return delegate.getSupportedItems(); }
    /**
     /// <summary>Paper allows null here to mean “no primaries”.</summary>
     */
    @Override public @Nullable RegistryKeySet<ItemType> getPrimaryItems()    { return delegate.getPrimaryItems(); }
    /// <inheritdoc/>
    @Override public int getWeight()                                         { return delegate.getWeight(); }
    /// <inheritdoc/>
    @Override public @NotNull RegistryKeySet<Enchantment> getExclusiveWith() { return delegate.getExclusiveWith(); }
    /// <inheritdoc/>
    @Override public @NotNull String translationKey()                        { return delegate.translationKey(); }

    // ---- Legacy / deprecated hooks (still exposed on Paper) ----

    /** @deprecated Legacy code path still present in Paper. */
    @Deprecated @Override @SuppressWarnings({"deprecation","removal"})
    public @NotNull String getName() { return delegate.getName(); }

    /** @deprecated Legacy item target. */
    @Deprecated @Override @SuppressWarnings({"deprecation","removal"})
    public @NotNull EnchantmentTarget getItemTarget() { return delegate.getItemTarget(); }

    /** @deprecated Paper marks rarity as deprecated/for removal; keep until gone. */
    @Deprecated @Override @SuppressWarnings({"deprecation","removal"})
    public @NotNull EnchantmentRarity getRarity() { return delegate.getRarity(); }

    /** @deprecated Legacy damage hook. */
    @Deprecated @Override @SuppressWarnings({"deprecation","removal"})
    public float getDamageIncrease(int level, @NotNull EntityCategory category) {
        return delegate.getDamageIncrease(level, category);
    }

    /** @deprecated Legacy damage hook. */
    @Deprecated @Override @SuppressWarnings({"deprecation","removal"})
    public float getDamageIncrease(int level, @NotNull EntityType type) {
        return delegate.getDamageIncrease(level, type);
    }

    /** @deprecated Legacy translation key. */
    @Deprecated @Override @SuppressWarnings({"deprecation","removal"})
    public @NotNull String getTranslationKey() { return delegate.getTranslationKey(); }
}