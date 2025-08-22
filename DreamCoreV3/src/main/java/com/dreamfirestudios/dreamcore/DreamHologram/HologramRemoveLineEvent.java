package com.dreamfirestudios.dreamcore.DreamHologram;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Fired after a line has been removed from a hologram.
 */
@Getter
public class HologramRemoveLineEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    private final DreamHologram hologram;
    private final int index;

    public HologramRemoveLineEvent(@NotNull DreamHologram hologram, int index) {
        super(!Bukkit.isPrimaryThread());
        this.hologram = hologram;
        this.index = index;
    }

    /** Convenience helper to construct + call the event. */
    public static void fire(@NotNull DreamHologram hologram, int index) {
        Bukkit.getPluginManager().callEvent(new HologramRemoveLineEvent(hologram, index));
    }

    @Override public @NotNull HandlerList getHandlers() { return HANDLERS; }
    public static @NotNull HandlerList getHandlerList() { return HANDLERS; }
}