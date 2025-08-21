package com.dreamfirestudios.dreamcore.DreamPersistentData;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/// <summary>
/// Event fired when a persistent data entry is removed from an <see cref="Entity"/>.
/// </summary>
/// <remarks>
/// This event is not cancellable.
/// Use it to react when custom metadata identified by a <see cref="NamespacedKey"/> is cleared from an entity.
/// </remarks>
/// <example>
/// ```java
/// @EventHandler
/// public void onPersistentEntityRemoved(PersistentEntityRemovedEvent e) {
///     Entity ent = e.getEntity();
///     getLogger().info("Removed key " + e.getNamespacedKey() + " from entity " + ent.getUniqueId());
/// }
/// ```
/// </example>
@Getter
public class PersistentEntityRemovedEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    /// <summary>
    /// The entity from which the persistent data entry was removed.
    /// </summary>
    private final Entity entity;

    /// <summary>
    /// The namespaced key that was removed from the entity.
    /// </summary>
    private final NamespacedKey namespacedKey;

    /// <summary>
    /// Initializes a new <see cref="PersistentEntityRemovedEvent"/>.
    /// </summary>
    /// <param name="entity">The target entity.</param>
    /// <param name="namespacedKey">The key that was removed.</param>
    public PersistentEntityRemovedEvent(Entity entity, NamespacedKey namespacedKey){
        this.entity = entity;
        this.namespacedKey = namespacedKey;
        Bukkit.getPluginManager().callEvent(this);
    }

    /// <summary>
    /// Gets the handler list for this event.
    /// </summary>
    /// <returns>The static handler list.</returns>
    public static HandlerList getHandlerList() {
        return handlers;
    }

    /// <inheritdoc />
    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }
}