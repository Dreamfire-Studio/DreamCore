package com.dreamfirestudios.dreamcore.DreamActionBar;

import com.dreamfirestudios.dreamcore.DreamCore;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/// <summary>
/// Event fired when a <see cref="DreamActionBar"/> resumes playing after being paused.
/// </summary>
/// <remarks>
/// This event is cancellable. Cancelling it will prevent the action bar
/// from resuming playback.
/// </remarks>
public class DreamActionBarPlayed extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    /// <summary>
    /// The action bar instance associated with this event.
    /// </summary>
    @Getter
    private final DreamActionBar DreamActionBar;

    private boolean cancelled;

    /// <summary>
    /// Creates a new instance of the <see cref="DreamActionBarPlayed"/> event.
    /// </summary>
    /// <param name="dreamActionBar">The action bar being resumed.</param>
    public DreamActionBarPlayed(DreamActionBar dreamActionBar) {
        DreamActionBar = dreamActionBar;
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