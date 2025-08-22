// src/main/java/com/dreamfirestudios/dreamcore/DreamTimer/TimerTickEvent.java
package com.dreamfirestudios.dreamcore.DreamTimer;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/// <summary>
/// Fired each period while a running <see cref="DreamTimer"/> is active (before decrement).
/// </summary>
@Getter
public class TimerTickEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    /// <summary>The timer instance.</summary>
    private final DreamTimer timer;
    /// <summary>Remaining seconds at this tick (pre-decrement).</summary>
    private final int remainingSeconds;

    /// <summary>Constructs and dispatches the event.</summary>
    public TimerTickEvent(DreamTimer timer, int remainingSeconds) {
        this.timer = timer;
        this.remainingSeconds = remainingSeconds;
        Bukkit.getPluginManager().callEvent(this);
    }
    /// <summary>Static handler list accessor.</summary>
    public static HandlerList getHandlerList() { return HANDLERS; }
    /// <inheritdoc />
    @Override public @NotNull HandlerList getHandlers() { return HANDLERS; }
}