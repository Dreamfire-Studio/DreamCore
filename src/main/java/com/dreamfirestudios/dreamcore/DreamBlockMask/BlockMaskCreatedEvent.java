package com.dreamfirestudios.dreamcore.DreamBlockMask;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/// <summary>
/// Fired when a <see cref="DreamBlockMask"/> is created and registered for a player.
/// </summary>
/// <remarks>
/// Not cancellable. This event is dispatched immediately from the constructor
/// (i.e., creating an instance will trigger <c>callEvent</c>).
/// Use this to initialize related state for the new mask.
/// </remarks>
@Getter
public final class BlockMaskCreatedEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    /// <summary>
    /// Player for whom the mask was created.
    /// </summary>
    private final Player player;

    /// <summary>
    /// The mask instance that was created.
    /// </summary>
    private final DreamBlockMask mask;

    /// <summary>
    /// Creates and dispatches the event.
    /// </summary>
    /// <param name="mask">The created mask.</param>
    /// <param name="player">The associated player.</param>
    public BlockMaskCreatedEvent(DreamBlockMask mask, Player player) {
        this.player = player;
        this.mask = mask;
        Bukkit.getPluginManager().callEvent(this);
    }

    /// <summary>Gets the static handler list required by Bukkit.</summary>
    public static HandlerList getHandlerList() { return handlers; }

    /// <inheritdoc/>
    @Override public @NotNull HandlerList getHandlers() { return handlers; }
}