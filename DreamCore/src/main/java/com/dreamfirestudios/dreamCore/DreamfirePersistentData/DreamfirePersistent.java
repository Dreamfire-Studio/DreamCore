package com.dreamfirestudios.dreamCore.DreamfirePersistentData;

import com.dreamfirestudios.dreamCore.DreamfirePersistentData.Callbacks.IDreamfirePersistentBlockCallback;
import com.dreamfirestudios.dreamCore.DreamfirePersistentData.Callbacks.IDreamfirePersistentEntityCallback;
import com.dreamfirestudios.dreamCore.DreamfirePersistentData.Callbacks.IDreamfirePersistentItemStackCallback;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

public class DreamfirePersistent {
    public static boolean CanCallback(Block block, IDreamfirePersistentBlockCallback IDreamfirePersistentBlockCallback){
        if(block == null) return false;
        if(block.getType() != IDreamfirePersistentBlockCallback.BlockIsMaterial()) return false;
        var storedTags = DreamfirePersistentBlock.GetALl(block);
        for(var key : IDreamfirePersistentBlockCallback.BlockHasTags()) {
            if(!storedTags.containsValue(key)) return false;
        }
        return true;
    }

    public static boolean CanCallback(Entity entity, IDreamfirePersistentEntityCallback IDreamfirePersistentEntityCallback){
        if(entity == null) return false;
        if(entity.getType() != IDreamfirePersistentEntityCallback.EntityIsType()) return false;
        var storedTags = DreamfirePersistentEntity.GetALl(entity);
        for(var key : IDreamfirePersistentEntityCallback.BlockHasTags()) {
            if(!storedTags.containsValue(key)) return false;
        }
        return true;
    }

    public static boolean CanCallback(ItemStack itemStack, IDreamfirePersistentItemStackCallback IDreamfirePersistentItemStackCallback){
        if(itemStack == null) return false;
        if(itemStack.getType() != IDreamfirePersistentItemStackCallback.ItemStackIsType()) return false;
        var storedTags = DreamfirePersistentItemStack.GetALl(itemStack);
        for(var key : IDreamfirePersistentItemStackCallback.ItemStackHasTags()) {
            if(!storedTags.containsValue(key)) return false;
        }
        return true;
    }
}
