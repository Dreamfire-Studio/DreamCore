package com.dreamfirestudios.dreamcore.DreamBook;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/// <summary>
/// Fired when a player is removed from the viewers of a <see cref="DreamBook"/>.
/// </summary>
/// <remarks>
/// This event is <see cref="Cancellable"/>. Cancelling may prevent the viewer removal,
/// depending on how the caller handles the cancellation state.
/// The event is dispatched from the constructor.
/// </remarks>
@Getter
public class DreamBookViewerRemovedEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    /// <summary>The book previously being viewed.</summary>
    private final DreamBook book;

    /// <summary>The player being removed from the viewer list.</summary>
    private final Player player;

    private boolean cancelled;

    /// <summary>
    /// Constructs and dispatches a new <see cref="DreamBookViewerRemovedEvent"/>.
    /// </summary>
    /// <param name="book">The book being un-viewed.</param>
    /// <param name="player">The player being removed as a viewer.</param>
    public DreamBookViewerRemovedEvent(DreamBook book, Player player) {
        this.book = book;
        this.player = player;
        Bukkit.getPluginManager().callEvent(this);
    }

    /// <summary>Gets the static handler list required by Bukkit.</summary>
    public static HandlerList getHandlerList() { return handlers; }

    /// <inheritdoc/>
    @Override public @NotNull HandlerList getHandlers() { return handlers; }

    /// <inheritdoc/>
    @Override public boolean isCancelled() { return cancelled; }

    /// <inheritdoc/>
    @Override public void setCancelled(boolean cancelled) { this.cancelled = cancelled; }
}