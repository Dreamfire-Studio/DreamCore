package com.dreamfirestudios.dreamCore.DreamfireEntityMask;

import com.dreamfirestudios.dreamCore.DreamCore;
import com.dreamfirestudios.dreamCore.DreamfireEntityMask.Event.EntityMaskCreateEvent;
import com.dreamfirestudios.dreamCore.DreamfireEntityMask.Event.EntityMaskPausedEvent;
import com.dreamfirestudios.dreamCore.DreamfireEntityMask.Event.EntityMaskStartedEvent;
import com.dreamfirestudios.dreamCore.DreamfireEntityMask.Event.EntityMaskStoppedEvent;
import com.dreamfirestudios.dreamCore.DreamfirePlayer.DreamfirePlayerAction;
import com.dreamfirestudios.dreamCore.DreamfirePlayer.DreamfirePlayerActionAPI;
import com.dreamfirestudios.dreamCore.DreamfireVanish.DreamfireVanish;
import lombok.Getter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.UUID;

public class DreamfireEntityMask {
    @Getter private Player player;
    private boolean deleteMaskOnNull;
    @Getter private double minDistance;
    @Getter private double maxDistance;
    private EntityMaskType entityMaskType;
    private ArrayList<EntityType> entityTypeExceptions;
    private ArrayList<UUID> uuidExceptions;
    private ArrayList<Entity> lastFrameEntities = new ArrayList<>();
    @Getter private boolean actionBarPaused = true;

    /**
     * Adds an entity type to the list of exceptions.
     *
     * @param entityType the entity type to be added
     */
    public void addToExceptions(EntityType entityType){
        if(!entityTypeExceptions.contains(entityType)) entityTypeExceptions.add(entityType);
    }

    /**
     * Adds an entity UUID to the list of exceptions.
     *
     * @param entityUUID the UUID of the entity to be added
     */
    public void addToExceptions(UUID entityUUID){
        if(!uuidExceptions.contains(entityUUID)) uuidExceptions.add(entityUUID);
    }

    /**
     * Displays the next frame of entities based on the mask configuration.
     */
    public void displayNextFrame(){
        if(player == null || !player.isOnline() || actionBarPaused) return;
        player.sendMessage("0");

        var newEntityStates = new ArrayList<Entity>();

        var playerWorld = player.getWorld();

        for(var entity : playerWorld.getEntities()){
            if(entityTypeExceptions.contains(entity.getType())) continue;
            if(uuidExceptions.contains(entity.getUniqueId())) continue;
            if(!isCorrectForMaskType(entity)) continue;
            var distanceToEntity = player.getLocation().distance(entity.getLocation());
            if(distanceToEntity < minDistance || distanceToEntity > maxDistance) continue;
            newEntityStates.add(entity);
        }

        for(var entity : lastFrameEntities) DreamfireVanish.showTargetToViewer(entity, player);
        for(var entity : newEntityStates) DreamfireVanish.hideTargetFromViewer(entity, player);
        lastFrameEntities = newEntityStates;
    }

    /**
     * Checks if an entity matches the current mask type.
     *
     * @param entity the entity to check
     * @return true if the entity matches the mask type
     */
    private boolean isCorrectForMaskType(Entity entity){
        if(entityMaskType == EntityMaskType.Entity) return true;
        if(entityMaskType == EntityMaskType.LivingEntity && entity instanceof LivingEntity) return true;
        if(entityMaskType == EntityMaskType.Player && entity instanceof Player) return true;
        return false;
    }

    /**
     * Pauses the mask action bar.
     */
    public void pause() {
        if(!actionBarPaused){
            actionBarPaused = true;
            new EntityMaskPausedEvent(this);
            for(var entity : lastFrameEntities) DreamfireVanish.showTargetToViewer(entity, player);
        }
    }

    /**
     * Starts the mask action bar.
     */
    public void play() {
        if(actionBarPaused){
            actionBarPaused = false;
            new EntityMaskStartedEvent(this);
        }
    }

