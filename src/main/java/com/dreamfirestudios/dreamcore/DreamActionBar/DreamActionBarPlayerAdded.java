package com.dreamfirestudios.dreamcore.DreamActionBar;

import com.dreamfirestudios.dreamcore.DreamCore;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/// <summary>
/// Event fired when a <see cref="Player"/> is added to a <see cref="DreamActionBar"/>.
/// </summary>
/// <remarks>
/// This event is cancellable. Cancelling it will prevent the player
/// from being added as a viewer of the action bar.
/// </remarks>
public class DreamActionBarPlayerAdded extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    /// <summary>
    /// The action bar instance the player was added to.
    /// </summary>
    @Getter
    private final DreamActionBar DreamActionBar;

    /// <summary>
    /// The player being added to the action bar.
    /// </summary>
    @Getter
    private final Player Player;

    private boolean cancelled;

    /// <summary>
    /// Creates a new instance of the <see cref="DreamActionBarPlayerAdded"/> event.
    /// </summary>
    /// <param name="dreamActionBar">The action bar to which the player is being added.</param>
    /// <param name="player">The player being added.</param>
    public DreamActionBarPlayerAdded(DreamActionBar dreamActionBar, Player player) {
        DreamActionBar = dreamActionBar;
        Player = player;
        Bukkit.getScheduler().runTask(DreamCore.DreamCore,
                () -> Bukkit.getPluginManager().callEvent(this));
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

    /// <inheritdoc />
    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    /// <inheritdoc />
    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}