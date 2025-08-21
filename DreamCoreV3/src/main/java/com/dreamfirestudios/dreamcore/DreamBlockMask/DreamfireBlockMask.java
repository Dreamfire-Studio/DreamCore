package com.dreamfirestudios.dreamcore.DreamBlockMask;

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * <summary>
 * Renders a per-player “block mask” viewport by sending ephemeral block changes around the player.
 * </summary>
 * <remarks>
 * Use {@link Builder} to construct an instance. Call {@link #play()}, {@link #pause()}, {@link #displayNextFrame()},
 * and {@link #stop()} to control the lifecycle. This class uses per-tick calls to update frames; it does not schedule itself.
 * </remarks>
 */
public class DreamfireBlockMask {

    private Player player;

    @Getter private boolean deleteMaskOnNull;
    @Getter private boolean ignoreAir;
    @Getter private boolean resetLastFrames;
    @Getter private boolean keepTrailTheSame;
    @Getter private double minDistance;
    @Getter private double maxX;
    @Getter private double maxY;
    @Getter private double maxZ;

    @Getter private Map<Material, Material> blockExceptions;

    private Map<Vector, BlockState> lastFrameBlockStates = new HashMap<>();
    private final Map<Vector, BlockState> visitedTrailLocations = new HashMap<>();

    @Getter
    private volatile boolean actionBarPaused = true;

    private DreamfireBlockMask() {}

    // ----------------------------- Mutation helpers -----------------------------

    /**
     * <summary>Adds/merges block type exceptions into this mask.</summary>
     * <param name="blockExceptions">Map of actual block type -> “view” block type.</param>
     * <exception cref="IllegalArgumentException">If <paramref name="blockExceptions"/> is null.</exception>
     */
    public void addToExceptions(Map<Material, Material> blockExceptions){
        if (blockExceptions == null) throw new IllegalArgumentException("Block exceptions cannot be null.");
        this.blockExceptions = mergeExceptions(this.blockExceptions, blockExceptions);
    }

    // ----------------------------- Frame cycle -----------------------------

    /**
     * <summary>
     * Computes and applies the next frame. Call this periodically (e.g., once per tick) while {@link #isActionBarPaused()} is false.
     * </summary>
     * <remarks>
     * Fires {@link BlockMaskFrameComputedEvent} before sending, and {@link BlockMaskFrameAppliedEvent} after sending.
     * </remarks>
     * <returns><c>true</c> when no work was performed (no player/frames paused); otherwise <c>false</c>.</returns>
     */
    public boolean displayNextFrame(){
        if (player == null || !player.isOnline() || actionBarPaused) return true;

        final Location playerLoc = player.getLocation();
        final double px = playerLoc.getX();
        final double py = playerLoc.getY();
        final double pz = playerLoc.getZ();

        final Map<Vector, BlockState> previousFrameStates = new HashMap<>();
        final Map<Vector, BlockState> newFrameStates = new HashMap<>();

        // NOTE: We intentionally iterate using doubles (matching your original logic) to avoid behavioral change.
        for (double x = px - maxX; x < px + maxX; x++){
            for (double y = py - maxY; y < py + maxY; y++){
                for (double z = pz - maxZ; z < pz + maxZ; z++){

                    final Location loc = new Location(player.getWorld(), x, y, z);
                    if (loc.distance(playerLoc) < minDistance) continue;

                    final var block = loc.getBlock();
                    if (block == null) continue; // defensive
                    if (block.getType() == Material.AIR && ignoreAir) continue;

                    final Material viewMat = blockExceptions.getOrDefault(block.getType(), Material.BARRIER);
                    if (viewMat == null) continue;

                    final Vector key = block.getLocation().toVector();
                    previousFrameStates.put(key, block.getState());

                    final BlockState newState = block.getState();
                    newState.setType(viewMat);
                    newFrameStates.put(key, newState);

                    if (keepTrailTheSame && !visitedTrailLocations.containsKey(key)) {
                        visitedTrailLocations.put(key, newState);
                    }
                }
            }
        }

        // Restore areas from last frame that are no longer present (unless retained in the trail map).
        if (resetLastFrames) {
            for (Map.Entry<Vector, BlockState> e : lastFrameBlockStates.entrySet()) {
                if (!newFrameStates.containsKey(e.getKey()) && !visitedTrailLocations.containsKey(e.getKey())) {
                    newFrameStates.put(e.getKey(), e.getValue());
                }
            }
        }

        // Pre-apply event hook
        new BlockMaskFrameComputedEvent(player, this,
                Collections.unmodifiableMap(newFrameStates),
                Collections.unmodifiableMap(previousFrameStates));

        // Apply and swap
        player.sendBlockChanges(newFrameStates.values());
        lastFrameBlockStates = previousFrameStates;

        // Post-apply event hook
        new BlockMaskFrameAppliedEvent(player, this,
                Collections.unmodifiableMap(newFrameStates));

        return false;
    }

    /**
     * <summary>Pauses the mask and restores any pending last/trail states to the player.</summary>
     */
    public void pause() {
        if (!actionBarPaused) {
            actionBarPaused = true;
            // restore what the player currently sees for last frame and persistent trail
            player.sendBlockChanges(lastFrameBlockStates.values());
            player.sendBlockChanges(visitedTrailLocations.values());
            new BlockMaskPausedEvent(player, this);
        }
    }

    /**
     * <summary>Resumes the mask if paused.</summary>
     */
    public void play() {
        if (actionBarPaused) {
            actionBarPaused = false;
            new BlockMaskStartedEvent(player, this);
        }
    }

    /**
     * <summary>
     * Stops the mask, restores visible changes, fires stopped event, and removes it from the core registry.
     * </summary>
     * <returns>The removed mask instance from DreamCore (as confirmation).</returns>
     */
    public DreamfireBlockMask stop(){
        actionBarPaused = true;
        player.sendBlockChanges(lastFrameBlockStates.values());
        player.sendBlockChanges(visitedTrailLocations.values());
        new BlockMaskStoppedEvent(player, this);
        return DreamCore.GetDreamfireCore().DeleteBlockMask(player.getUniqueId());
    }

    // ----------------------------- Utilities -----------------------------

    /**
     * <summary>Returns an immutable view of the persistent trail entries.</summary>
     */
    public Map<Vector, BlockState> getVisitedTrailLocationsView() {
        return Collections.unmodifiableMap(visitedTrailLocations);
    }

    private static Map<Material, Material> mergeExceptions(Map<Material, Material> base, Map<Material, Material> add) {
        Map<Material, Material> out = new HashMap<>(base);
        out.putAll(add);
        return Collections.unmodifiableMap(out);
    }

    // ----------------------------- Builder -----------------------------

    /**
     * <summary>Fluent builder for {@link DreamfireBlockMask}.</summary>
     */
    public static class Builder {

        private final Map<Material, Material> blockExceptions = new HashMap<>();
        private boolean deleteMaskOnNull = false;
        private boolean ignoreAir = true;
        private boolean resetLastFrames = true;
        private boolean keepTrailTheSame = false;
        private double minDistance = 0.0d;
        private double maxX = 5.0d;
        private double maxY = 5.0d;
        private double maxZ = 5.0d;

        /**
         * <summary>Bulk add block exceptions (actual -> view).</summary>
         */
        public Builder blockExceptions(Map<Material, Material> blockExceptions){
            if (blockExceptions == null) throw new IllegalArgumentException("Block exceptions cannot be null.");
            this.blockExceptions.putAll(blockExceptions);
            return this;
        }

        /**
         * <summary>Add a single block exception mapping.</summary>
         */
        public Builder blockExceptions(Material target, Material view){
            if (target == null || view == null) throw new IllegalArgumentException("Block type cannot be null.");
            this.blockExceptions.put(target, view);
            return this;
        }

        /**
         * <summary>Delete mask automatically when the player reference becomes invalid.</summary>
         */
        public Builder deleteMaskOnNull(boolean deleteMaskOnNull){
            this.deleteMaskOnNull = deleteMaskOnNull;
            return this;
        }

        /**
         * <summary>Ignore AIR blocks when producing the mask.</summary>
         */
        public Builder ignoreAir(boolean ignoreAir){
            this.ignoreAir = ignoreAir;
            return this;
        }

        /**
         * <summary>Restore blocks from the previous frame if they are not in the new frame (unless retained by trail).</summary>
         */
        public Builder resetLastFrames(boolean resetLastFrames){
            this.resetLastFrames = resetLastFrames;
            return this;
        }

        /**
         * <summary>Keep a persistent trail of masked blocks across frames.</summary>
         */
        public Builder keepTrailTheSame(boolean keepTrailTheSame){
            this.keepTrailTheSame = keepTrailTheSame;
            return this;
        }

        /**
         * <summary>Minimum distance from the player under which blocks are not affected.</summary>
         */
        public Builder minDistance(double minDistance){
            if (minDistance < 0) throw new IllegalArgumentException("minDistance cannot be negative.");
            this.minDistance = minDistance;
            return this;
        }

        /**
         * <summary>Axis bounds around the player (X/Y/Z) for mask region.</summary>
         */
        public Builder maxX(double maxX){
            if (maxX < 0) throw new IllegalArgumentException("Max X cannot be negative.");
            this.maxX = maxX;
            return this;
        }
        public Builder maxY(double maxY){
            if (maxY < 0) throw new IllegalArgumentException("Max Y cannot be negative.");
            this.maxY = maxY;
            return this;
        }
        public Builder maxZ(double maxZ){
            if (maxZ < 0) throw new IllegalArgumentException("Max Z cannot be negative.");
            this.maxZ = maxZ;
            return this;
        }

        /**
         * <summary>
         * Creates (or merges with) the player's mask. If a mask already exists for the player,
         * it will be returned with new exceptions merged.
         * </summary>
         * <param name="player">Target player.</param>
         * <returns>The created/existing mask stored in {@link DreamCore}.</returns>
         */
        public DreamfireBlockMask CreateMask(Player player){
            if (player == null) throw new IllegalArgumentException("Player cannot be null.");
            var stored = DreamCore.GetDreamfireCore().GetBlockMask(player.getUniqueId());
            if (stored != null){
                stored.addToExceptions(this.blockExceptions);
                return stored;
            }

            DreamfireBlockMask mask = new DreamfireBlockMask();
            mask.player = player;
            mask.deleteMaskOnNull = deleteMaskOnNull;
            mask.resetLastFrames = resetLastFrames;
            mask.keepTrailTheSame = keepTrailTheSame;
            mask.minDistance = minDistance;
            mask.maxX = maxX;
            mask.maxY = maxY;
            mask.maxZ = maxZ;
            mask.ignoreAir = ignoreAir;
            mask.blockExceptions = Collections.unmodifiableMap(new HashMap<>(this.blockExceptions));

            new BlockMaskCreatedEvent(mask, player);
            return DreamCore.GetDreamfireCore().AddBlockMask(player.getUniqueId(), mask);
        }
    }
}