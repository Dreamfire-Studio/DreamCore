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
package com.dreamfirestudios.dreamcore.DreamBlock;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;

/// <summary>
/// Utilities for synchronous and asynchronous (read-only) block queries
/// within a region (cube or sphere) around a center location.
/// </summary>
/// <remarks>
/// <para>
/// All synchronous methods that **modify** blocks (e.g., <see cref="replaceBlocksInRadius"/>,
/// <see cref="clearBlocksInRadius"/>) must be called on the main server thread.
/// </para>
/// <para>
/// Asynchronous wrappers are provided only for read-only operations and return results
/// via <see cref="CompletableFuture"/> without touching the world state.
/// </para>
/// </remarks>
public class DreamBlock {

    // ============================================================
    // Synchronous Methods
    // ============================================================

    /// <summary>
    /// Filters blocks within a region centered at <paramref name="location"/> that match <paramref name="condition"/>.
    /// </summary>
    /// <param name="location">Region center; its world must be non-null.</param>
    /// <param name="radius">Half-length of the search extent (in blocks).</param>
    /// <param name="step">Sampling step size in blocks; must be &gt; 0.</param>
    /// <param name="shape">Region shape: cube or sphere.</param>
    /// <param name="condition">Predicate to test each block; returning <c>true</c> includes it.</param>
    /// <returns>A list of blocks that satisfied <paramref name="condition"/>; empty if none or invalid inputs.</returns>
    /// <remarks>
    /// Iterates positions in the region using the given <paramref name="step"/> to reduce workload.
    /// For spherical searches, positions outside radius are skipped by squared-distance check.
    /// </remarks>
    /// <example>
    /// <code>
    /// var ores = DreamBlock.filterBlocksInRadius(player.getLocation(), 12, 1, RegionShape.SPHERE,
    ///     b -> b.getType().toString().endsWith("_ORE"));
    /// </code>
    /// </example>
    public static List<Block> filterBlocksInRadius(final Location location, final int radius, final int step,
                                                   final RegionShape shape, final Predicate<Block> condition) {
        List<Block> result = new ArrayList<>();
        final World world = location.getWorld();
        if (world == null || step <= 0) return result;

        final int cx = location.getBlockX();
        final int cy = location.getBlockY();
        final int cz = location.getBlockZ();

        final int r2 = radius * radius;
        for (int x = cx - radius; x < cx + radius; x += step) {
            for (int y = cy - radius; y < cy + radius; y += step) {
                for (int z = cz - radius; z < cz + radius; z += step) {
                    if (shape == RegionShape.SPHERE) {
                        int dx = x - cx, dy = y - cy, dz = z - cz;
                        if (dx * dx + dy * dy + dz * dz > r2) continue;
                    }
                    Block block = world.getBlockAt(x, y, z);
                    if (condition.test(block)) result.add(block);
                }
            }
        }
        return result;
    }

    /// <summary>
    /// Returns all blocks within a region centered at <paramref name="location"/>,
    /// optionally filtered by <paramref name="materials"/>.
    /// </summary>
    /// <param name="location">Region center; its world must be non-null.</param>
    /// <param name="radius">Half-length of the search extent (in blocks).</param>
    /// <param name="step">Sampling step size in blocks; must be &gt; 0.</param>
    /// <param name="shape">Region shape: cube or sphere.</param>
    /// <param name="materials">Optional material filter; when empty, all blocks are returned.</param>
    /// <returns>All matching blocks; empty list if none or invalid inputs.</returns>
    public static List<Block> returnAllBlocksInRadius(final Location location, final int radius, final int step,
                                                      final RegionShape shape, final Material... materials) {
        List<Block> result = new ArrayList<>();
        final World world = location.getWorld();
        if (world == null || step <= 0) return result;

        final int cx = location.getBlockX();
        final int cy = location.getBlockY();
        final int cz = location.getBlockZ();

        final int r2 = radius * radius;
        final boolean filterByMaterial = materials != null && materials.length > 0;
        final Set<Material> materialSet = filterByMaterial ? new HashSet<>(Arrays.asList(materials)) : null;

        for (int x = cx - radius; x < cx + radius; x += step) {
            for (int y = cy - radius; y < cy + radius; y += step) {
                for (int z = cz - radius; z < cz + radius; z += step) {
                    if (shape == RegionShape.SPHERE) {
                        int dx = x - cx, dy = y - cy, dz = z - cz;
                        if (dx * dx + dy * dy + dz * dz > r2) continue;
                    }
                    Block block = world.getBlockAt(x, y, z);
                    if (!filterByMaterial || materialSet.contains(block.getType())) result.add(block);
                }
            }
        }
        return result;
    }

