package com.dreamfirestudios.dreamCore.DreamfireEvents.DreamfireBlockDisplay;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;


@Getter
public class BlockDisplayCreatedEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final BlockDisplay blockDisplay;

    public BlockDisplayCreatedEvent(BlockDisplay blockDisplay){
        this.blockDisplay = blockDisplay;
        Bukkit.getPluginManager().callEvent(this);
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }
}
