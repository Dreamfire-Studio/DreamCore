package com.dreamfirestudios.dreamcore.DreamHologram;

import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Fired after a hologram line has been inserted and spawned.
 */
@Getter
public class HologramAddLineEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    private final DreamHologram hologram;
    private final Component line;

    public HologramAddLineEvent(@NotNull DreamHologram hologram, @NotNull Component line) {
        super(!Bukkit.isPrimaryThread()); // Async flag
        this.hologram = hologram;
        this.line = line;
    }

    /** Convenience helper to construct + call the event. */
    public static void fire(@NotNull DreamHologram hologram, @NotNull Component line) {
        Bukkit.getPluginManager().callEvent(new HologramAddLineEvent(hologram, line));
    }

    @Override public @NotNull HandlerList getHandlers() { return HANDLERS; }
    public static @NotNull HandlerList getHandlerList() { return HANDLERS; }
}
