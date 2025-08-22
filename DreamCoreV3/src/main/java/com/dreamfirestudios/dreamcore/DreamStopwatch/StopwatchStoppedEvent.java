// StopwatchStoppedEvent.java
package com.dreamfirestudios.dreamcore.DreamStopwatch;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@Getter
public class StopwatchStoppedEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    private final DreamStopwatch stopwatch;
    private final int finalElapsedSeconds;

    public StopwatchStoppedEvent(DreamStopwatch stopwatch, int finalElapsedSeconds) {
        this.stopwatch = stopwatch;
        this.finalElapsedSeconds = finalElapsedSeconds;
        Bukkit.getPluginManager().callEvent(this);
    }
    public static HandlerList getHandlerList() { return HANDLERS; }
    @Override public @NotNull HandlerList getHandlers() { return HANDLERS; }
}