package com.dreamfirestudios.dreamCore.DreamfireInventory;

import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Objects;

public class DreamfireInventory {

    /**
     * Returns all items from the living entity's equipment with their respective locations.
     *
     * @param livingEntity The entity whose items are being retrieved.
     * @return A map of ItemStacks and their locations.
     * @throws IllegalArgumentException if the livingEntity is null.
     */
    public static HashMap<ItemStack, ItemLocation> returnAllItemsWithLocation(LivingEntity livingEntity) {
        if (livingEntity == null) return new HashMap<>();
        if(livingEntity.getEquipment() == null) return new HashMap<>();
        var foundInformation = new HashMap<ItemStack, ItemLocation>();
        for (var itemStack : livingEntity.getEquipment().getArmorContents()) foundInformation.put(itemStack, ItemLocation.EntityArmor);
        foundInformation.put(livingEntity.getEquipment().getItemInMainHand(), ItemLocation.EntityMainHand);
        foundInformation.put(livingEntity.getEquipment().getItemInOffHand(), ItemLocation.EntityOffHand);
        return foundInformation;
    }

    /**
     * Returns all items from the player's inventory with their respective locations.
     *
     * @param playerInventory The player's inventory.
     * @return A map of ItemStacks and their locations.
     * @throws IllegalArgumentException if the playerInventory is null.
     */
    public static HashMap<ItemStack, ItemLocation> returnAllItemsWithLocation(PlayerInventory playerInventory) {
        if (playerInventory == null) throw new IllegalArgumentException("Player inventory cannot be null.");
        var foundInformation = new HashMap<ItemStack, ItemLocation>();
        for (var itemStack : playerInventory.getContents()) {
            if (itemStack == null) continue;
            else if (itemStack.isSimilar(playerInventory.getItemInMainHand())) foundInformation.put(itemStack, ItemLocation.EntityMainHand);
            else if (itemStack.isSimilar(playerInventory.getItemInOffHand())) foundInformation.put(itemStack, ItemLocation.EntityOffHand);
            else if (armorContentsContains(playerInventory.getArmorContents(), itemStack)) foundInformation.put(itemStack, ItemLocation.EntityArmor);
            else foundInformation.put(itemStack, ItemLocation.EntityInventory);
        }
        return foundInformation;
    }

    /**
     * Checks if the item exists in the armor contents.
     *
     * @param itemStacks The array of armor items.
     * @param itemStack The item to check for.
     * @return true if the item exists in armor contents.
     */
    private static boolean armorContentsContains(ItemStack[] itemStacks, ItemStack itemStack) {
        for (var i : itemStacks) {
            if (itemStack.isSimilar(i)) return true;
        }
        return false;
    }

    /**
     * Checks if an item is broken based on its durability.
     *
     * @param item The item to check.
     * @return true if the item is broken, false otherwise.
     */
    @SuppressWarnings("deprecation")
    public static boolean isItemBroken(ItemStack item) {
        if (item == null) return false;
        int damage = item.getDurability();
        int maxDur = item.getType().getMaxDurability();
        return maxDur > 0 && damage >= maxDur;
    }

    /**
     * Gets the total count of a specific item in the player's inventory.
     *
     * @param inventory The player's inventory.
     * @param itemStack The item to count.
     * @return The total number of items found.
     * @throws IllegalArgumentException if the inventory or itemStack is null.
     */
    public static int getTotalItemCount(PlayerInventory inventory, ItemStack itemStack) {
        int totalCount = 0;
        for (ItemStack item : inventory.getContents()) {
            if (item != null && item.isSimilar(itemStack)) {
                totalCount += item.getAmount();
            }
        }
        return totalCount;
    }

    /**
     * Finds an item in the player's inventory by its name or type.
     *
     * @param inventory The player's inventory.
     * @param name The name to search for.
     * @return The matching ItemStack, or null if not found.
     */
    public static ItemStack findItemByName(PlayerInventory inventory, String name) {
        for (ItemStack item : inventory.getContents()) {
            if (item != null && item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
                if (PlainTextComponentSerializer.plainText().serialize(Objects.requireNonNull(item.getItemMeta().displayName())).equalsIgnoreCase(name)) {
                    return item;
                }
            }
        }
        return null;
    }

    /**
     * Removes a specified item from the player's inventory.
     *
     * @param inventory The player's inventory.
     * @param itemStack The item to remove.
     * @throws IllegalArgumentException if inventory or itemStack is null.
     */
    public static void removeItemFromInventory(PlayerInventory inventory, ItemStack itemStack) {
        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack currentItem = inventory.getItem(i);
            if (currentItem != null && currentItem.isSimilar(itemStack)) {
                inventory.clear(i);
            }
        }
    }

    /**
     * Adds an item to the player's inventory.
     *
     * @param inventory The player's inventory.
     * @param itemStack The item to add.
     * @return true if item was added successfully, false if no space.
     * @throws IllegalArgumentException if inventory or itemStack is null.
     */
    public static boolean addItemToInventory(PlayerInventory inventory, ItemStack itemStack) {
        if (inventory.firstEmpty() == -1) {
            return false; // No space
        }
        inventory.addItem(itemStack);
        return true;
    }

    /**
     * Swaps two items in the inventory.
     *
     * @param inventory The player's inventory.
     * @param slot1 The first slot index.
     * @param slot2 The second slot index.
     * @throws IllegalArgumentException if inventory is null or slots are invalid.
     */
    public static void swapItemsInInventory(PlayerInventory inventory, int slot1, int slot2) {
        ItemStack item1 = inventory.getItem(slot1);
        ItemStack item2 = inventory.getItem(slot2);

        inventory.setItem(slot1, item2);
        inventory.setItem(slot2, item1);
    }

    /**
     * Adds an item to the player's main hand if it's not already there.
     *
     * @param player The player to modify.
     * @param itemStack The item to add.
     * @throws IllegalArgumentException if player or itemStack is null.
     */
    public static void addItemToMainHand(Player player, ItemStack itemStack) {
        if (player.getInventory().getItemInMainHand().isSimilar(itemStack)) {
            return; // Item already in main hand
        }
        player.getInventory().setItemInMainHand(itemStack);
    }

    /**
     * Checks if the player has a sufficient amount of a specific item.
     *
     * @param inventory The player's inventory.
     * @param itemStack The item to check.
     * @param amount The amount required.
     * @return true if the player has enough items, false otherwise.
     * @throws IllegalArgumentException if inventory or itemStack is null.
     */
    public static boolean hasSufficientAmount(PlayerInventory inventory, ItemStack itemStack, int amount) {
        int count = 0;
        for (ItemStack item : inventory.getContents()) {
            if (item != null && item.isSimilar(itemStack)) {
                count += item.getAmount();
            }
        }
        return count >= amount;
    }
}
