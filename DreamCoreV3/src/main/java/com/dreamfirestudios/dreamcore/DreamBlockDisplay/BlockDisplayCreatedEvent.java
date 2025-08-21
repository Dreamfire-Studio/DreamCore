package com.dreamfirestudios.dreamcore.DreamBlockDisplay;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/// <summary>
/// Event fired when a <see cref="BlockDisplay"/> is created.
/// </summary>
/// <remarks>
/// This event is not cancellable. It exposes the created display entity
/// so listeners can apply additional configuration immediately after creation.
/// </remarks>
@Getter
public class BlockDisplayCreatedEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    /// <summary>
    /// The created <see cref="BlockDisplay"/>.
    /// </summary>
    private final BlockDisplay blockDisplay;

    /// <summary>
    /// Initializes a new instance of the <see cref="BlockDisplayCreatedEvent"/>.
    /// </summary>
    /// <param name="blockDisplay">The created block display entity.</param>
    public BlockDisplayCreatedEvent(BlockDisplay blockDisplay) {
        this.blockDisplay = blockDisplay;
        Bukkit.getPluginManager().callEvent(this);
    }

    /// <summary>
    /// Gets the handler list for this event.
    /// </summary>
    /// <returns>The static handler list.</returns>
    public static HandlerList getHandlerList() {
        return handlers;
    }

    /// <inheritdoc />
    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }
}