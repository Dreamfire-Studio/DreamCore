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
/// Fired after a frame’s block changes are sent to the player.
/// </summary>
/// <remarks>
/// Not cancellable. Dispatched from the constructor (construction triggers <c>callEvent</c>).
/// </remarks>
@Getter
public class BlockMaskFrameAppliedEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    /// <summary>Target player who received the frame updates.</summary>
    private final Player player;

    /// <summary>The mask that produced this frame.</summary>
    private final DreamBlockMask mask;

    /// <summary>
    /// The map of world positions to the <see cref="BlockState"/> values that were applied in this frame.
    /// </summary>
    private final Map<Vector, BlockState> appliedStates;

    /// <summary>
    /// Constructs and dispatches the event after a frame is sent to a player.
    /// </summary>
    /// <param name="player">Target player.</param>
    /// <param name="mask">Owning mask.</param>
    /// <param name="appliedStates">Map of applied states keyed by block location vector.</param>
    public BlockMaskFrameAppliedEvent(Player player,
                                      DreamBlockMask mask,
                                      Map<Vector, BlockState> appliedStates) {
        this.player = player;
        this.mask = mask;
        this.appliedStates = appliedStates;
        Bukkit.getPluginManager().callEvent(this);
    }

    /// <summary>Gets the static handler list required by Bukkit.</summary>
    public static HandlerList getHandlerList() { return handlers; }

    /// <inheritdoc/>
    @Override public @NotNull HandlerList getHandlers() { return handlers; }
}