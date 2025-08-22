package com.dreamfirestudios.dreamcore.DreamEntityMask;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@Getter
public class EntityMaskPausedEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private final DreamEntityMask dreamfireEntityMask;

    public EntityMaskPausedEvent(DreamEntityMask dreamfireEntityMask){
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
