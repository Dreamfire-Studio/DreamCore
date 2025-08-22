package com.dreamfirestudios.dreamcore.DreamHologram;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/// <summary>
/// Event fired after a line has been removed from a hologram.
/// </summary>
/// <remarks>
/// Emitted by <c>DreamHologram#removeLine(int)</c>. After removal, remaining lines are
/// restacked by the hologram.
/// </remarks>
/// <example>
/// <code>
/// @EventHandler
/// public void onRemove(HologramRemoveLineEvent e) {
///     audit.log("Removed line " + e.getIndex() + " from " + e.getHologram().getHologramName());
/// }
/// </code>
/// </example>
@Getter
public class HologramRemoveLineEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    /// <summary>Hologram affected by the removal.</summary>
    private final DreamHologram hologram;

    /// <summary>Zero-based index of the line that was removed.</summary>
    private final int index;

    /// <summary>
    /// Constructs the event.
    /// </summary>
    /// <param name="hologram">The hologram from which a line was removed.</param>
    /// <param name="index">The index of the removed line.</param>
    public HologramRemoveLineEvent(@NotNull DreamHologram hologram, int index) {
        super(!Bukkit.isPrimaryThread());
        this.hologram = hologram;
        this.index = index;
    }

    /// <summary>
    /// Fires this event via the Bukkit plugin manager.
    /// </summary>
    /// <param name="hologram">Affected hologram.</param>
    /// <param name="index">Removed line index.</param>
    public static void fire(@NotNull DreamHologram hologram, int index) {
        Bukkit.getPluginManager().callEvent(new HologramRemoveLineEvent(hologram, index));
    }

    @Override public @NotNull HandlerList getHandlers() { return HANDLERS; }

    /// <returns>Shared handler list.</returns>
    public static @NotNull HandlerList getHandlerList() { return HANDLERS; }
}