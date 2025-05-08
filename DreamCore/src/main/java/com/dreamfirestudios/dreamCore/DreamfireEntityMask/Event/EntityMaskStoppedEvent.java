package com.dreamfirestudios.dreamCore.DreamfireEntityMask.Event;

import com.dreamfirestudios.dreamCore.DreamfireEntityMask.DreamfireEntityMask;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;


@Getter
public class EntityMaskStoppedEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private final DreamfireEntityMask dreamfireEntityMask;

    public EntityMaskStoppedEvent(DreamfireEntityMask dreamfireEntityMask){
        this.dreamfireEntityMask = dreamfireEntityMask;
        Bukkit.getPluginManager().callEvent(this);
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }
}
