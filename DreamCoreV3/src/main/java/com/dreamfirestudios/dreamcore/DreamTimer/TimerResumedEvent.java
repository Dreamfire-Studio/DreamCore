// TimerResumedEvent.java
package com.dreamfirestudios.dreamcore.DreamTimer;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@Getter
public class TimerResumedEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    private final DreamTimer timer;
    private final int remainingSeconds;

    public TimerResumedEvent(DreamTimer timer, int remainingSeconds) {
        this.timer = timer;
        this.remainingSeconds = remainingSeconds;
        Bukkit.getPluginManager().callEvent(this);
    }
    public static HandlerList getHandlerList() { return HANDLERS; }
    @Override public @NotNull HandlerList getHandlers() { return HANDLERS; }
}