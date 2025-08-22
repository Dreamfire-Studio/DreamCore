package com.dreamfirestudios.dreamcore.DreamScoreboard;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Fired when a DreamScoreboard is created via the builder.
 * If cancelled, the scoreboard will NOT be stored in DreamCore.DreamScoreboards.
 */
public class ScoreboardCreatedEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    @Getter
    private final DreamScoreboard dreamfireScoreboard;
    private boolean cancelled;

    public ScoreboardCreatedEvent(DreamScoreboard dreamfireScoreboard) {
        this.dreamfireScoreboard = dreamfireScoreboard;
        Bukkit.getPluginManager().callEvent(this);
    }

    public static HandlerList getHandlerList() { return handlers; }
    public @NotNull HandlerList getHandlers() { return handlers; }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    /// <inheritdoc />
    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}