package com.dreamfirestudios.dreamCore.DreamfireBlockMask.Events;

import com.dreamfirestudios.dreamCore.DreamfireBlockMask.DreamfireBlockMask;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@Getter
public class BlockMaskStartedEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    private final DreamfireBlockMask dreamfireBlockMask;
    private final Player player;

    private boolean cancelled; // Lombok generates a setter for this.

    public BlockMaskStartedEvent(Player player, DreamfireBlockMask dreamfireBlockMask) {
        this.dreamfireBlockMask = dreamfireBlockMask;
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
    public boolean isCancelled() { // Explicitly implement the required method.
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) { // Explicitly implement the required method.
        this.cancelled = cancelled;
    }
}