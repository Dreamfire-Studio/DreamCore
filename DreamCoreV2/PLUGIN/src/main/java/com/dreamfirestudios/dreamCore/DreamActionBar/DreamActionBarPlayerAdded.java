package com.dreamfirestudios.dreamCore.DreamActionBar;

import com.dreamfirestudios.dreamCore.DreamCore;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class DreamActionBarPlayerAdded extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    @Getter
    private final DreamActionBar DreamActionBar;
    @Getter
    private final Player Player;
    private boolean cancelled;

    public DreamActionBarPlayerAdded(DreamActionBar dreamActionBar, Player player) {
        DreamActionBar = dreamActionBar;
        Player = player;
        Bukkit.getScheduler().runTask(DreamCore.DreamCore, () -> {Bukkit.getPluginManager().callEvent(this);});
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
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
