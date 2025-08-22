package com.dreamfirestudios.dreamcore.DreamInventory;

/// <summary>
/// Logical locations for items when scanning entities or players.
/// </summary>
/// <remarks>
/// Provides classification beyond raw inventory indices (e.g., hands, armor).
/// Useful for search/filter logic when working with <see cref="ItemRef"/>.
/// </remarks>
public enum ItemLocation {
    /// <summary>Entity’s main hand (slot = <see cref="DreamInventory.SLOT_MAIN_HAND"/>).</summary>
    ENTITY_MAIN_HAND,

    /// <summary>Entity’s off hand (slot = <see cref="DreamInventory.SLOT_OFF_HAND"/>).</summary>
    ENTITY_OFF_HAND,

    /// <summary>Any armor slot (see slot index for which).</summary>
    ENTITY_ARMOR,

    /// <summary>Any backpack/hotbar slot.</summary>
    ENTITY_INVENTORY,

    /// <summary>Logical marker for broken items (helper classification).</summary>
    BROKEN_ITEM,

    /// <summary>Future expansion: container slots (e.g., chest, barrel).</summary>
    CONTAINER,

    /// <summary>Future expansion: dropped item entities in the world.</summary>
    DROPPED_ITEM
}