    /**
     * Stops the entity mask and removes visibility restrictions.
     *
     * @return the stopped DreamfireEntityMask
     */
    public DreamfireEntityMask stop(){
        actionBarPaused = true;
        new EntityMaskStoppedEvent(this);
        for(var entity : lastFrameEntities) DreamfireVanish.showTargetToViewer(entity, player);
        return DreamCore.GetDreamfireCore().DeleteDreamfireEntityMask(player.getUniqueId());
    }

    public static class Builder {
        private boolean deleteMaskOnNull = false;
        private double minDistance = 0;
        private double maxDistance = 5;
        private EntityMaskType entityMaskType = EntityMaskType.Player;
        private ArrayList<UUID> uuidExceptions = new ArrayList<>();
        private ArrayList<EntityType> entityTypeExceptions = new ArrayList<>();

        /**
         * Sets the entity type exceptions.
         *
         * @param entityTypeExceptions the list of entity types to exclude
         * @return the Builder instance
         */
        public Builder entityTypeExceptions(ArrayList<EntityType> entityTypeExceptions){
            for(var type : entityTypeExceptions) if(!entityTypeExceptions.contains(type)) entityTypeExceptions.add(type);
            return this;
        }

        /**
         * Sets the UUID exceptions.
         *
         * @param uuidExceptions the list of UUIDs to exclude
         * @return the Builder instance
         */
        public Builder uuidExceptions(ArrayList<UUID> uuidExceptions){
            for(var type : entityTypeExceptions) if(!entityTypeExceptions.contains(type)) entityTypeExceptions.add(type);
            return this;
        }

        /**
         * Sets the flag to delete the mask when the player becomes null.
         *
         * @param deleteMaskOnNull true if the mask should be deleted when player is null
         * @return the Builder instance
         */
        public Builder deleteMaskOnNull(boolean deleteMaskOnNull){
            this.deleteMaskOnNull = deleteMaskOnNull;
            return this;
        }

        /**
         * Sets the minimum distance for entities to be included in the mask.
         *
         * @param minDistance the minimum distance
         * @return the Builder instance
         */
        public Builder minDistance(double minDistance){
            this.minDistance = minDistance;
            return this;
        }

        /**
         * Sets the maximum distance for entities to be included in the mask.
         *
         * @param maxDistance the maximum distance
         * @return the Builder instance
         */
        public Builder maxDistance(double maxDistance){
            this.maxDistance = maxDistance;
            return this;
        }

        /**
         * Sets the mask type for the entities.
         *
         * @param entityMaskType the mask type
         * @return the Builder instance
         */
        public Builder entityMaskType(EntityMaskType entityMaskType){
            this.entityMaskType = entityMaskType;
            return this;
        }

        /**
         * Creates the DreamfireEntityMask instance.
         *
         * @param player the player for the mask
         * @return the created DreamfireEntityMask
         */
        public DreamfireEntityMask CreateMask(Player player){
            if (player == null) throw new IllegalArgumentException("Player cannot be null");
            if(!DreamfirePlayerActionAPI.CanPlayerAction(DreamfirePlayerAction.PlayerEntityMask, player.getUniqueId())) return null;

            var storedEntityMask = DreamCore.GetDreamfireCore().GetDreamfireEntityMask(player.getUniqueId());
            if(storedEntityMask != null){
                entityTypeExceptions.forEach(storedEntityMask::addToExceptions);
                uuidExceptions.forEach(storedEntityMask::addToExceptions);
                return storedEntityMask;
            }

            var createdEntityMask = new DreamfireEntityMask();
            createdEntityMask.player = player;
            createdEntityMask.deleteMaskOnNull = deleteMaskOnNull;
            createdEntityMask.minDistance = minDistance;
            createdEntityMask.maxDistance = maxDistance;
            createdEntityMask.entityMaskType = entityMaskType;
            entityTypeExceptions.forEach(storedEntityMask::addToExceptions);
            uuidExceptions.forEach(storedEntityMask::addToExceptions);

            new EntityMaskCreateEvent(createdEntityMask, player);
            return DreamCore.GetDreamfireCore().AddDreamfireEntityMask(player.getUniqueId(), createdEntityMask);
        }
    }
}
