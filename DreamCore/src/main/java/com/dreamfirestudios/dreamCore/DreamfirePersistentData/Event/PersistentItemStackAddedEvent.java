package com.dreamfirestudios.dreamCore.DreamfirePersistentData.Event;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

@Getter
public class PersistentItemStackAddedEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private final ItemStack itemStack;
    private final NamespacedKey namespacedKey;
    private final Object value;

    public PersistentItemStackAddedEvent(ItemStack itemStack, NamespacedKey namespacedKey, Object value){
        this.itemStack = itemStack;
        this.namespacedKey = namespacedKey;
        this.value = value;
        Bukkit.getPluginManager().callEvent(this);
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }
}
