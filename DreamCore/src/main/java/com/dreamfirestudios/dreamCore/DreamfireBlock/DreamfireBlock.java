package com.dreamfirestudios.dreamCore.DreamfireBlock;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;

/**
 * Utility class for operating on blocks within a region.
 */
public class DreamfireBlock {

    // ============================================================
    // Synchronous Methods (Works on main thread)
    // ============================================================

    /**
     * Filters blocks within a region centered at the given location that match the specified condition.
     *
     * @param location  The center location.
     * @param radius    The radius (in blocks) from the center.
     * @param step      The interval between checked blocks (1 = every block, 2 = every other block, etc.).
     * @param shape     The region shape (CUBE or SPHERE).
     * @param condition A predicate to test whether a block should be included.
     * @return A list of blocks that satisfy the condition.
     */
    public static List<Block> filterBlocksInRadius(final Location location, final int radius, final int step, final RegionShape shape, final Predicate<Block> condition) {
        List<Block> result = new ArrayList<>();
        final World world = location.getWorld();
        if (world == null || step <= 0) return result;

        final int centerX = location.getBlockX();
        final int centerY = location.getBlockY();
        final int centerZ = location.getBlockZ();

        final int startX = centerX - radius;
        final int endX = centerX + radius;
        final int startY = centerY - radius;
        final int endY = centerY + radius;
        final int startZ = centerZ - radius;
        final int endZ = centerZ + radius;

        final int radiusSquared = radius * radius;
        for (int x = startX; x < endX; x += step) {
            for (int y = startY; y < endY; y += step) {
                for (int z = startZ; z < endZ; z += step) {
                    // If using a spherical region, only process blocks within the sphere.
                    if (shape == RegionShape.SPHERE) {
                        int dx = x - centerX;
                        int dy = y - centerY;
                        int dz = z - centerZ;
                        if (dx * dx + dy * dy + dz * dz > radiusSquared) {
                            continue;
                        }
                    }
                    Block block = world.getBlockAt(x, y, z);
                    if (condition.test(block)) {
                        result.add(block);
                    }
                }
            }
        }
        return result;
    }

    /**
     * Returns all blocks within a region centered at the given location.
     *
     * @param location  The center location.
     * @param radius    The radius (in blocks) from the center.
     * @param step      The interval between checked blocks.
     * @param shape     The region shape (CUBE or SPHERE).
     * @param materials Optional. The materials to include. If empty, all blocks are included.
     * @return A list of blocks matching the criteria.
     */
    public static List<Block> returnAllBlocksInRadius(final Location location, final int radius, final int step, final RegionShape shape, final Material... materials) {
        List<Block> result = new ArrayList<>();
        final World world = location.getWorld();
        if (world == null || step <= 0) return result;

        final int centerX = location.getBlockX();
        final int centerY = location.getBlockY();
        final int centerZ = location.getBlockZ();

        final int startX = centerX - radius;
        final int endX = centerX + radius;
        final int startY = centerY - radius;
        final int endY = centerY + radius;
        final int startZ = centerZ - radius;
        final int endZ = centerZ + radius;

        final int radiusSquared = radius * radius;
        final boolean filterByMaterial = materials != null && materials.length > 0;
        final Set<Material> materialSet = filterByMaterial ? new HashSet<>(Arrays.asList(materials)) : null;

        for (int x = startX; x < endX; x += step) {
            for (int y = startY; y < endY; y += step) {
                for (int z = startZ; z < endZ; z += step) {
                    if (shape == RegionShape.SPHERE) {
                        int dx = x - centerX;
                        int dy = y - centerY;
                        int dz = z - centerZ;
                        if (dx * dx + dy * dy + dz * dz > radiusSquared) {
                            continue;
                        }
                    }
                    Block block = world.getBlockAt(x, y, z);
                    if (!filterByMaterial || materialSet.contains(block.getType())) {
                        result.add(block);
                    }
                }
            }
        }
        return result;
    }

