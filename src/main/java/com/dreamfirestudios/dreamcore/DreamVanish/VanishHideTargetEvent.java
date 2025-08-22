package com.dreamfirestudios.dreamcore.DreamVanish;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/// <summary>
/// Fired when a target entity is marked hidden from a specific viewer via
/// <see cref="DreamVanish#hideTargetFromViewer(Entity, Player)"/>.
/// </summary>
/// <remarks>Synchronously dispatched from the constructor.</remarks>
/// <example>
/// <code>
/// @EventHandler
/// public void onHide(VanishHideTargetEvent e) {
///     // e.getTarget(), e.getViewer()
/// }
/// </code>
/// </example>
@Getter
public class VanishHideTargetEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    /// <summary>The hidden target.</summary>
    private final Entity target;
    /// <summary>The viewer who can no longer see the target.</summary>
    private final Player viewer;

    /// <summary>Constructs and dispatches the event.</summary>
    public VanishHideTargetEvent(Entity target, Player viewer){
        this.target = target;
        this.viewer = viewer;
        Bukkit.getPluginManager().callEvent(this);
    }

    /// <summary>Static handler list required by Bukkit.</summary>
    public static HandlerList getHandlerList() { return handlers; }
    /// <inheritdoc />
    @Override public @NotNull HandlerList getHandlers() { return handlers; }
}