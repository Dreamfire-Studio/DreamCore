package com.dreamfirestudios.dreamcore.DreamHologram;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/// <summary>
/// Event fired after a hologram has been collectively updated (e.g., re-stack/teleport, visibility, spacing).
/// </summary.
/// <remarks>
/// Emitted by <c>DreamHologram#updateHologram()</c> and after certain batch operations
/// (such as line additions/removals) that trigger a full restack.
/// </remarks>
/// <example>
/// <code>
/// @EventHandler
/// public void onUpdate(HologramUpdateEvent e) {
///     metrics.increment("hologram_updates");
/// }
/// </code>
/// </example>
@Getter
public class HologramUpdateEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    /// <summary>The hologram that was updated.</summary>
    private final DreamHologram hologram;

    /// <summary>
    /// Constructs the event.
    /// </summary>
    /// <param name="hologram">The hologram instance that was updated.</param>
    public HologramUpdateEvent(@NotNull DreamHologram hologram) {
        super(!Bukkit.isPrimaryThread());
        this.hologram = hologram;
    }

    /// <summary>
    /// Fires this event via the Bukkit plugin manager.
    /// </summary>
    /// <param name="hologram">The hologram instance that was updated.</param>
    public static void fire(@NotNull DreamHologram hologram) {
        Bukkit.getPluginManager().callEvent(new HologramUpdateEvent(hologram));
    }

    @Override public @NotNull HandlerList getHandlers() { return HANDLERS; }

    /// <returns>Shared handler list.</returns>
    public static @NotNull HandlerList getHandlerList() { return HANDLERS; }
}