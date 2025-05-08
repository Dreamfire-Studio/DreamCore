package com.dreamfirestudios.dreamCore.DreamfireScoreboard.Events;

import com.dreamfirestudios.dreamCore.DreamfireScoreboard.DreamfireScoreboard;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;


@Getter
public class ScoreboardCreatedEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private final DreamfireScoreboard dreamfireScoreboard;
    @Setter
    private boolean cancelled;

    public ScoreboardCreatedEvent(DreamfireScoreboard dreamfireScoreboard){
        this.dreamfireScoreboard = dreamfireScoreboard;
        Bukkit.getPluginManager().callEvent(this);
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }
}
