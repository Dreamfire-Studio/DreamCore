package com.dreamfirestudios.dreamcore.DreamBlockMask;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/// <summary>
/// Fired after a frame is computed but before it is sent to the player.
/// </summary>
/// <remarks>
/// Not cancellable. Dispatched from the constructor (construction triggers <c>callEvent</c>).
/// Use this to inspect/modify state external to the mask before send.
/// </remarks>
@Getter
public class BlockMaskFrameComputedEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    /// <summary>Target player who will receive the frame.</summary>
    private final Player player;

    /// <summary>The mask that produced the computation.</summary>
    private final DreamBlockMask mask;

    /// <summary>Computed states for the new frame (what will be sent).</summary>
    private final Map<Vector, BlockState> newFrameStates;

    /// <summary>States captured from the previous frame.</summary>
    private final Map<Vector, BlockState> previousFrameStates;

    /// <summary>
    /// Constructs and dispatches the event after a frame is computed.
    /// </summary>
    /// <param name="player">Target player.</param>
    /// <param name="mask">Owning mask.</param>
    /// <param name="newFrameStates">Computed new frame states.</param>
    /// <param name="previousFrameStates">States stored from previous frame.</param>
    public BlockMaskFrameComputedEvent(Player player,
                                       DreamBlockMask mask,
                                       Map<Vector, BlockState> newFrameStates,
                                       Map<Vector, BlockState> previousFrameStates) {
        this.player = player;
        this.mask = mask;
        this.newFrameStates = newFrameStates;
        this.previousFrameStates = previousFrameStates;
        Bukkit.getPluginManager().callEvent(this);
    }

    /// <summary>Gets the static handler list required by Bukkit.</summary>
    public static HandlerList getHandlerList() { return handlers; }

    /// <inheritdoc/>
    @Override public @NotNull HandlerList getHandlers() { return handlers; }
}