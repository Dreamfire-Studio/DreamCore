package com.dreamfirestudios.dreamcore.DreamBlockDisplay;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.BlockDisplay;

import java.util.ArrayList;
import java.util.List;

/// <summary>
/// A simple pool for <see cref="BlockDisplay"/> entities to reduce the overhead
/// of frequent spawning and removal.
/// </summary>
/// <remarks>
/// Released displays are teleported to a hidden location (default world, Y = -1000) and
/// stored for later reuse. When re-acquired, the display is teleported to the desired
/// location and its block data is updated.
/// </remarks>
public class BlockDisplayPool {

    /// <summary>
    /// Internal storage of pooled displays.
    /// </summary>
    private static final List<BlockDisplay> pool = new ArrayList<>();

    /// <summary>
    /// Hidden location for pooled displays. Defaults to world "world", Y = -1000.
    /// </summary>
    private static final Location HIDDEN_LOCATION =
            new Location(Bukkit.getWorlds().isEmpty() ? null : Bukkit.getWorlds().get(0), 0, -1000, 0);

    /// <summary>
    /// Acquires a <see cref="BlockDisplay"/> from the pool if available, or spawns a new one.
    /// </summary>
    /// <param name="world">The world in which the display should exist.</param>
    /// <param name="location">The desired location for the display.</param>
    /// <param name="blockData">The <see cref="BlockData"/> to set on the display.</param>
    /// <returns>A <see cref="BlockDisplay"/> instance ready for use.</returns>
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

    /// <summary>
    /// Releases a <see cref="BlockDisplay"/> back into the pool for later reuse.
    /// </summary>
    /// <param name="display">The display to release.</param>
    public static void release(BlockDisplay display) {
        if (display == null || !display.isValid()) return;

        // Reset block data (optional, avoids players seeing old state if reused later).
        display.setBlock(Bukkit.createBlockData("minecraft:air"));

        // Teleport to a hidden location so it isn’t visible to players.
        if (HIDDEN_LOCATION.getWorld() != null) {
            display.teleport(HIDDEN_LOCATION);
        }

        pool.add(display);
    }

    /// <summary>
    /// Clears the entire pool and removes all pooled displays from the world.
    /// </summary>
    public static void clear() {
        for (BlockDisplay display : pool) {
            if (display.isValid()) {
                display.remove();
            }
        }
        pool.clear();
    }
}