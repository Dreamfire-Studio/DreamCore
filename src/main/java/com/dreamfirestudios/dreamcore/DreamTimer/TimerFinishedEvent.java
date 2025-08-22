// src/main/java/com/dreamfirestudios/dreamcore/DreamTimer/TimerFinishedEvent.java
package com.dreamfirestudios.dreamcore.DreamTimer;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/// <summary>
/// Fired when a <see cref="DreamTimer"/> finishes or is cancelled.
/// </summary>
/// <remarks>
/// The final remaining seconds are clamped to 0.
/// </remarks>
/// <example>
/// <code>
/// @EventHandler
/// public void onFinished(TimerFinishedEvent e) {
///     getLogger().info("Timer finished at 0s");
/// }
/// </code>
/// </example>
@Getter
public class TimerFinishedEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    /// <summary>The timer instance.</summary>
    private final DreamTimer timer;
    /// <summary>Final remaining seconds (usually 0).</summary>
    private final int finalRemainingSeconds;

    /// <summary>Constructs and dispatches the event.</summary>
    public TimerFinishedEvent(DreamTimer timer, int finalRemainingSeconds) {
        this.timer = timer;
        this.finalRemainingSeconds = finalRemainingSeconds;
        Bukkit.getPluginManager().callEvent(this);
    }
    /// <summary>Static handler list accessor.</summary>
    public static HandlerList getHandlerList() { return HANDLERS; }
    /// <inheritdoc />
    @Override public @NotNull HandlerList getHandlers() { return HANDLERS; }
}