// src/main/java/com/dreamfirestudios/dreamcore/DreamTimer/TimerPausedEvent.java
package com.dreamfirestudios.dreamcore.DreamTimer;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/// <summary>
/// Fired when a <see cref="DreamTimer"/> is paused.
/// </summary>
/// <example>
/// <code>
/// @EventHandler
/// public void onPaused(TimerPausedEvent e) {
///     // e.getRemainingSeconds()
/// }
/// </code>
/// </example>
@Getter
public class TimerPausedEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    /// <summary>The timer instance.</summary>
    private final DreamTimer timer;
    /// <summary>Remaining seconds at the time of pause.</summary>
    private final int remainingSeconds;

    /// <summary>Constructs and dispatches the event.</summary>
    public TimerPausedEvent(DreamTimer timer, int remainingSeconds) {
        this.timer = timer;
        this.remainingSeconds = remainingSeconds;
        Bukkit.getPluginManager().callEvent(this);
    }
    /// <summary>Static handler list accessor.</summary>
    public static HandlerList getHandlerList() { return HANDLERS; }
    /// <inheritdoc />
    @Override public @NotNull HandlerList getHandlers() { return HANDLERS; }
}