package com.dreamfirestudios.dreamCore.DreamfireRaycast;

import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockIterator;

import java.util.Set;

//TODO add entiy support
public class DreamfireRaycast {

    /**
     * Performs a raycast from the player's position, ignoring specific materials along the path.
     *
     * @param player    The player initiating the raycast.
     * @param range     The maximum range of the raycast.
     * @param particle  Particle to display along the raycast path (can be null for no particles).
     * @param materials A set of materials to ignore during the raycast.
     * @return The first block that is not in the ignored materials, or null if none is found within range.
     */
    public static Block rayCastFromPlayerIgnore(Player player, int range, Particle particle, Set<Material> materials) {
        return performRaycast(player, range, particle, materials, false);
    }

    /**
     * Performs a raycast from the player's position, stopping at the first block of specific materials.
     *
     * @param player    The player initiating the raycast.
     * @param range     The maximum range of the raycast.
     * @param particle  Particle to display along the raycast path (can be null for no particles).
     * @param materials A set of materials to detect during the raycast.
     * @return The first block that matches the materials, or null if none is found within range.
     */
    public static Block rayCastFromPlayerMust(Player player, int range, Particle particle, Set<Material> materials) {
        return performRaycast(player, range, particle, materials, true);
    }

    /**
     * A generalized raycast helper function to reduce redundant logic.
     *
     * @param player    The player performing the raycast.
     * @param range     The maximum range of the raycast.
     * @param particle  Particle to display along the path (can be null).
     * @param materials A set of materials to detect or ignore during the raycast.
     * @param mustMatch If true, stop at the first block that matches the materials; otherwise, skip them.
     * @return The target block as per the criteria, or null if no target is found.
     */
    private static Block performRaycast(Player player, int range, Particle particle, Set<Material> materials, boolean mustMatch) {
        if (player == null || player.getWorld() == null) {
            throw new IllegalArgumentException("Player or player world cannot be null.");
        }
        if (range <= 0) {
            throw new IllegalArgumentException("Range must be greater than zero.");
        }

        var playerDirection = player.getLocation().getDirection();
        var blockIterator = new BlockIterator(player.getWorld(), player.getLocation().toVector(), playerDirection, 0, range);
        Block targetBlock = null;

        while (blockIterator.hasNext()) {
            Block currentBlock = blockIterator.next();

            // Spawn particles along the ray path.
            if (particle != null) {
                player.getWorld().spawnParticle(particle, currentBlock.getLocation().add(0.5, 0.5, 0.5), 1);
            }

            // Determine whether to skip or stop based on the mustMatch flag.
            boolean matchesCriteria = materials.contains(currentBlock.getType());
            if (mustMatch == matchesCriteria) {
                targetBlock = currentBlock;
                break;
            }
        }
        return targetBlock;
    }
}