package com.dreamfirestudios.dreamcore.DreamHologram;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Fired after a hologram has been deleted and its ArmorStands removed.
 */
@Getter
public class HologramDeleteEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    private final DreamHologram hologram;

    public HologramDeleteEvent(@NotNull DreamHologram hologram) {
        super(!Bukkit.isPrimaryThread());
        this.hologram = hologram;
    }

    /** Convenience helper to construct + call the event. */
    public static void fire(@NotNull DreamHologram hologram) {
        Bukkit.getPluginManager().callEvent(new HologramDeleteEvent(hologram));
    }

    @Override public @NotNull HandlerList getHandlers() { return HANDLERS; }
    public static @NotNull HandlerList getHandlerList() { return HANDLERS; }
}