    /// <summary>
    /// Replaces all blocks whose material is in <paramref name="targetMaterials"/> within the region
    /// with <paramref name="replacementMaterial"/>.
    /// </summary>
    /// <param name="location">Region center; its world must be non-null.</param>
    /// <param name="radius">Half-length of the search extent (in blocks).</param>
    /// <param name="step">Sampling step size in blocks; must be &gt; 0.</param>
    /// <param name="shape">Region shape: cube or sphere.</param>
    /// <param name="targetMaterials">Set of materials to replace.</param>
    /// <param name="replacementMaterial">Material to set when a target match is found.</param>
    /// <returns>The number of blocks replaced.</returns>
    /// <remarks>
    /// This method **modifies world state** and must be called on the main thread.
    /// Uses <c>setType(replacementMaterial, false)</c> to avoid physics updates.
    /// </remarks>
    public static int replaceBlocksInRadius(final Location location, final int radius, final int step,
                                            final RegionShape shape, final Material[] targetMaterials,
                                            final Material replacementMaterial) {
        final World world = location.getWorld();
        if (world == null || step <= 0) return 0;

        int replaced = 0;
        final int cx = location.getBlockX();
        final int cy = location.getBlockY();
        final int cz = location.getBlockZ();

        final int r2 = radius * radius;
        final Set<Material> targets = new HashSet<>(Arrays.asList(targetMaterials));

        for (int x = cx - radius; x < cx + radius; x += step) {
            for (int y = cy - radius; y < cy + radius; y += step) {
                for (int z = cz - radius; z < cz + radius; z += step) {
                    if (shape == RegionShape.SPHERE) {
                        int dx = x - cx, dy = y - cy, dz = z - cz;
                        if (dx * dx + dy * dy + dz * dz > r2) continue;
                    }
                    Block block = world.getBlockAt(x, y, z);
                    if (targets.contains(block.getType())) {
                        block.setType(replacementMaterial, false);
                        replaced++;
                    }
                }
            }
        }
        return replaced;
    }

    /// <summary>
    /// Counts blocks within the region. If <paramref name="materials"/> is supplied,
    /// counts only blocks whose type is in that set; otherwise counts all sampled positions.
    /// </summary>
    /// <param name="location">Region center; its world must be non-null.</param>
    /// <param name="radius">Half-length of the search extent (in blocks).</param>
    /// <param name="step">Sampling step size in blocks; must be &gt; 0.</param>
    /// <param name="shape">Region shape: cube or sphere.</param>
    /// <param name="materials">Optional material filter.</param>
    /// <returns>The number of blocks counted.</returns>
    public static int countBlocksInRadius(final Location location, final int radius, final int step,
                                          final RegionShape shape, final Material... materials) {
        final World world = location.getWorld();
        if (world == null || step <= 0) return 0;

        int count = 0;
        final int cx = location.getBlockX();
        final int cy = location.getBlockY();
        final int cz = location.getBlockZ();

        final int r2 = radius * radius;
        final boolean filterByMaterial = materials != null && materials.length > 0;
        final Set<Material> materialSet = filterByMaterial ? new HashSet<>(Arrays.asList(materials)) : null;

        for (int x = cx - radius; x < cx + radius; x += step) {
            for (int y = cy - radius; y < cy + radius; y += step) {
                for (int z = cz - radius; z < cz + radius; z += step) {
                    if (shape == RegionShape.SPHERE) {
                        int dx = x - cx, dy = y - cy, dz = z - cz;
                        if (dx * dx + dy * dy + dz * dz > r2) continue;
                    }
                    Block block = world.getBlockAt(x, y, z);
                    if (!filterByMaterial || materialSet.contains(block.getType())) count++;
                }
            }
        }
        return count;
    }

    /// <summary>
    /// Finds the closest block of <paramref name="material"/> within the region, or <c>null</c> if none.
    /// </summary>
    /// <param name="location">Region center; its world must be non-null.</param>
    /// <param name="radius">Half-length of the search extent (in blocks).</param>
    /// <param name="shape">Region shape: cube or sphere.</param>
    /// <param name="material">Target material to search for.</param>
    /// <returns>The nearest matching block, or <c>null</c> if not found.</returns>
    public static Block findClosestBlock(final Location location, final int radius,
                                         final RegionShape shape, final Material material) {
        final World world = location.getWorld();
        if (world == null) return null;

        Block closest = null;
        double bestDist = Double.MAX_VALUE;

        final int cx = location.getBlockX();
        final int cy = location.getBlockY();
        final int cz = location.getBlockZ();
        final int r2 = radius * radius;

        for (int x = cx - radius; x < cx + radius; x++) {
            for (int y = cy - radius; y < cy + radius; y++) {
                for (int z = cz - radius; z < cz + radius; z++) {
                    if (shape == RegionShape.SPHERE) {
                        int dx = x - cx, dy = y - cy, dz = z - cz;
                        if (dx * dx + dy * dy + dz * dz > r2) continue;
                    }
                    Block block = world.getBlockAt(x, y, z);
                    if (block.getType() == material) {
                        double dist = location.distanceSquared(block.getLocation());
                        if (dist < bestDist) {
                            bestDist = dist;
                            closest = block;
                        }
                    }
                }
            }
        }
        return closest;
    }

