package com.dreamfirestudios.dreamCore.DreamfirePersistentData.Event;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@Getter
public class PersistentEntityRemovedEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private final Entity entity;
    private final NamespacedKey namespacedKey;

    public PersistentEntityRemovedEvent(Entity entity, NamespacedKey namespacedKey){
        this.entity = entity;
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
