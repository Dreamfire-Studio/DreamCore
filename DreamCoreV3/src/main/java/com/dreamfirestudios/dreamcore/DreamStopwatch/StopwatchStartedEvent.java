// StopwatchStartedEvent.java
package com.dreamfirestudios.dreamcore.DreamStopwatch;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@Getter
public class StopwatchStartedEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    private final DreamStopwatch stopwatch;

    public StopwatchStartedEvent(DreamStopwatch stopwatch) {
        this.stopwatch = stopwatch;
        Bukkit.getPluginManager().callEvent(this);
    }
    public static HandlerList getHandlerList() { return HANDLERS; }
    @Override public @NotNull HandlerList getHandlers() { return HANDLERS; }
}