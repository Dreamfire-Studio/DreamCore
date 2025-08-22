package com.dreamfirestudios.dreamcore.DreamItemDisplay;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@Getter
public class ItemDisplaySpawnEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final ItemDisplay itemDisplay;

    public ItemDisplaySpawnEvent(ItemDisplay itemDisplay){
        this.itemDisplay = itemDisplay;
        Bukkit.getPluginManager().callEvent(this);
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }
}