package com.dreamfirestudios.dreamcore.DreamBlockMask;


import com.dreamfirestudios.dreamCore.DreamfireBlockMask.DreamfireBlockMask;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/// <summary>
/// Fired after a frame is computed but before it is sent to the player.
/// </summary>
@Getter
public class BlockMaskFrameComputedEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private final Player player;
    private final DreamfireBlockMask mask;
    private final Map<org.bukkit.util.Vector, BlockState> newFrameStates;
    private final Map<org.bukkit.util.Vector, BlockState> previousFrameStates;

    public BlockMaskFrameComputedEvent(Player player,
                                       DreamfireBlockMask mask,
                                       Map<org.bukkit.util.Vector, BlockState> newFrameStates,
                                       Map<org.bukkit.util.Vector, BlockState> previousFrameStates) {
        this.player = player;
        this.mask = mask;
        this.newFrameStates = newFrameStates;
        this.previousFrameStates = previousFrameStates;
        Bukkit.getPluginManager().callEvent(this);
    }

    public static HandlerList getHandlerList() { return handlers; }
    @Override public @NotNull HandlerList getHandlers() { return handlers; }
}