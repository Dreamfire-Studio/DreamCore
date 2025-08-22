package com.dreamfirestudios.dreamcore.DreamHologram;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Fired after a hologram has been collectively updated (teleport/re-stack, visibility, spacing, etc.).
 */
@Getter
public class HologramUpdateEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    private final DreamHologram hologram;

    public HologramUpdateEvent(@NotNull DreamHologram hologram) {
        super(!Bukkit.isPrimaryThread());
        this.hologram = hologram;
    }

    /** Convenience helper to construct + call the event. */
    public static void fire(@NotNull DreamHologram hologram) {
        Bukkit.getPluginManager().callEvent(new HologramUpdateEvent(hologram));
    }

    @Override public @NotNull HandlerList getHandlers() { return HANDLERS; }
    public static @NotNull HandlerList getHandlerList() { return HANDLERS; }
}