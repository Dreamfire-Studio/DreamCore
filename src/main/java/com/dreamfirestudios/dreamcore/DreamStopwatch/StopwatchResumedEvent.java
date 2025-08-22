// src/main/java/com/dreamfirestudios/dreamcore/DreamStopwatch/StopwatchResumedEvent.java
package com.dreamfirestudios.dreamcore.DreamStopwatch;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/// <summary>
/// Fired when a <see cref="DreamStopwatch"/> is resumed.
/// </summary>
@Getter
public class StopwatchResumedEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    /// <summary>The stopwatch instance.</summary>
    private final DreamStopwatch stopwatch;
    /// <summary>Elapsed seconds at the time of resume.</summary>
    private final int elapsedSeconds;

    /// <summary>Constructs and dispatches the event.</summary>
    public StopwatchResumedEvent(DreamStopwatch stopwatch, int elapsedSeconds) {
        this.stopwatch = stopwatch;
        this.elapsedSeconds = elapsedSeconds;
        Bukkit.getPluginManager().callEvent(this);
    }
    /// <summary>Static handler list accessor.</summary>
    public static HandlerList getHandlerList() { return HANDLERS; }
    /// <inheritdoc />
    @Override public @NotNull HandlerList getHandlers() { return HANDLERS; }
}