package com.dreamfirestudios.dreamcore.DreamEvent;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/// <summary>
/// Lightweight, throttleable move event fired by DreamPlayerMoveMonitor when a player's
/// position/rotation actually changes between checks. Cancellable like PlayerMoveEvent.
/// </summary>
public final class DreamPlayerMoveEvent extends Event implements Cancellable {
    private static final HandlerList HANDLERS = new HandlerList();

    private final Player player;
    private final Location from;
    private final Location to;
    private boolean cancelled;

    /// <summary>
    /// Preferred ctor — sync/async flag is derived from current thread.
    /// </summary>
    public DreamPlayerMoveEvent(Player player, Location from, Location to) {
        super(!Bukkit.isPrimaryThread()); // <-- IMPORTANT: make this false on main thread
        this.player = player;
        this.from = from.clone();
        this.to = to.clone();
    }

    public Player getPlayer() { return player; }
    public Location getFrom() { return from.clone(); }
    public Location getTo() { return to.clone(); }

    @Override public boolean isCancelled() { return cancelled; }
    @Override public void setCancelled(boolean cancel) { this.cancelled = cancel; }

    @Override public HandlerList getHandlers() { return HANDLERS; }
    public static HandlerList getHandlerList() { return HANDLERS; }
}