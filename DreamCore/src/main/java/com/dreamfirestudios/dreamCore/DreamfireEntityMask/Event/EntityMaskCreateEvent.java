package com.dreamfirestudios.dreamCore.DreamfireEntityMask.Event;

import com.dreamfirestudios.dreamCore.DreamfireBlockMask.DreamfireBlockMask;
import com.dreamfirestudios.dreamCore.DreamfireEntityMask.DreamfireEntityMask;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@Getter
public class EntityMaskCreateEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private final DreamfireEntityMask dreamfireEntityMask;
    private final Player player;

    public EntityMaskCreateEvent(DreamfireEntityMask dreamfireEntityMask, Player player){
        this.dreamfireEntityMask = dreamfireEntityMask;
        this.player = player;
        Bukkit.getPluginManager().callEvent(this);
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }
}
