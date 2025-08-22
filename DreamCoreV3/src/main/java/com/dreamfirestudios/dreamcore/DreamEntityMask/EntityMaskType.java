package com.dreamfirestudios.dreamcore.DreamEntityMask;

/**
 * Defines the filter scope for an entity mask.
 */
public enum EntityMaskType {
    /** Match all entities regardless of type. */
    Entity,
    /** Match only living entities (mobs, animals, players). */
    LivingEntity,
    /** Match only players. */
    Player
}