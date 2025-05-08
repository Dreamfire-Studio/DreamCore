package com.dreamfirestudios.dreamCore.DreamfireVanish.Event;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@Getter
public class VanishShowTargetEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private final Entity target;
    private final Player viewer;

    public VanishShowTargetEvent(Entity target, Player viewer){
        this.target = target;
        this.viewer = viewer;
        Bukkit.getPluginManager().callEvent(this);
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }
}

