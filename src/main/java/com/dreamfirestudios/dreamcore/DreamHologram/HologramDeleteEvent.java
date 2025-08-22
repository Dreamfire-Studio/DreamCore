package com.dreamfirestudios.dreamcore.DreamHologram;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/// <summary>
/// Event fired after a hologram has been deleted and all ArmorStands removed.
/// </summary>
/// <remarks>
/// Emitted by <c>DreamHologram#deleteHologram()</c> once removal completes.
/// Use this to clean up any external references or metadata bound to the hologram.
/// </remarks>
/// <example>
/// <code>
/// @EventHandler
/// public void onDelete(HologramDeleteEvent e) {
///     cache.remove(e.getHologram().getClassID());
/// }
/// </code>
/// </example>
@Getter
public class HologramDeleteEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    /// <summary>The hologram that was deleted.</summary>
    private final DreamHologram hologram;

    /// <summary>
    /// Constructs the event.
    /// </summary>
    /// <param name="hologram">The deleted hologram.</param>
    public HologramDeleteEvent(@NotNull DreamHologram hologram) {
        super(!Bukkit.isPrimaryThread());
        this.hologram = hologram;
    }

    /// <summary>
    /// Fires this event through the Bukkit plugin manager.
    /// </summary>
    /// <param name="hologram">The deleted hologram.</param>
    public static void fire(@NotNull DreamHologram hologram) {
        Bukkit.getPluginManager().callEvent(new HologramDeleteEvent(hologram));
    }

    @Override public @NotNull HandlerList getHandlers() { return HANDLERS; }

    /// <returns>Shared handler list.</returns>
    public static @NotNull HandlerList getHandlerList() { return HANDLERS; }
}
