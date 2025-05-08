package com.dreamfirestudios.dreamCore.DreamfirePersistentData.Event;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;


@Getter
public class PersistentBlockAddedEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private final Block block;
    private final NamespacedKey namespacedKey;
    private final Object value;

    public PersistentBlockAddedEvent(Block block, NamespacedKey namespacedKey, Object value){
        this.block = block;
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
