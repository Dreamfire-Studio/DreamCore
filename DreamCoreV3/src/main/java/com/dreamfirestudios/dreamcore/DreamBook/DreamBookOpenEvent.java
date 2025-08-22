package com.dreamfirestudios.dreamcore.DreamBook;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class DreamBookOpenEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    private final DreamBook dreamfireBook;
    private final Player player;

    private boolean cancelled;

    public DreamBookOpenEvent(DreamBook dreamfireBook, Player player) {
        this.dreamfireBook = dreamfireBook;
        this.player = player;
        Bukkit.getPluginManager().callEvent(this);
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    @Override
    public boolean isCancelled() { // Explicitly implement this method from the Cancellable interface.
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) { // Explicitly implement this method from the Cancellable interface.
        this.cancelled = cancelled;
    }
}
