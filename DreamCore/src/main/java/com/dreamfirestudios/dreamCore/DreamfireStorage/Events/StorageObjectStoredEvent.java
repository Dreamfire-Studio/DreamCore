package com.dreamfirestudios.dreamCore.DreamfireStorage.Events;

import com.dreamfirestudios.dreamCore.DreamfireScoreboard.DreamfireScoreboard;
import com.dreamfirestudios.dreamCore.DreamfireStorage.DreamfireStorageObject;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@Getter
public class StorageObjectStoredEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private final DreamfireStorageObject<?> dreamfireStorageObject;
    @Setter
    private boolean cancelled;

    public <T> StorageObjectStoredEvent(DreamfireStorageObject<T> dreamfireStorageObject){
        this.dreamfireStorageObject = dreamfireStorageObject;
        Bukkit.getPluginManager().callEvent(this);
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }
}
