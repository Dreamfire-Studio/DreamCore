package com.dreamfirestudios.dreamcore.DreamBlockMask;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/// <summary>
/// Fired when a <see cref="DreamBlockMask"/> transitions from paused to playing.
/// </summary>
/// <remarks>
/// Not cancellable. Emitted after the play state is set. Dispatched from the constructor.
/// </remarks>
@Getter
public final class BlockMaskStartedEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    /// <summary>Target player for whom the mask has started playing.</summary>
    private final Player player;

    /// <summary>The mask instance that changed to playing.</summary>
    private final DreamBlockMask mask;

    /// <summary>
    /// Constructs and dispatches the event after the mask has started playing.
    /// </summary>
    /// <param name="player">Target player.</param>
    /// <param name="mask">The mask instance.</param>
    public BlockMaskStartedEvent(Player player, DreamBlockMask mask) {
        this.player = player;
        this.mask = mask;
        Bukkit.getPluginManager().callEvent(this);
    }

    /// <summary>Gets the static handler list required by Bukkit.</summary>
    public static HandlerList getHandlerList() { return handlers; }

    /// <inheritdoc/>
    @Override public @NotNull HandlerList getHandlers() { return handlers; }
}