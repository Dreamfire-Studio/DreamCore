package com.dreamfirestudios.dreamCore.DreamfireBlockMask.Events;

import com.dreamfirestudios.dreamCore.DreamfireBlockMask.DreamfireBlockMask;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@Getter
public class BlockMaskStoppedEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private final DreamfireBlockMask dreamfireBlockMask;
    private final Player player;

    public BlockMaskStoppedEvent(Player player, DreamfireBlockMask dreamfireBlockMask){
        this.dreamfireBlockMask = dreamfireBlockMask;
        this.player = player;
        Bukkit.getPluginManager().callEvent(this);
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }
}
