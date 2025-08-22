package com.dreamfirestudios.dreamcore.DreamCam;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/// <summary>
/// Fired before a player is added to a <see cref="DreamCamPath"/>.
/// </summary>
/// <remarks>
/// This event is <see cref="Cancellable"/>. If cancelled, the player is not added to the path.
/// The event is dispatched from the constructor (instantiation triggers <c>PluginManager.callEvent</c>).
/// </remarks>
/// <example>
/// ```java
/// @EventHandler
/// public void onAdd(CamPathPlayerAddedEvent e) {
///     if (!e.getPlayer().hasPermission("cameras.use")) {
///         e.setCancelled(true);
///     }
/// }
/// ```
/// </example>
@Getter
public final class CamPathPlayerAddedEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    /// <summary>The camera path the player is being added to.</summary>
    private final DreamCamPath camPath;

    /// <summary>The player being added.</summary>
    private final Player player;

    private boolean cancelled;

    /// <summary>
    /// Creates and dispatches the event.
    /// </summary>
    /// <param name="camPath">The camera path.</param>
    /// <param name="player">The player being added.</param>
    public CamPathPlayerAddedEvent(DreamCamPath camPath, Player player) {
        this.camPath = camPath;
        this.player = player;
        Bukkit.getPluginManager().callEvent(this);
    }

    /// <summary>Gets the static handler list required by Bukkit.</summary>
    public static HandlerList getHandlerList() { return handlers; }

    /// <inheritdoc/>
    @Override public @NotNull HandlerList getHandlers() { return handlers; }

    /// <inheritdoc/>
    @Override public boolean isCancelled() { return cancelled; }

    /// <inheritdoc/>
    @Override public void setCancelled(boolean cancelled) { this.cancelled = cancelled; }
}