package com.dreamfirestudios.dreamcore.DreamBlockDisplay;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/// <summary>
/// Event triggered when a <see cref="BlockDisplay"/> entity is created.
/// </summary>
/// <remarks>
/// This event is fired immediately after the display has been spawned,
/// allowing listeners to configure it. It is not cancellable.
/// </remarks>
@Getter
public class BlockDisplayCreatedEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    /// <summary>
    /// The created <see cref="BlockDisplay"/> instance.
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
    /// Gets the static handler list for this event type.
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