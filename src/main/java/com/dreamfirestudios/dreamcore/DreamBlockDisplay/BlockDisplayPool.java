/*
 * MIT License
 *
 * Copyright (c) 2025 Dreamfire Studio
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
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
public final class BlockDisplayPool {

    /// <summary>
    /// Internal storage of pooled displays.
    /// </summary>
    private static final List<BlockDisplay> pool = new ArrayList<>();

    /// <summary>
    /// Hidden location for pooled displays. Defaults to the first loaded world, Y = -1000.
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
    /// <exception cref="IllegalArgumentException">Thrown if <paramref name="world"/>, <paramref name="location"/>, or <paramref name="blockData"/> is null.</exception>
    public static BlockDisplay acquire(World world, Location location, BlockData blockData) {
        if (world == null || location == null || blockData == null) {
            throw new IllegalArgumentException("World, location, and blockData cannot be null");
        }

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
    /// <remarks>
    /// The block data is reset to air, and the display is teleported to the hidden location.
    /// </remarks>
    public static void release(BlockDisplay display) {
        if (display == null || !display.isValid()) return;

        display.setBlock(Bukkit.createBlockData("minecraft:air"));
        if (HIDDEN_LOCATION.getWorld() != null) {
            display.teleport(HIDDEN_LOCATION);
        }
        pool.add(display);
    }

    /// <summary>
    /// Clears the entire pool and removes all pooled displays from the world.
    /// </summary>
    /// <remarks>
    /// Use this method on plugin shutdown to prevent entity leaks.
    /// </remarks>
    public static void clear() {
        for (BlockDisplay display : pool) {
            if (display.isValid()) {
                display.remove();
            }
        }
        pool.clear();
    }
}