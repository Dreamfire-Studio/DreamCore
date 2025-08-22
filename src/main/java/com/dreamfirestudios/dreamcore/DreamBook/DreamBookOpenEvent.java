package com.dreamfirestudios.dreamcore.DreamBook;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/// <summary>
/// Fired when a <see cref="DreamBook"/> is about to be opened for a player.
/// </summary>
/// <remarks>
/// This event is <see cref="Cancellable"/>. Cancelling prevents the open operation,
/// depending on how listeners and the caller handle the cancellation state.
/// The event is dispatched from the constructor (instantiation triggers <c>callEvent</c>).
/// </remarks>
@Getter
public class DreamBookOpenEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    /// <summary>
    /// The book that is being opened.
    /// </summary>
    private final DreamBook dreamfireBook;

    /// <summary>
    /// The target player for whom the book is being opened.
    /// </summary>
    private final Player player;

    private boolean cancelled;

    /// <summary>
    /// Constructs and dispatches a new <see cref="DreamBookOpenEvent"/>.
    /// </summary>
    /// <param name="dreamfireBook">The book to open.</param>
    /// <param name="player">The target player.</param>
    public DreamBookOpenEvent(DreamBook dreamfireBook, Player player) {
        this.dreamfireBook = dreamfireBook;
        this.player = player;
        Bukkit.getPluginManager().callEvent(this);
    }

    /// <summary>Gets the static handler list required by Bukkit.</summary>
    public static HandlerList getHandlerList() { return handlers; }

    /// <inheritdoc/>
    @Override
    public @NotNull HandlerList getHandlers() { return handlers; }

    /// <inheritdoc/>
    @Override
    public boolean isCancelled() { return cancelled; }

    /// <inheritdoc/>
    @Override
    public void setCancelled(boolean cancelled) { this.cancelled = cancelled; }
}