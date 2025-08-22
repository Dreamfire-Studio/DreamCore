// src/main/java/com/dreamfirestudios/dreamcore/DreamStopwatch/StopwatchStoppedEvent.java
package com.dreamfirestudios.dreamcore.DreamStopwatch;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/// <summary>
/// Fired when a <see cref="DreamStopwatch"/> stops.
/// </summary>
@Getter
public class StopwatchStoppedEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    /// <summary>The stopwatch instance.</summary>
    private final DreamStopwatch stopwatch;
    /// <summary>Final elapsed seconds.</summary>
    private final int finalElapsedSeconds;

    /// <summary>Constructs and dispatches the event.</summary>
    public StopwatchStoppedEvent(DreamStopwatch stopwatch, int finalElapsedSeconds) {
        this.stopwatch = stopwatch;
        this.finalElapsedSeconds = finalElapsedSeconds;
        Bukkit.getPluginManager().callEvent(this);
    }
    /// <summary>Static handler list accessor.</summary>
    public static HandlerList getHandlerList() { return HANDLERS; }
    /// <inheritdoc />
    @Override public @NotNull HandlerList getHandlers() { return HANDLERS; }
}