package com.dreamfirestudios.dreamCore.DreamfireEvents.DreamfireActionBar;

import com.dreamfirestudios.dreamCore.DreamCore;
import com.dreamfirestudios.dreamCore.DreamfireActionBar.DreamfireActionBar;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@Getter
public class ActionBarPlayerAddedEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    private final DreamfireActionBar dreamfireActionBar;
    private final Player player;
    private boolean cancelled;

    public ActionBarPlayerAddedEvent(DreamfireActionBar dreamfireActionBar, Player player) {
        this.dreamfireActionBar = dreamfireActionBar;
        this.player = player;
        Bukkit.getScheduler().runTask(DreamCore.GetDreamfireCore(), () -> {Bukkit.getPluginManager().callEvent(this);});
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