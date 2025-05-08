package com.dreamfirestudios.dreamCore.DreamfireFakeBlock;

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

public class DreamfireFakeBlock {
    @Getter private final Location location;
    private Material material;
    private Set<Player> observers = new HashSet<>();

    public Material getMaterial(){return material;}

    /**
     * Constructor for creating a fake block at a specific location with a material.
     *
     * @param location The location of the fake block.
     * @param material The material for the fake block.
     * @throws IllegalArgumentException If the location or material is null.
     */
    public DreamfireFakeBlock(Location location, Material material) {
        if (location == null || material == null) throw new IllegalArgumentException("Location and material cannot be null");
        this.location = location;
        this.material = material;
    }

    /**
     * Adds a player as an observer of the fake block. The player will see the fake block at the specified location.
     *
     * @param player The player to add as an observer.
     * @throws IllegalArgumentException If the player is null.
     */
    public void addObserver(Player player) {
        if (player == null) throw new IllegalArgumentException("Player cannot be null");
        player.sendBlockChange(location, material.createBlockData());
        observers.add(player);
    }

    /**
     * Removes a player from the observers of the fake block. The player will stop seeing the fake block at the location.
     *
     * @param player The player to remove.
     * @throws IllegalArgumentException If the player is null.
     */
    public void removeObserver(Player player) {
        if (player == null) throw new IllegalArgumentException("Player cannot be null");
        player.sendBlockChange(location, location.getBlock().getType().createBlockData());
        observers.remove(player);
    }


    /**
     * Removes all players from the observers list. All observers will stop seeing the fake block.
     */
    public void removeAllObservers() {
        for (Player player : observers) removeObserver(player);
        observers.clear();
    }

    /**
     * Checks if a player is observing the block at a specific location.
     *
     * @param player The player to check.
     * @param location The location to check.
     * @return True if the player is observing the block at the given location, false otherwise.
     * @throws IllegalArgumentException If the player or location is null.
     */
    public boolean isPlayerObservingAtLocation(Player player, Location location) {
        if (player == null || location == null) throw new IllegalArgumentException("Player and location cannot be null");
        return observers.contains(player) && this.location.equals(location);
    }

    /**
     * Checks if a location is the same as the location of this fake block.
     *
     * @param location The location to check.
     * @return True if the given location matches this fake block's location, false otherwise.
     */
    public boolean isLocation(Location location) {
        return this.location.equals(location);
    }

    /**
     * Updates the material of the fake block for all observers. All players observing this block will see the new material.
     *
     * @param newMaterial The new material to set for the fake block.
     * @throws IllegalArgumentException If the new material is null.
     */
    public void updateMaterialForAllObservers(Material newMaterial) {
        this.material = newMaterial;
        // Notify all observers of the material change
        for (Player player : observers) {
            player.sendBlockChange(location, newMaterial.createBlockData());
        }
    }

    /**
     * Sends the current fake block state (material) to all observers.
     */
    public void DisplayNextFrame() {
        for (Player player : observers) {
            player.sendBlockChange(location, material.createBlockData());
        }
    }

    /**
     * Retrieves the number of observers currently watching the fake block.
     *
     * @return The number of players observing the fake block.
     */
    public int getObserverCount() {
        return observers.size();
    }
}
