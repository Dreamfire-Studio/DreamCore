package com.dreamfirestudios.dreamCore.DreamfireCam.Events;

import com.dreamfirestudios.dreamCore.DreamfireBossBar.DreamfireBossBar;
import com.dreamfirestudios.dreamCore.DreamfireCam.DreamfireCamPath;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@Getter
public class CamPathPlayerAddedEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    private final DreamfireCamPath dreamfireCamPath;
    private final Player player;

    private boolean cancelled;

    public CamPathPlayerAddedEvent( DreamfireCamPath dreamfireCamPath, Player player){
        this.dreamfireCamPath = dreamfireCamPath;
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
    public boolean isCancelled() { // Explicitly implement this method from the Cancellable interface.
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) { // Explicitly implement this method from the Cancellable interface.
        this.cancelled = cancelled;
    }
}
