package com.dreamfirestudios.dreamcore.DreamVanish;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * <summary>
 * Fired when a target entity is marked visible to a specific viewer via
 * {@link DreamVanish#showTargetToViewer(Entity, Player)}.
 * </summary>
 *
 * <remarks>
 * The event is synchronously called from the constructor using Bukkit's PluginManager.
 * </remarks>
 */
@Getter
public class VanishShowTargetEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private final Entity target;
    private final Player viewer;

    /**
     * <summary>Constructs and immediately dispatches the event.</summary>
     *
     * <param name="target">The entity being shown.</param>
     * <param name="viewer">The viewer who will now see the entity.</param>
     */
    public VanishShowTargetEvent(Entity target, Player viewer){
        this.target = target;
        this.viewer = viewer;
        Bukkit.getPluginManager().callEvent(this);
    }

    /** <returns>The static handler list required by Bukkit.</returns> */
    public static HandlerList getHandlerList() { return handlers; }

    /** <returns>The handler list instance.</returns> */
    @Override public @NotNull HandlerList getHandlers() { return handlers; }
}