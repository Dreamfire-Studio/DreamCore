package com.dreamfirestudios.dreamcore.DreamfireStorage;

import com.dreamfirestudios.dreamcore.DreamCore;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class StorageObjectAddedEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    @Getter
    private final DreamfireStorageObject<?> storageObject;
    private boolean cancelled;

    public StorageObjectAddedEvent(final DreamfireStorageObject<?> storageObject) {
        this.storageObject = storageObject;
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
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}

