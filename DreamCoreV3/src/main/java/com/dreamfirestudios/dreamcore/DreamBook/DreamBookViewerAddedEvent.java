package com.dreamfirestudios.dreamcore.DreamBook;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/// <summary>
/// Fired when a player is recorded as a viewer of a <see cref="DreamfireBook"/>.
/// </summary>
@Getter
public class DreamBookViewerAddedEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private final DreamBook book;
    private final Player player;
    private boolean cancelled;

    public DreamBookViewerAddedEvent(DreamBook book, Player player) {
        this.book = book;
        this.player = player;
        Bukkit.getPluginManager().callEvent(this);
    }

    public static HandlerList getHandlerList() { return handlers; }
    @Override public @NotNull HandlerList getHandlers() { return handlers; }
    @Override public boolean isCancelled() { return cancelled; }
    @Override public void setCancelled(boolean cancelled) { this.cancelled = cancelled; }
}
