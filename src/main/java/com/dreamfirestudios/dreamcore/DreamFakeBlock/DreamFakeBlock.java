package com.dreamfirestudios.dreamcore.DreamFakeBlock;

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

/**
 * <summary>
 * Represents a single fake block at a given location for one or more observers.
 * Observers will see this fake block instead of the real world block, until removed.
 * </summary>
 *
 * <remarks>
 * Internally uses {@link Player#sendBlockChange(Location, org.bukkit.block.data.BlockData)}
 * which is supported on both Bukkit and Paper.
 * </remarks>
 */
public class DreamFakeBlock {
    @Getter private final Location location;
    private Material material;
    private final Set<Player> observers = new HashSet<>();

    /** <returns>The current fake block material.</returns> */
    public Material getMaterial(){ return material; }

    /**
     * <summary>Creates a new fake block at a location with a specific material.</summary>
     *
     * <param name="location">World location of the fake block.</param>
     * <param name="material">Material to render to observers.</param>
     * <exception cref="IllegalArgumentException">If location or material is null.</exception>
     */
    public DreamFakeBlock(Location location, Material material) {
        if (location == null || material == null) throw new IllegalArgumentException("Location and material cannot be null");
        this.location = location;
        this.material = material;
    }

    /**
     * <summary>Adds a player to the observer list and sends them the fake block.</summary>
     *
     * <param name="player">The player to add as observer.</param>
     */
    public void addObserver(Player player) {
        if (player == null) throw new IllegalArgumentException("Player cannot be null");
        player.sendBlockChange(location, material.createBlockData());
        observers.add(player);
        new FakeBlockObserverAddedEvent(this, player);
    }

    /**
     * <summary>Removes a player from observers and restores the real block to them.</summary>
     *
     * <param name="player">The player to remove.</param>
     */
    public void removeObserver(Player player) {
        if (player == null) throw new IllegalArgumentException("Player cannot be null");
        player.sendBlockChange(location, location.getBlock().getType().createBlockData());
        observers.remove(player);
        new FakeBlockObserverRemovedEvent(this, player);
    }

    /**
     * <summary>Removes all observers, restoring the world block to them.</summary>
     */
    public void removeAllObservers() {
        for (Player player : new HashSet<>(observers)) {
            removeObserver(player);
        }
        observers.clear();
        new FakeBlockClearedEvent(this);
    }

    /**
     * <summary>Checks if a player is observing this block at a given location.</summary>
     *
     * <param name="player">The player to check.</param>
     * <param name="location">The location to compare against.</param>
     * <returns>True if the player is observing this block at that location.</returns>
     */
    public boolean isPlayerObservingAtLocation(Player player, Location location) {
        if (player == null || location == null) throw new IllegalArgumentException("Player and location cannot be null");
        return observers.contains(player) && this.location.equals(location);
    }

    /**
     * <summary>Checks if a given location matches this fake block’s location.</summary>
     *
     * <param name="location">The location to check.</param>
     * <returns>True if same location, otherwise false.</returns>
     */
    public boolean isLocation(Location location) {
        return this.location.equals(location);
    }

    /**
     * <summary>Updates the material of the fake block for all observers.</summary>
     *
     * <param name="newMaterial">The new material.</param>
     */
    public void updateMaterialForAllObservers(Material newMaterial) {
        if (newMaterial == null) throw new IllegalArgumentException("Material cannot be null");
        this.material = newMaterial;
        for (Player player : observers) {
            player.sendBlockChange(location, newMaterial.createBlockData());
        }
        new FakeBlockUpdatedEvent(this, newMaterial);
    }

    /**
     * <summary>Re-sends the current block state to all observers (e.g. for a frame update).</summary>
     */
    public void displayNextFrame() {
        for (Player player : observers) {
            player.sendBlockChange(location, material.createBlockData());
        }
    }

    /** <returns>The number of players observing this fake block.</returns> */
    public int getObserverCount() {
        return observers.size();
    }
}