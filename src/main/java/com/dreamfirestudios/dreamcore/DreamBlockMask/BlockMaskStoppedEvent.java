package com.dreamfirestudios.dreamcore.DreamBlockMask;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/// <summary>
/// Fired when a <see cref="DreamBlockMask"/> is stopped and unregistered.
/// </summary>
/// <remarks>
/// Not cancellable. Emitted after restoration/cleanup logic runs. Dispatched from the constructor.
/// </remarks>
@Getter
public final class BlockMaskStoppedEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    /// <summary>Target player whose mask was stopped.</summary>
    private final Player player;

    /// <summary>The mask instance that was stopped.</summary>
    private final DreamBlockMask mask;

    /// <summary>
    /// Constructs and dispatches the event after the mask has been stopped.
    /// </summary>
    /// <param name="player">Target player.</param>
    /// <param name="mask">The stopped mask instance.</param>
    public BlockMaskStoppedEvent(Player player, DreamBlockMask mask) {
        this.player = player;
        this.mask = mask;
        Bukkit.getPluginManager().callEvent(this);
    }

    /// <summary>Gets the static handler list required by Bukkit.</summary>
    public static HandlerList getHandlerList() { return handlers; }

    /// <inheritdoc/>
    @Override public @NotNull HandlerList getHandlers() { return handlers; }
}