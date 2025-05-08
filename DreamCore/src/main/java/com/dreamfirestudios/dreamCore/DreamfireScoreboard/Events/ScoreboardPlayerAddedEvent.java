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
public class ScoreboardPlayerAddedEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    private final DreamfireScoreboard dreamfireScoreboard;
    private final Player player;

    private boolean cancelled;

    public ScoreboardPlayerAddedEvent(DreamfireScoreboard dreamfireScoreboard, Player player){
        this.dreamfireScoreboard = dreamfireScoreboard;
        this.player = player;
        Bukkit.getPluginManager().callEvent(this);
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    @Override
    public boolean isCancelled() { // Explicitly override this method for the interface.
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) { // Explicitly override this method for the interface.
        this.cancelled = cancelled;
    }
}
