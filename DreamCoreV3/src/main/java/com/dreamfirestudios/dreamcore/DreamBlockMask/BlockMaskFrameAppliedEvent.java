package com.dreamfirestudios.dreamcore.DreamBlockMask;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/// <summary>
/// Fired after a frame’s block changes are sent to the player.
/// </summary>
@Getter
public class BlockMaskFrameAppliedEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private final Player player;
    private final DreamBlockMask mask;
    private final Map<org.bukkit.util.Vector, BlockState> appliedStates;

    public BlockMaskFrameAppliedEvent(Player player,
                                      DreamBlockMask mask,
                                      Map<org.bukkit.util.Vector, BlockState> appliedStates) {
        this.player = player;
        this.mask = mask;
        this.appliedStates = appliedStates;
        Bukkit.getPluginManager().callEvent(this);
    }

    public static HandlerList getHandlerList() { return handlers; }
    @Override public @NotNull HandlerList getHandlers() { return handlers; }
}