    /// <summary>
    /// Clears (sets to <see cref="Material#AIR"/>) all blocks within the region.
    /// If <paramref name="materials"/> is supplied, only those materials are cleared.
    /// </summary>
    /// <param name="location">Region center; its world must be non-null.</param>
    /// <param name="radius">Half-length of the search extent (in blocks).</param>
    /// <param name="step">Sampling step size in blocks; must be &gt; 0.</param>
    /// <param name="shape">Region shape: cube or sphere.</param>
    /// <param name="materials">Optional material filter for selective clearing.</param>
    /// <returns>The number of blocks cleared.</returns>
    /// <remarks>
    /// This method **modifies world state** and must be called on the main thread.
    /// Uses <c>setType(Material.AIR, false)</c> to avoid physics updates.
    /// </remarks>
    public static int clearBlocksInRadius(final Location location, final int radius, final int step,
                                          final RegionShape shape, final Material... materials) {
        final World world = location.getWorld();
        if (world == null || step <= 0) return 0;

        int cleared = 0;
        final int cx = location.getBlockX();
        final int cy = location.getBlockY();
        final int cz = location.getBlockZ();

        final int r2 = radius * radius;
        final boolean filterByMaterial = materials != null && materials.length > 0;
        final Set<Material> materialSet = filterByMaterial ? new HashSet<>(Arrays.asList(materials)) : null;

        for (int x = cx - radius; x < cx + radius; x += step) {
            for (int y = cy - radius; y < cy + radius; y += step) {
                for (int z = cz - radius; z < cz + radius; z += step) {
                    if (shape == RegionShape.SPHERE) {
                        int dx = x - cx, dy = y - cy, dz = z - cz;
                        if (dx * dx + dy * dy + dz * dz > r2) continue;
                    }
                    Block block = world.getBlockAt(x, y, z);
                    if (!filterByMaterial || materialSet.contains(block.getType())) {
                        block.setType(Material.AIR, false);
                        cleared++;
                    }
                }
            }
        }
        return cleared;
    }

    // ============================================================
    // Asynchronous Wrappers (Read-only)
    // ============================================================

    /// <summary>
    /// Asynchronously filters blocks within a region.
    /// </summary>
    /// <param name="location">Region center; its world must be non-null.</param>
    /// <param name="radius">Half-length of the search extent (in blocks).</param>
    /// <param name="step">Sampling step size in blocks; must be &gt; 0.</param>
    /// <param name="shape">Region shape: cube or sphere.</param>
    /// <param name="condition">Predicate used to filter blocks.</param>
    /// <returns>A future resolving to the filtered list.</returns>
    public static CompletableFuture<List<Block>> filterBlocksInRadiusAsync(final Location location, final int radius,
                                                                           final int step, final RegionShape shape,
                                                                           final Predicate<Block> condition) {
        return CompletableFuture.supplyAsync(() -> filterBlocksInRadius(location, radius, step, shape, condition));
    }

    /// <summary>
    /// Asynchronously returns all blocks within a region.
    /// </summary>
    /// <param name="location">Region center; its world must be non-null.</param>
    /// <param name="radius">Half-length of the search extent (in blocks).</param>
    /// <param name="step">Sampling step size in blocks; must be &gt; 0.</param>
    /// <param name="shape">Region shape: cube or sphere.</param>
    /// <param name="materials">Optional material filter.</param>
    /// <returns>A future resolving to the list of blocks.</returns>
    public static CompletableFuture<List<Block>> returnAllBlocksInRadiusAsync(final Location location, final int radius,
                                                                              final int step, final RegionShape shape,
                                                                              final Material... materials) {
        return CompletableFuture.supplyAsync(() -> returnAllBlocksInRadius(location, radius, step, shape, materials));
    }

    /// <summary>
    /// Asynchronously counts blocks with the specified materials within a region.
    /// </summary>
    /// <param name="location">Region center; its world must be non-null.</param>
    /// <param name="radius">Half-length of the search extent (in blocks).</param>
    /// <param name="step">Sampling step size in blocks; must be &gt; 0.</param>
    /// <param name="shape">Region shape: cube or sphere.</param>
    /// <param name="materials">Optional material filter.</param>
    /// <returns>A future resolving to the count.</returns>
    public static CompletableFuture<Integer> countBlocksInRadiusAsync(final Location location, final int radius,
                                                                      final int step, final RegionShape shape,
                                                                      final Material... materials) {
        return CompletableFuture.supplyAsync(() -> countBlocksInRadius(location, radius, step, shape, materials));
    }

    /// <summary>
    /// Asynchronously finds the closest block of the specified material within a region.
    /// </summary>
    /// <param name="location">Region center; its world must be non-null.</param>
    /// <param name="radius">Half-length of the search extent (in blocks).</param>
    /// <param name="shape">Region shape: cube or sphere.</param>
    /// <param name="material">Target material to search for.</param>
    /// <returns>A future resolving to the nearest matching block, or <c>null</c> if none.</returns>
    public static CompletableFuture<Block> findClosestBlockAsync(final Location location, final int radius,
                                                                 final RegionShape shape, final Material material) {
        return CompletableFuture.supplyAsync(() -> findClosestBlock(location, radius, shape, material));
    }
}