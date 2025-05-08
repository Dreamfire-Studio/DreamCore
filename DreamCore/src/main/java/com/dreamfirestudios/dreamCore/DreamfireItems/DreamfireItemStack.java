package com.dreamfirestudios.dreamCore.DreamfireItems;

import com.dreamfirestudios.dreamCore.DreamCore;
import com.dreamfirestudios.dreamCore.DreamfirePersistentData.DreamfirePersistentItemStack;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

public class DreamfireItemStack {
    public static IDreamfireItemStack ReturnPulseItem(ItemStack itemStack){
        for(var pulseItemStack : DreamCore.GetDreamfireCore().iDreamfireItemStacks){
            if(isItemTheSame(ReturnItemStack(pulseItemStack), itemStack)) return pulseItemStack;
        }
        return null;
    }

    @SuppressWarnings("deprecation")
    public static ItemStack ReturnItemStack(IDreamfireItemStack pulseItemStack){
        ItemStack itemStack = new ItemStack(pulseItemStack.itemType(), pulseItemStack.itemAmount());
        var itemMeta = itemStack.getItemMeta();
        if(itemMeta != null){
            itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', pulseItemStack.itemName()));
            itemMeta.setLore(pulseItemStack.itemLore());
            for(var enchantment : pulseItemStack.itemEnchantments().keySet()) itemMeta.addEnchant(enchantment, pulseItemStack.itemEnchantments().get(enchantment), true);
            for(var itemFlag : pulseItemStack.itemFlags()) itemMeta.addItemFlags(itemFlag);
            for(var attribute : pulseItemStack.attributeModifiers().keySet()) itemMeta.addAttributeModifier(pulseItemStack.attributeModifiers().get(attribute), attribute);
            if(pulseItemStack.customModelData() > 0) itemMeta.setCustomModelData(pulseItemStack.customModelData());
            itemMeta.setUnbreakable(pulseItemStack.unbreakable());
        }
        itemStack.setItemMeta(itemMeta);
        for(String key : pulseItemStack.nbtTags().keySet()) DreamfirePersistentItemStack.Add(null, itemStack, PersistentDataType.STRING, key, pulseItemStack.nbtTags().get(key));
        return itemStack;
    }

    /**
     * Counts the number of a specific {@link ItemStack} in a player's inventory.
     * @param player The player whose inventory is being checked.
     * @param itemStack The {@link ItemStack} to count.
     * @return The number of items found in the player's inventory.
     * @throws IllegalArgumentException if the player or itemStack is null.
     */
    public static int countItem(Player player, ItemStack itemStack){
        if (player == null || itemStack == null) throw new IllegalArgumentException("Player or ItemStack cannot be null.");
        return countItem(player.getInventory(), itemStack);
    }

    /**
     * Counts the number of a specific {@link ItemStack} in a given inventory.
     * @param inventory The inventory to check.
     * @param itemStack The {@link ItemStack} to count.
     * @return The number of items found in the inventory.
     * @throws IllegalArgumentException if the inventory or itemStack is null.
     */
    public static int countItem(Inventory inventory, ItemStack itemStack){
        if (inventory == null || itemStack == null) throw new IllegalArgumentException("Inventory or ItemStack cannot be null.");
        var count = 0;
        for(var item : inventory.getContents()){
            if(isItemTheSame(item, itemStack)) count += item.getAmount();
        }
        return count;
    }

    /**
     * Counts the number of items of a specific {@link Material} in a player's inventory.
     * @param player The player whose inventory is being checked.
     * @param material The {@link Material} to count.
     * @return The number of items found in the player's inventory.
     * @throws IllegalArgumentException if the player or material is null.
     */
    public static int countItem(Player player, Material material){
        if (player == null || material == null) throw new IllegalArgumentException("Player or Material cannot be null.");
        return countItem(player.getInventory(), material);
    }

    /**
     * Counts the number of items of a specific {@link Material} in a given inventory.
     * @param inventory The inventory to check.
     * @param material The {@link Material} to count.
     * @return The number of items found in the inventory.
     * @throws IllegalArgumentException if the inventory or material is null.
     */
    public static int countItem(Inventory inventory, Material material){
        if (inventory == null || material == null)  throw new IllegalArgumentException("Inventory or Material cannot be null.");
        var count = 0;
        for(var item : inventory.getContents()){
            if(item.getType() == material) count += item.getAmount();
        }
        return count;
    }

    /**
     * Checks if two {@link ItemStack}s are the same (based on type and metadata).
     * @param a The first {@link ItemStack}.
     * @param b The second {@link ItemStack}.
     * @return True if the {@link ItemStack}s are identical, otherwise false.
     */
    public static boolean isItemTheSame(ItemStack a, ItemStack b){
        if(a == null || b == null) return false;
        return a.isSimilar(b);
    }
}
