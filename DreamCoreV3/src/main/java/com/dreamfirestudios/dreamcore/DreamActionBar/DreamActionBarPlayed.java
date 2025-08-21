package com.dreamfirestudios.dreamcore.DreamActionBar;

import com.dreamfirestudios.dreamcore.DreamCore;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class DreamActionBarPlayed extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    @Getter
    private final DreamActionBar DreamActionBar;
    private boolean cancelled;

    public DreamActionBarPlayed(DreamActionBar dreamActionBar) {
        DreamActionBar = dreamActionBar;
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
