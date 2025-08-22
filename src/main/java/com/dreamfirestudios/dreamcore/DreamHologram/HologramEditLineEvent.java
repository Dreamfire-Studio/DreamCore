package com.dreamfirestudios.dreamcore.DreamHologram;

import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/// <summary>
/// Event fired after a hologram line's content (custom name) has been updated.
/// </summary>
/// <remarks>
/// Emitted by <c>DreamHologram#editLine(int)</c>. The index corresponds to the line position
/// within the hologram (0 = top line).
/// </remarks>
/// <example>
/// <code>
/// @EventHandler
/// public void onEdit(HologramEditLineEvent e) {
///     getLogger().fine("Edited line " + e.getIndex() + " of " + e.getHologram().getHologramName());
/// }
/// </code>
/// </example>
@Getter
public class HologramEditLineEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    /// <summary>Hologram whose line changed.</summary>
    private final DreamHologram hologram;

    /// <summary>Zero-based line index affected.</summary>
    private final int index;

    /// <summary>New Adventure component applied to the line.</summary>
    private final Component line;

    /// <summary>
    /// Constructs the event.
    /// </summary>
    /// <param name="hologram">Target hologram.</param>
    /// <param name="index">Zero-based index of the edited line.</param>
    /// <param name="line">Updated component content.</param>
    public HologramEditLineEvent(@NotNull DreamHologram hologram, int index, @NotNull Component line) {
        super(!Bukkit.isPrimaryThread());
        this.hologram = hologram;
        this.index = index;
        this.line = line;
    }

    /// <summary>
    /// Fires this event via the plugin manager.
    /// </summary>
    /// <param name="hologram">Target hologram.</param>
    /// <param name="index">Edited line index.</param>
    /// <param name="line">Updated line component.</param>
    public static void fire(@NotNull DreamHologram hologram, int index, @NotNull Component line) {
        Bukkit.getPluginManager().callEvent(new HologramEditLineEvent(hologram, index, line));
    }

    @Override public @NotNull HandlerList getHandlers() { return HANDLERS; }

    /// <returns>Shared handler list.</returns>
    public static @NotNull HandlerList getHandlerList() { return HANDLERS; }
}