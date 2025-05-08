package com.dreamfirestudios.dreamCore.DreamfireTextDisplay.Events;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.TextDisplay;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@Getter
public class TextDisplaySpawnEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final TextDisplay textDisplay;

    public TextDisplaySpawnEvent(TextDisplay textDisplay){
        this.textDisplay = textDisplay;
        Bukkit.getPluginManager().callEvent(this);
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }
}
