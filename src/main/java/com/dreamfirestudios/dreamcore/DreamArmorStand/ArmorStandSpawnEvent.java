package com.dreamfirestudios.dreamcore.DreamArmorStand;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/// <summary>
/// Event fired when an <see cref="ArmorStand"/> is spawned.
/// </summary>
/// <remarks>
/// This event is not cancellable.
/// It provides details about the armor stand, its spawn location,
/// visibility, custom name, and other properties.
/// </remarks>
@Getter
public class ArmorStandSpawnEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    /// <summary>
    /// The armor stand that was spawned.
    /// </summary>
    private final ArmorStand armorStand;

    /// <summary>
    /// The location where the armor stand was spawned.
    /// </summary>
    private final Location location;

    /// <summary>
    /// Whether the armor stand is visible.
    /// </summary>
    private final boolean isVisible;

    /// <summary>
    /// Whether the armor stand's custom name is visible.
    /// </summary>
    private final boolean customNameVisible;

    /// <summary>
    /// The custom name of the armor stand, if any.
    /// </summary>
    private final String customName;

    /// <summary>
    /// Whether the armor stand can pick up items.
    /// </summary>
    private final boolean canPickupItems;

    /// <summary>
    /// Whether the armor stand is affected by gravity.
    /// </summary>
    private final boolean useGravity;

    /// <summary>
    /// Creates a new <see cref="ArmorStandSpawnEvent"/>.
    /// </summary>
    /// <param name="armorStand">The armor stand that was spawned.</param>
    /// <param name="location">The spawn location.</param>
    /// <param name="isVisible">Whether the armor stand is visible.</param>
    /// <param name="customNameVisible">Whether the armor stand's custom name is visible.</param>
    /// <param name="customName">The custom name of the armor stand.</param>
    /// <param name="canPickupItems">Whether the armor stand can pick up items.</param>
    /// <param name="useGravity">Whether the armor stand is affected by gravity.</param>
    public ArmorStandSpawnEvent(ArmorStand armorStand, Location location, boolean isVisible,
                                boolean customNameVisible, String customName,
                                boolean canPickupItems, boolean useGravity) {
        this.armorStand = armorStand;
        this.location = location;
        this.isVisible = isVisible;
        this.customNameVisible = customNameVisible;
        this.customName = customName;
        this.canPickupItems = canPickupItems;
        this.useGravity = useGravity;
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