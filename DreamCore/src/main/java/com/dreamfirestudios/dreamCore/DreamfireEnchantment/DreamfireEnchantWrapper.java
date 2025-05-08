package com.dreamfirestudios.dreamCore.DreamfireEnchantment;

import io.papermc.paper.enchantments.EnchantmentRarity;
import io.papermc.paper.registry.set.RegistryKeySet;
import net.kyori.adventure.text.Component;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.EntityCategory;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class DreamfireEnchantWrapper extends Enchantment {

    private final IDreamfireEnchantment IDreamfireEnchantment;

    public DreamfireEnchantWrapper(IDreamfireEnchantment IDreamfireEnchantment) {
        this.IDreamfireEnchantment = IDreamfireEnchantment;
    }

    @Override
    @SuppressWarnings({"deprecation","removal"})
    public @NotNull String getName() {
        return "";
    }

    @Override
    public int getMaxLevel() {
        return IDreamfireEnchantment.getMaxLevel();
    }

    @Override
    public int getStartLevel() {
        return IDreamfireEnchantment.getStartLevel();
    }

    @Override
    @SuppressWarnings({"deprecation","removal"})
    public @NotNull EnchantmentTarget getItemTarget() {
        return null;
    }

    @Override
    @SuppressWarnings({"deprecation","removal"})
    public boolean isTreasure() {
        return IDreamfireEnchantment.isTreasure();
    }

    @Override
    public boolean isCursed() {
        return IDreamfireEnchantment.isCursed();
    }

    @Override
    public boolean conflictsWith(@NotNull Enchantment enchantment) {
        return IDreamfireEnchantment.conflictsWith(enchantment);
    }

    @Override
    public boolean canEnchantItem(@NotNull ItemStack itemStack) {
        return IDreamfireEnchantment.canEnchantItem(itemStack);
    }

    @Override
    public @NotNull Component displayName(int i) {
        return IDreamfireEnchantment.displayName(i);
    }

    @Override
    public boolean isTradeable() {
        return IDreamfireEnchantment.isTradeable();
    }

    @Override
    public boolean isDiscoverable() {
        return IDreamfireEnchantment.isDiscoverable();
    }

    @Override
    public int getMinModifiedCost(int i) {
        return IDreamfireEnchantment.getMinModifiedCost(i);
    }

    @Override
    public int getMaxModifiedCost(int i) {
        return IDreamfireEnchantment.getMaxModifiedCost(i);
    }

    @Override
    public int getAnvilCost() {
        return IDreamfireEnchantment.getAnvilCost();
    }

    @Override
    @SuppressWarnings({"deprecation","removal"})
    public @NotNull EnchantmentRarity getRarity() {
        return IDreamfireEnchantment.getRarity();
    }

    @Override
    @SuppressWarnings({"deprecation","removal"})
    public float getDamageIncrease(int i, @NotNull EntityCategory entityCategory) {
        return IDreamfireEnchantment.getDamageIncrease(i ,entityCategory);
    }

    @Override
    @SuppressWarnings({"deprecation","removal"})
    public float getDamageIncrease(int i, @NotNull EntityType entityType) {
        return IDreamfireEnchantment.getDamageIncrease(i, entityType);
    }

    @Override
    public @NotNull Set<EquipmentSlotGroup> getActiveSlotGroups() {
        return IDreamfireEnchantment.getActiveSlotGroups();
    }

    @Override
    public @NotNull Component description() {
        return IDreamfireEnchantment.description();
    }

    @Override
    public @NotNull RegistryKeySet<ItemType> getSupportedItems() {
        return IDreamfireEnchantment.getSupportedItems();
    }

    @Override
    public @Nullable RegistryKeySet<ItemType> getPrimaryItems() {
        return IDreamfireEnchantment.getPrimaryItems();
    }

    @Override
    public int getWeight() {
        return IDreamfireEnchantment.getWeight();
    }

    @Override
    public @NotNull RegistryKeySet<Enchantment> getExclusiveWith() {
        return IDreamfireEnchantment.getExclusiveWith();
    }

    @Override
    @SuppressWarnings({"deprecation","removal"})
    public @NotNull String translationKey() {return IDreamfireEnchantment.translationKey();}

    @Override
    public @NotNull NamespacedKey getKey() {
        return IDreamfireEnchantment.getKey();
    }

    @Override
    @SuppressWarnings({"deprecation","removal"})
    public @NotNull String getTranslationKey() {
        return IDreamfireEnchantment.getTranslationKey();
    }
}
