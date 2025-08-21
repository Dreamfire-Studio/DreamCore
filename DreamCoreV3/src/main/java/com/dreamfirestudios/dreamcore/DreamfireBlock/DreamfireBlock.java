package com.dreamfirestudios.dreamcore.DreamfireBlock;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;

/// <summary>
/// Utility class for synchronous and asynchronous block operations
/// in a defined radius and shape (cube or sphere).
/// </summary>
public class DreamfireBlock {

    // ============================================================
    // Synchronous Methods
    // ============================================================

    /// <summary>
    /// Filters blocks within a region centered at the given location that match the specified condition.
    /// </summary>
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
    /// Returns all blocks within a region centered at the given location.
    /// Optionally filters by materials.
    /// </summary>
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
    /// Replaces all blocks of the target materials within a region with the given replacement material.
    /// </summary>
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
    /// Counts the number of blocks matching the given materials within a region.
    /// If no materials are specified, counts all blocks.
    /// </summary>
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
    /// Finds the closest block of the given material within a region.
    /// </summary>
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
    /// Clears all blocks (sets them to AIR) within a region.
    /// Optionally filters by materials.
    /// </summary>
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
    public static CompletableFuture<List<Block>> filterBlocksInRadiusAsync(final Location location, final int radius,
                                                                           final int step, final RegionShape shape,
                                                                           final Predicate<Block> condition) {
        return CompletableFuture.supplyAsync(() -> filterBlocksInRadius(location, radius, step, shape, condition));
    }

    /// <summary>
    /// Asynchronously returns all blocks within a region.
    /// </summary>
    public static CompletableFuture<List<Block>> returnAllBlocksInRadiusAsync(final Location location, final int radius,
                                                                              final int step, final RegionShape shape,
                                                                              final Material... materials) {
        return CompletableFuture.supplyAsync(() -> returnAllBlocksInRadius(location, radius, step, shape, materials));
    }

    /// <summary>
    /// Asynchronously counts blocks with specified materials within a region.
    /// </summary>
    public static CompletableFuture<Integer> countBlocksInRadiusAsync(final Location location, final int radius,
                                                                      final int step, final RegionShape shape,
                                                                      final Material... materials) {
        return CompletableFuture.supplyAsync(() -> countBlocksInRadius(location, radius, step, shape, materials));
    }

    /// <summary>
    /// Asynchronously finds the closest block of the specified material within a region.
    /// </summary>
    public static CompletableFuture<Block> findClosestBlockAsync(final Location location, final int radius,
                                                                 final RegionShape shape, final Material material) {
        return CompletableFuture.supplyAsync(() -> findClosestBlock(location, radius, shape, material));
    }
}