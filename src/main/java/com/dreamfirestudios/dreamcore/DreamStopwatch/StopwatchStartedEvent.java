// src/main/java/com/dreamfirestudios/dreamcore/DreamStopwatch/StopwatchStartedEvent.java
package com.dreamfirestudios.dreamcore.DreamStopwatch;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/// <summary>
/// Fired when a <see cref="DreamStopwatch"/> starts.
/// </summary>
@Getter
public class StopwatchStartedEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    /// <summary>The stopwatch instance.</summary>
    private final DreamStopwatch stopwatch;

    /// <summary>Constructs and dispatches the event.</summary>
    public StopwatchStartedEvent(DreamStopwatch stopwatch) {
        this.stopwatch = stopwatch;
        Bukkit.getPluginManager().callEvent(this);
    }
    /// <summary>Static handler list accessor.</summary>
    public static HandlerList getHandlerList() { return HANDLERS; }
    /// <inheritdoc />
    @Override public @NotNull HandlerList getHandlers() { return HANDLERS; }
}