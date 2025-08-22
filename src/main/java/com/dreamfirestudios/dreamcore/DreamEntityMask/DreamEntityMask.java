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
 /// <summary>
 /// Represents a per-player entity visibility mask.
 /// </summary>
 /// <remarks>
 /// A mask controls which entities are hidden or shown to a player,
 /// depending on distance, entity type, and defined exceptions.
 /// Lifecycle: {@link #play()}, {@link #pause()}, and {@link #stop()}.
 /// </remarks>
 */
public class DreamEntityMask {

    /// <summary>Player that this mask applies to.</summary>
    @Getter private Player player;

    /// <summary>If true, mask deletes itself when player reference is null.</summary>
    private boolean deleteMaskOnNull;

    /// <summary>Minimum allowed distance to hide entities.</summary>
    @Getter private double minDistance;

    /// <summary>Maximum allowed distance to hide entities.</summary>
    @Getter private double maxDistance;

    /// <summary>Filter scope for which entities are affected.</summary>
    private EntityMaskType entityMaskType;

    /// <summary>Exceptions by entity type (these remain visible).</summary>
    private final List<EntityType> entityTypeExceptions = new ArrayList<>();

    /// <summary>Exceptions by entity UUID (these remain visible).</summary>
    private final List<UUID> uuidExceptions = new ArrayList<>();

    /// <summary>Snapshot of entities hidden in the last frame.</summary>
    private List<Entity> lastFrameEntities = new ArrayList<>();

    /// <summary>True if the mask is currently paused.</summary>
    @Getter private boolean actionBarPaused = true;

    // ---------------- Exceptions ----------------

    /**
     /// <summary>
     /// Adds an entity type to the list of exceptions (always visible).
     /// </summary>
     /// <param name="entityType">The entity type to add.</param>
     */
    public void addToExceptions(EntityType entityType){
        if (!entityTypeExceptions.contains(entityType)) {
            entityTypeExceptions.add(entityType);
        }
    }

    /**
     /// <summary>
     /// Adds an entity UUID to the list of exceptions (always visible).
     /// </summary>
     /// <param name="entityUUID">The entity UUID to add.</param>
     */
    public void addToExceptions(UUID entityUUID){
        if (!uuidExceptions.contains(entityUUID)) {
            uuidExceptions.add(entityUUID);
        }
    }

    // ---------------- Frame update ----------------

    /**
     /// <summary>
     /// Updates entity visibility for this player based on mask configuration.
     /// </summary>
     /// <remarks>
     /// - Entities matching the mask rules are hidden.
     /// - Entities no longer matched are revealed again.
     /// This should be called periodically (e.g., per tick).
     /// </remarks>
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
     /// <summary>
     /// Checks if an entity matches the current mask type filter.
     /// </summary>
     /// <param name="entity">The entity to check.</param>
     /// <returns><c>true</c> if the entity is valid for this mask type.</returns>
     */
    private boolean isCorrectForMaskType(Entity entity){
        return switch (entityMaskType) {
            case Entity -> true;
            case LivingEntity -> entity instanceof LivingEntity;
            case Player -> entity instanceof Player;
        };
    }

    // ---------------- Lifecycle ----------------

    /**
     /// <summary>
     /// Pauses the mask. Entities will become visible again until resumed.
     /// </summary>
     */
    public void pause() {
        if (!actionBarPaused){
            actionBarPaused = true;
            new EntityMaskPausedEvent(this);
            for (var entity : lastFrameEntities) DreamVanish.showTargetToViewer(entity, player);
        }
    }

    /**
     /// <summary>
     /// Resumes the mask. Entities matching the mask rules will be hidden again.
     /// </summary>
     */
    public void play() {
        if (actionBarPaused){
            actionBarPaused = false;
            new EntityMaskStartedEvent(this);
        }
    }

    /**
     /// <summary>
     /// Stops the mask completely and restores visibility to all entities.
     /// </summary>
     /// <returns>The removed entity mask from <see cref="DreamCore"/>.</returns>
     */
    public DreamEntityMask stop(){
        actionBarPaused = true;
        new EntityMaskStoppedEvent(this);
        for (var entity : lastFrameEntities) DreamVanish.showTargetToViewer(entity, player);

        return DreamCore.DreamEntityMasks.remove(player.getUniqueId());
    }

    // ---------------- Builder ----------------

    /**
     /// <summary>
     /// Builder pattern for creating <see cref="DreamEntityMask"/> instances.
     /// </summary>
     */
    public static class Builder {
        private boolean deleteMaskOnNull = false;
        private double minDistance = 0;
        private double maxDistance = 5;
        private EntityMaskType entityMaskType = EntityMaskType.Player;
        private final List<UUID> uuidExceptions = new ArrayList<>();
        private final List<EntityType> entityTypeExceptions = new ArrayList<>();

        /**
         /// <summary>Adds entity type exceptions to this mask.</summary>
         */
        public Builder entityTypeExceptions(List<EntityType> exceptions){
            for (var type : exceptions) {
                if (!entityTypeExceptions.contains(type)) entityTypeExceptions.add(type);
            }
            return this;
        }

        /**
         /// <summary>Adds entity UUID exceptions to this mask.</summary>
         */
        public Builder uuidExceptions(List<UUID> exceptions){
            for (var id : exceptions) {
                if (!uuidExceptions.contains(id)) uuidExceptions.add(id);
            }
            return this;
        }

        /// <summary>Configures whether to delete mask on null player reference.</summary>
        public Builder deleteMaskOnNull(boolean deleteMaskOnNull){
            this.deleteMaskOnNull = deleteMaskOnNull;
            return this;
        }

        /// <summary>Sets the minimum distance for the mask.</summary>
        public Builder minDistance(double minDistance){
            this.minDistance = minDistance;
            return this;
        }

        /// <summary>Sets the maximum distance for the mask.</summary>
        public Builder maxDistance(double maxDistance){
            this.maxDistance = maxDistance;
            return this;
        }

        /// <summary>Sets the mask type filter.</summary>
        public Builder entityMaskType(EntityMaskType entityMaskType){
            this.entityMaskType = entityMaskType;
            return this;
        }

        /**
         /// <summary>
         /// Builds and registers the entity mask for a player.
         /// </summary>
         /// <param name="player">Target player.</param>
         /// <returns>The created or existing mask instance.</returns>
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