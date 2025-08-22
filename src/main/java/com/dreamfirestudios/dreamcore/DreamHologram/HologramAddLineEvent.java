package com.dreamfirestudios.dreamcore.DreamHologram;

import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/// <summary>
/// Event fired after a hologram line has been inserted and its ArmorStand spawned.
/// </summary>
/// <remarks>
/// The event is flagged async if fired from a non-primary thread (mirroring Bukkit's Event ctor).
/// Use this to react to content creation, e.g., logging or augmenting the line's visuals.
/// </remarks>
/// <example>
/// <code>
/// @EventHandler
/// public void onAdd(HologramAddLineEvent e) {
///     getLogger().info("Line added to " + e.getHologram().getHologramName());
/// }
/// </code>
/// </example>
@Getter
public class HologramAddLineEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    /// <summary>Hologram instance receiving the new line.</summary>
    private final DreamHologram hologram;

    /// <summary>The Adventure component used as the line's custom name.</summary>
    private final Component line;

    /// <summary>
    /// Constructs the event.
    /// </summary>
    /// <param name="hologram">The hologram receiving the line.</param>
    /// <param name="line">The line component that was added.</param>
    public HologramAddLineEvent(@NotNull DreamHologram hologram, @NotNull Component line) {
        super(!Bukkit.isPrimaryThread()); // Async flag mirrors current thread
        this.hologram = hologram;
        this.line = line;
    }

    /// <summary>
    /// Convenience helper to create and call this event via the plugin manager.
    /// </summary>
    /// <param name="hologram">Target hologram.</param>
    /// <param name="line">Line component that was added.</param>
    public static void fire(@NotNull DreamHologram hologram, @NotNull Component line) {
        Bukkit.getPluginManager().callEvent(new HologramAddLineEvent(hologram, line));
    }

    @Override public @NotNull HandlerList getHandlers() { return HANDLERS; }

    /// <summary>
    /// Static accessor required by Bukkit's event system.
    /// </summary>
    /// <returns>Shared handler list.</returns>
    public static @NotNull HandlerList getHandlerList() { return HANDLERS; }
}