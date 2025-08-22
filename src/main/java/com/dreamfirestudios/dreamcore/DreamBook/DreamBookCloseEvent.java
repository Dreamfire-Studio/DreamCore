package com.dreamfirestudios.dreamcore.DreamBook;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/// <summary>
/// Fired after a <see cref="DreamBook"/> is closed by a player.
/// </summary>
/// <remarks>
/// Not cancellable. This event is dispatched from the constructor,
/// i.e., creating an instance will immediately call <c>PluginManager.callEvent</c>.
/// </remarks>
@Getter
public class DreamBookCloseEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    /// <summary>
    /// The book instance that was closed.
    /// </summary>
    private final DreamBook dreamBook;

    /// <summary>
    /// The player who closed the book.
    /// </summary>
    private final Player player;

    /// <summary>
    /// Constructs and dispatches a new <see cref="DreamBookCloseEvent"/>.
    /// </summary>
    /// <param name="dreamfireBook">The closed book instance.</param>
    /// <param name="player">The player who closed the book.</param>
    public DreamBookCloseEvent(DreamBook dreamfireBook, Player player){
        this.dreamBook = dreamfireBook;
        this.player = player;
        Bukkit.getPluginManager().callEvent(this);
    }

    /// <summary>Gets the static handler list required by Bukkit.</summary>
    public static HandlerList getHandlerList() { return handlers; }

    /// <inheritdoc/>
    @Override
    public @NotNull HandlerList getHandlers() { return handlers; }
}