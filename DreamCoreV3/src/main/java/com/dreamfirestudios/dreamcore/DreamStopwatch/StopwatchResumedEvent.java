// StopwatchResumedEvent.java
package com.dreamfirestudios.dreamcore.DreamStopwatch;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@Getter
public class StopwatchResumedEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    private final DreamStopwatch stopwatch;
    private final int elapsedSeconds;

    public StopwatchResumedEvent(DreamStopwatch stopwatch, int elapsedSeconds) {
        this.stopwatch = stopwatch;
        this.elapsedSeconds = elapsedSeconds;
        Bukkit.getPluginManager().callEvent(this);
    }
    public static HandlerList getHandlerList() { return HANDLERS; }
    @Override public @NotNull HandlerList getHandlers() { return HANDLERS; }
}