    /**
     * Replaces all blocks of specified target materials within a region with a replacement material.
     *
     * @param location            The center location.
     * @param radius              The radius (in blocks) from the center.
     * @param step                The interval between checked blocks.
     * @param shape               The region shape (CUBE or SPHERE).
     * @param targetMaterials     The materials to be replaced.
     * @param replacementMaterial The material to replace with.
     * @return The number of blocks replaced.
     */
    public static int replaceBlocksInRadius(final Location location, final int radius, final int step, final RegionShape shape, final Material[] targetMaterials, final Material replacementMaterial) {
        final World world = location.getWorld();
        if (world == null || step <= 0) return 0;

        int replacedCount = 0;
        final int centerX = location.getBlockX();
        final int centerY = location.getBlockY();
        final int centerZ = location.getBlockZ();

        final int startX = centerX - radius;
        final int endX = centerX + radius;
        final int startY = centerY - radius;
        final int endY = centerY + radius;
        final int startZ = centerZ - radius;
        final int endZ = centerZ + radius;

        final int radiusSquared = radius * radius;
        final Set<Material> targetSet = new HashSet<>(Arrays.asList(targetMaterials));

        for (int x = startX; x < endX; x += step) {
            for (int y = startY; y < endY; y += step) {
                for (int z = startZ; z < endZ; z += step) {
                    if (shape == RegionShape.SPHERE) {
                        int dx = x - centerX;
                        int dy = y - centerY;
                        int dz = z - centerZ;
                        if (dx * dx + dy * dy + dz * dz > radiusSquared) {
                            continue;
                        }
                    }
                    Block block = world.getBlockAt(x, y, z);
                    if (targetSet.contains(block.getType())) {
                        // The second parameter (applyPhysics) is false for efficiency.
                        block.setType(replacementMaterial, false);
                        replacedCount++;
                    }
                }
            }
        }
        return replacedCount;
    }

    /**
     * Counts the number of blocks with specified materials within a region.
     *
     * @param location  The center location.
     * @param radius    The radius (in blocks) from the center.
     * @param step      The interval between checked blocks.
     * @param shape     The region shape (CUBE or SPHERE).
     * @param materials The materials to count. If empty, counts all blocks.
     * @return The count of matching blocks.
     */
    public static int countBlocksInRadius(final Location location, final int radius, final int step, final RegionShape shape, final Material... materials) {
        final World world = location.getWorld();
        if (world == null || step <= 0) return 0;

        int count = 0;
        final int centerX = location.getBlockX();
        final int centerY = location.getBlockY();
        final int centerZ = location.getBlockZ();

        final int startX = centerX - radius;
        final int endX = centerX + radius;
        final int startY = centerY - radius;
        final int endY = centerY + radius;
        final int startZ = centerZ - radius;
        final int endZ = centerZ + radius;

        final int radiusSquared = radius * radius;
        final boolean filterByMaterial = materials != null && materials.length > 0;
        final Set<Material> materialSet = filterByMaterial ? new HashSet<>(Arrays.asList(materials)) : null;

        for (int x = startX; x < endX; x += step) {
            for (int y = startY; y < endY; y += step) {
                for (int z = startZ; z < endZ; z += step) {
                    if (shape == RegionShape.SPHERE) {
                        int dx = x - centerX;
                        int dy = y - centerY;
                        int dz = z - centerZ;
                        if (dx * dx + dy * dy + dz * dz > radiusSquared) {
                            continue;
                        }
                    }
                    Block block = world.getBlockAt(x, y, z);
                    if (!filterByMaterial || materialSet.contains(block.getType())) {
                        count++;
                    }
                }
            }
        }
        return count;
    }

    /**
     * Finds the closest block of the specified material within a region.
     *
     * @param location The center location.
     * @param radius   The radius (in blocks) from the center.
     * @param shape    The region shape (CUBE or SPHERE).
     * @param material The material to find.
     * @return The closest matching block, or null if none is found.
     */
    public static Block findClosestBlock(final Location location, final int radius, final RegionShape shape, final Material material) {
        final World world = location.getWorld();
        if (world == null) return null;

        Block closestBlock = null;
        double closestDistanceSquared = Double.MAX_VALUE;

        final int centerX = location.getBlockX();
        final int centerY = location.getBlockY();
        final int centerZ = location.getBlockZ();

        final int startX = centerX - radius;
        final int endX = centerX + radius;
        final int startY = centerY - radius;
        final int endY = centerY + radius;
        final int startZ = centerZ - radius;
        final int endZ = centerZ + radius;

        final int radiusSquared = radius * radius;
        for (int x = startX; x < endX; x++) {
            for (int y = startY; y < endY; y++) {
                for (int z = startZ; z < endZ; z++) {
                    if (shape == RegionShape.SPHERE) {
                        int dx = x - centerX;
                        int dy = y - centerY;
                        int dz = z - centerZ;
                        if (dx * dx + dy * dy + dz * dz > radiusSquared) {
                            continue;
                        }
                    }
                    Block block = world.getBlockAt(x, y, z);
                    if (block.getType() == material) {
                        double distanceSquared = location.distanceSquared(block.getLocation());
                        if (distanceSquared < closestDistanceSquared) {
                            closestDistanceSquared = distanceSquared;
                            closestBlock = block;
                        }
                    }
                }
            }
        }
        return closestBlock;
    }

