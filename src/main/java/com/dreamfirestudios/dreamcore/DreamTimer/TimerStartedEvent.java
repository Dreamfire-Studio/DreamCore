// src/main/java/com/dreamfirestudios/dreamcore/DreamTimer/TimerStartedEvent.java
package com.dreamfirestudios.dreamcore.DreamTimer;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/// <summary>
/// Fired when a <see cref="DreamTimer"/> starts.
/// </summary>
/// <remarks>
/// Contains the starting seconds at the time of dispatch.
/// </remarks>
@Getter
public class TimerStartedEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    /// <summary>The timer instance.</summary>
    private final DreamTimer timer;
    /// <summary>Starting seconds at the moment the timer began.</summary>
    private final int startingSeconds;

    /// <summary>Constructs and dispatches the event.</summary>
    public TimerStartedEvent(DreamTimer timer, int startingSeconds) {
        this.timer = timer;
        this.startingSeconds = startingSeconds;
        Bukkit.getPluginManager().callEvent(this);
    }
    /// <summary>Static handler list accessor.</summary>
    public static HandlerList getHandlerList() { return HANDLERS; }
    /// <inheritdoc />
    @Override public @NotNull HandlerList getHandlers() { return HANDLERS; }
}