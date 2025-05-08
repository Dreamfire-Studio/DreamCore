package com.dreamfirestudios.dreamCore.DreamfireEnchantment;

import io.papermc.paper.enchantments.EnchantmentRarity;
import io.papermc.paper.registry.set.RegistryKeySet;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.EntityCategory;
import org.bukkit.entity.EntityType;
import org.bukkit.event.block.*;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public interface IDreamfireEnchantment {
    default String getName(){return getClass().getSimpleName();}
    int getMaxLevel();
    int getStartLevel();
    @SuppressWarnings({"deprecation","removal"})
    EnchantmentTarget getItemTarget();
    boolean isTreasure();
    boolean isCursed();
    boolean conflictsWith(Enchantment enchantment);
    boolean canEnchantItem(ItemStack itemStack);
    net.kyori.adventure.text.Component displayName(int i);
    boolean isTradeable();
    boolean isDiscoverable();
    int getMinModifiedCost(int i);
    int getMaxModifiedCost(int i);
    int getAnvilCost();
    @Deprecated
    @SuppressWarnings({"deprecation","removal"})
    EnchantmentRarity getRarity();
    float getDamageIncrease(int i, @NotNull EntityCategory entityCategory);
    float getDamageIncrease(int i, @NotNull EntityType entityType);
    Set<EquipmentSlotGroup> getActiveSlotGroups();
    net.kyori.adventure.text.Component description();
    RegistryKeySet<ItemType> getSupportedItems();
    RegistryKeySet<ItemType> getPrimaryItems();
    int getWeight();
    @NotNull RegistryKeySet<Enchantment> getExclusiveWith();
    @NotNull String translationKey();
    @NotNull NamespacedKey getKey();
    @NotNull String getTranslationKey();
    default Enchantment ReturnEnchantment(){ return new DreamfireEnchantWrapper(this); }
    default boolean AddEnchantmentToItemStack(ItemStack itemStack){
        var itemMeta = itemStack.getItemMeta();
        itemMeta.addEnchant(ReturnEnchantment(), 1, true);
        itemStack.setItemMeta(itemMeta);
        return itemStack.getEnchantmentLevel(ReturnEnchantment()) != 0;
    }
}