    /**
     * Clears (sets to AIR) all blocks within a region centered at the given location.
     *
     * @param location  The center location.
     * @param radius    The radius (in blocks) from the center.
     * @param step      The interval between checked blocks.
     * @param shape     The region shape (CUBE or SPHERE).
     * @param materials Optional. The materials to clear. If empty, clears all blocks.
     * @return The number of blocks cleared.
     */
    public static int clearBlocksInRadius(final Location location, final int radius, final int step, final RegionShape shape, final Material... materials) {
        final World world = location.getWorld();
        if (world == null || step <= 0) return 0;

        int clearedCount = 0;
        final int centerX = location.getBlockX();
        final int centerY = location.getBlockY();
        final int centerZ = location.getBlockZ();

        final int startX = centerX - radius;
        final int endX = centerX + radius;
        final int startY = centerY - radius;
        final int endY = centerY + radius;
        final int startZ = centerZ - radius;
        final int endZ = centerZ + radius;

        final int radiusSquared = radius * radius;
        final boolean filterByMaterial = materials != null && materials.length > 0;
        final Set<Material> materialSet = filterByMaterial ? new HashSet<>(Arrays.asList(materials)) : null;

        for (int x = startX; x < endX; x += step) {
            for (int y = startY; y < endY; y += step) {
                for (int z = startZ; z < endZ; z += step) {
                    if (shape == RegionShape.SPHERE) {
                        int dx = x - centerX;
                        int dy = y - centerY;
                        int dz = z - centerZ;
                        if (dx * dx + dy * dy + dz * dz > radiusSquared) {
                            continue;
                        }
                    }
                    Block block = world.getBlockAt(x, y, z);
                    if (!filterByMaterial || materialSet.contains(block.getType())) {
                        block.setType(Material.AIR, false);
                        clearedCount++;
                    }
                }
            }
        }
        return clearedCount;
    }

    // ============================================================
    // Asynchronous Wrappers (Read-only operations only)
    // ============================================================

    /**
     * Asynchronously filters blocks within a region.
     * <p>
     * <strong>Warning:</strong> Although this method is asynchronous, it accesses the Bukkit API.
     * Ensure that chunks are loaded and test thoroughly on your Paper server.
     * </p>
     *
     * @param location  The center location.
     * @param radius    The radius from the center.
     * @param step      The interval between checked blocks.
     * @param shape     The region shape.
     * @param condition The predicate to test each block.
     * @return A CompletableFuture that will supply the list of matching blocks.
     */
    public static CompletableFuture<List<Block>> filterBlocksInRadiusAsync(final Location location, final int radius, final int step, final RegionShape shape, final Predicate<Block> condition) {
        return CompletableFuture.supplyAsync(() -> filterBlocksInRadius(location, radius, step, shape, condition));
    }

    /**
     * Asynchronously returns all blocks within a region.
     *
     * @param location  The center location.
     * @param radius    The radius from the center.
     * @param step      The interval between checked blocks.
     * @param shape     The region shape.
     * @param materials The materials to include (if any).
     * @return A CompletableFuture that will supply the list of matching blocks.
     */
    public static CompletableFuture<List<Block>> returnAllBlocksInRadiusAsync(final Location location, final int radius, final int step, final RegionShape shape, final Material... materials) {
        return CompletableFuture.supplyAsync(() -> returnAllBlocksInRadius(location, radius, step, shape, materials));
    }

    /**
     * Asynchronously counts blocks with specified materials within a region.
     *
     * @param location  The center location.
     * @param radius    The radius from the center.
     * @param step      The interval between checked blocks.
     * @param shape     The region shape.
     * @param materials The materials to count (if any).
     * @return A CompletableFuture that will supply the count.
     */
    public static CompletableFuture<Integer> countBlocksInRadiusAsync(final Location location, final int radius, final int step, final RegionShape shape, final Material... materials) {
        return CompletableFuture.supplyAsync(() -> countBlocksInRadius(location, radius, step, shape, materials));
    }

    /**
     * Asynchronously finds the closest block of the specified material within a region.
     *
     * @param location The center location.
     * @param radius   The radius from the center.
     * @param shape    The region shape.
     * @param material The material to search for.
     * @return A CompletableFuture that will supply the closest block, or null if not found.
     */
    public static CompletableFuture<Block> findClosestBlockAsync(final Location location, final int radius, final RegionShape shape, final Material material) {
        return CompletableFuture.supplyAsync(() -> findClosestBlock(location, radius, shape, material));
    }
}
