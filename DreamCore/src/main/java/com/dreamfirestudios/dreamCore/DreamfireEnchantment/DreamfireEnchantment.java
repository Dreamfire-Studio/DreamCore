package com.dreamfirestudios.dreamCore.DreamfireEnchantment;

import com.dreamfirestudios.dreamCore.DreamCore;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class DreamfireEnchantment {
    public static ArrayList<IDreamfireEnchantment> ReturnAllCustomEnchantmentsFromItem(ItemStack itemStack){
        var data = new ArrayList<IDreamfireEnchantment>();
        if(itemStack == null || itemStack.getItemMeta() == null) return data;
        var itemStackMeta = itemStack.getItemMeta();
        for(var pulseEnchantment : DreamCore.GetDreamfireCore().iDreamfireEnchantments){
            if(itemStackMeta.hasEnchant(pulseEnchantment.ReturnEnchantment())) data.add(pulseEnchantment);
        }
        return data;
    }
}
