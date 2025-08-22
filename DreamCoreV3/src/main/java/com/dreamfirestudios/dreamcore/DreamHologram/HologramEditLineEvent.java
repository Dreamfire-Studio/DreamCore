package com.dreamfirestudios.dreamcore.DreamHologram;

import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Fired after a hologram line's content has been updated.
 */
@Getter
public class HologramEditLineEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    private final DreamHologram hologram;
    private final int index;
    private final Component line;

    public HologramEditLineEvent(@NotNull DreamHologram hologram, int index, @NotNull Component line) {
        super(!Bukkit.isPrimaryThread());
        this.hologram = hologram;
        this.index = index;
        this.line = line;
    }

    /** Convenience helper to construct + call the event. */
    public static void fire(@NotNull DreamHologram hologram, int index, @NotNull Component line) {
        Bukkit.getPluginManager().callEvent(new HologramEditLineEvent(hologram, index, line));
    }

    @Override public @NotNull HandlerList getHandlers() { return HANDLERS; }
    public static @NotNull HandlerList getHandlerList() { return HANDLERS; }
}