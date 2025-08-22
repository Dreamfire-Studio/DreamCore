// src/main/java/com/dreamfirestudios/dreamcore/DreamScoreboard/ScoreboardCreatedEvent.java
package com.dreamfirestudios.dreamcore.DreamScoreboard;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/// <summary>
/// Fired when a <see cref="DreamScoreboard"/> is created via the builder.
/// </summary>
/// <remarks>
/// If cancelled, the scoreboard will NOT be stored in <c>DreamCore.DreamScoreboards</c>.
/// </remarks>
/// <example>
/// <code>
/// @EventHandler
/// public void onCreated(ScoreboardCreatedEvent e) {
///     // e.setCancelled(true);
/// }
/// </code>
/// </example>
public class ScoreboardCreatedEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    /// <summary>The created scoreboard.</summary>
    @Getter
    private final DreamScoreboard dreamfireScoreboard;

    private boolean cancelled;

    /// <summary>Constructs and dispatches the event.</summary>
    public ScoreboardCreatedEvent(DreamScoreboard dreamfireScoreboard) {
        this.dreamfireScoreboard = dreamfireScoreboard;
        Bukkit.getPluginManager().callEvent(this);
    }

    /// <summary>Static handler list accessor.</summary>
    public static HandlerList getHandlerList() { return handlers; }
    /// <inheritdoc />
    public @NotNull HandlerList getHandlers() { return handlers; }

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