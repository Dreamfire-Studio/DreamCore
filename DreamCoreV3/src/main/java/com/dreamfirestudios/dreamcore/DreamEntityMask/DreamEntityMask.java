package com.dreamfirestudios.dreamcore.DreamEntityMask;

import com.dreamfirestudios.dreamcore.DreamCore;
import com.dreamfirestudios.dreamcore.DreamVanish.DreamVanish;
import lombok.Getter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Represents a per-player entity visibility mask.
 * A mask controls which entities are hidden or shown to a player,
 * depending on distance, type, and exceptions.
 */
public class DreamEntityMask {

    @Getter private Player player;
    private boolean deleteMaskOnNull;
    @Getter private double minDistance;
    @Getter private double maxDistance;
    private EntityMaskType entityMaskType;
    private final List<EntityType> entityTypeExceptions = new ArrayList<>();
    private final List<UUID> uuidExceptions = new ArrayList<>();
    private List<Entity> lastFrameEntities = new ArrayList<>();
    @Getter private boolean actionBarPaused = true;

    /**
     * Adds an entity type to the list of exceptions (always visible).
     *
     * @param entityType the entity type to add
     */
    public void addToExceptions(EntityType entityType){
        if (!entityTypeExceptions.contains(entityType)) {
            entityTypeExceptions.add(entityType);
        }
    }

    /**
     * Adds an entity UUID to the list of exceptions (always visible).
     *
     * @param entityUUID the entity UUID to add
     */
    public void addToExceptions(UUID entityUUID){
        if (!uuidExceptions.contains(entityUUID)) {
            uuidExceptions.add(entityUUID);
        }
    }

    /**
     * Updates entity visibility for this player based on mask configuration.
     * Entities in the new frame are hidden, and entities no longer matched are shown again.
     */
    public void displayNextFrame(){
        if (player == null || !player.isOnline() || actionBarPaused) return;

        var newEntityStates = new ArrayList<Entity>();

        var playerWorld = player.getWorld();
        for (var entity : playerWorld.getEntities()){
            if (entityTypeExceptions.contains(entity.getType())) continue;
            if (uuidExceptions.contains(entity.getUniqueId())) continue;
            if (!isCorrectForMaskType(entity)) continue;

            double distance = player.getLocation().distance(entity.getLocation());
            if (distance < minDistance || distance > maxDistance) continue;

            newEntityStates.add(entity);
        }

        // Reveal old entities, hide new ones
        for (var entity : lastFrameEntities) DreamVanish.showTargetToViewer(entity, player);
        for (var entity : newEntityStates) DreamVanish.hideTargetFromViewer(entity, player);

        lastFrameEntities = newEntityStates;
    }

    /**
     * Checks if an entity matches the current mask type filter.
     *
     * @param entity the entity to check
     * @return true if valid for this mask type
     */
    private boolean isCorrectForMaskType(Entity entity){
        return switch (entityMaskType) {
            case Entity -> true;
            case LivingEntity -> entity instanceof LivingEntity;
            case Player -> entity instanceof Player;
        };
    }

    /**
     * Pauses the mask. Entities will become visible again until resumed.
     */
    public void pause() {
        if (!actionBarPaused){
            actionBarPaused = true;
            new EntityMaskPausedEvent(this);
            for (var entity : lastFrameEntities) DreamVanish.showTargetToViewer(entity, player);
        }
    }

    /**
     * Resumes the mask. Entities matching the mask rules will be hidden again.
     */
    public void play() {
        if (actionBarPaused){
            actionBarPaused = false;
            new EntityMaskStartedEvent(this);
        }
    }

    /**
     * Stops the mask completely and restores visibility to all entities.
     *
     * @return the removed entity mask from DreamCore
     */
    public DreamEntityMask stop(){
        actionBarPaused = true;
        new EntityMaskStoppedEvent(this);
        for (var entity : lastFrameEntities) DreamVanish.showTargetToViewer(entity, player);

        return DreamCore.DreamEntityMasks.remove(player.getUniqueId());
    }

    // -------------------------------------------------
    // Builder pattern for creating entity masks
    // -------------------------------------------------
    public static class Builder {
        private boolean deleteMaskOnNull = false;
        private double minDistance = 0;
        private double maxDistance = 5;
        private EntityMaskType entityMaskType = EntityMaskType.Player;
        private final List<UUID> uuidExceptions = new ArrayList<>();
        private final List<EntityType> entityTypeExceptions = new ArrayList<>();

        /**
         * Adds entity type exceptions to this mask.
         */
        public Builder entityTypeExceptions(List<EntityType> exceptions){
            for (var type : exceptions) {
                if (!entityTypeExceptions.contains(type)) entityTypeExceptions.add(type);
            }
            return this;
        }

        /**
         * Adds entity UUID exceptions to this mask.
         */
        public Builder uuidExceptions(List<UUID> exceptions){
            for (var id : exceptions) {
                if (!uuidExceptions.contains(id)) uuidExceptions.add(id);
            }
            return this;
        }

        public Builder deleteMaskOnNull(boolean deleteMaskOnNull){
            this.deleteMaskOnNull = deleteMaskOnNull;
            return this;
        }

        public Builder minDistance(double minDistance){
            this.minDistance = minDistance;
            return this;
        }

        public Builder maxDistance(double maxDistance){
            this.maxDistance = maxDistance;
            return this;
        }

        public Builder entityMaskType(EntityMaskType entityMaskType){
            this.entityMaskType = entityMaskType;
            return this;
        }

        /**
         * Builds and registers the entity mask for a player.
         */
        public DreamEntityMask CreateMask(Player player){
            if (player == null) throw new IllegalArgumentException("Player cannot be null");

            var storedMask = DreamCore.DreamEntityMasks.getOrDefault(player.getUniqueId(), null);
            if (storedMask != null){
                entityTypeExceptions.forEach(storedMask::addToExceptions);
                uuidExceptions.forEach(storedMask::addToExceptions);
                return storedMask;
            }

            var createdMask = new DreamEntityMask();
            createdMask.player = player;
            createdMask.deleteMaskOnNull = deleteMaskOnNull;
            createdMask.minDistance = minDistance;
            createdMask.maxDistance = maxDistance;
            createdMask.entityMaskType = entityMaskType;

            entityTypeExceptions.forEach(createdMask::addToExceptions);
            uuidExceptions.forEach(createdMask::addToExceptions);

            new EntityMaskCreateEvent(createdMask, player);
            return DreamCore.DreamEntityMasks.put(player.getUniqueId(), createdMask);
        }
    }
}