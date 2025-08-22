package com.dreamfirestudios.dreamcore.DreamInventory;

/**
 * Logical locations for items when scanning entities/players.
 */
public enum ItemLocation {
    ENTITY_MAIN_HAND,
    ENTITY_OFF_HAND,
    ENTITY_ARMOR,        // Generic armor bucket (see slot index for which)
    ENTITY_INVENTORY,    // Any inventory slot (backpack/hotbar)
    BROKEN_ITEM,         // Logical marker if you want to tag results as broken
    CONTAINER,           // For future: chest/barrel scans
    DROPPED_ITEM         // For future: world item entities
}
