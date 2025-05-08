package com.dreamfirestudios.dreamCore.DreamfirePersistentData.Event;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;


@Getter
public class PersistentItemStackRemovedEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private final ItemStack itemStack;
    private final NamespacedKey namespacedKey;

    public PersistentItemStackRemovedEvent(ItemStack itemStack, NamespacedKey namespacedKey){
        this.itemStack = itemStack;
        this.namespacedKey = namespacedKey;
        Bukkit.getPluginManager().callEvent(this);
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }
}
