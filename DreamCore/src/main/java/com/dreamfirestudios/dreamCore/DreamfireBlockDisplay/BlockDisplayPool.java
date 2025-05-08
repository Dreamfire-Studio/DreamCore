package com.dreamfirestudios.dreamCore.DreamfireBlockDisplay;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.BlockDisplay;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple pool for BlockDisplay entities to reduce the overhead of frequent spawning and removal.
 *
 * <p>
 * Released displays are teleported to a hidden location (here, Y=-1000 in the default world) and stored for later reuse.
 * </p>
 */
public class BlockDisplayPool {

    private static final List<BlockDisplay> pool = new ArrayList<>();
    // Hidden location for pooled displays.
    private static final Location HIDDEN_LOCATION = new Location(Bukkit.getWorld("world"), 0, -1000, 0);

    /**
     * Acquires a BlockDisplay from the pool if available, or spawns a new one.
     *
     * @param world     The world in which the display should exist.
     * @param location  The desired location for the display.
     * @param blockData The BlockData to set on the display.
     * @return A BlockDisplay instance ready for use.
     */
    public static BlockDisplay acquire(World world, Location location, BlockData blockData) {
        BlockDisplay display;
        if (!pool.isEmpty()) {
            display = pool.remove(pool.size() - 1);
            display.teleport(location);
            display.setBlock(blockData);
        } else {
            display = world.spawn(location, BlockDisplay.class, bd -> bd.setBlock(blockData));
        }
        return display;
    }

    /**
     * Releases a BlockDisplay back into the pool for later reuse.
     *
     * @param display The BlockDisplay to release.
     */
    public static void release(BlockDisplay display) {
        // Optionally reset state as needed.
        // Teleport to a hidden location so it isn’t visible to players.
        display.teleport(HIDDEN_LOCATION);
        pool.add(display);
    }

    /**
     * Clears the entire pool and removes all pooled BlockDisplay entities from the world.
     */
    public static void clear() {
        for (BlockDisplay display : pool) {
            display.remove();
        }
        pool.clear();
    }
}