package com.dreamfirestudios.dreamcore.DreamBlockMask;

import com.dreamfirestudios.dreamcore.DreamCore;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/// <summary>
/// Renders a per-player “block mask” viewport by sending ephemeral block changes around the player.
/// </summary>
/// <remarks>
/// Use <see cref="DreamBlockMask.Builder"/> to construct an instance. Call <see cref="#play()"/>,
/// <see cref="#pause()"/>, <see cref="#displayNextFrame()"/>, and <see cref="#stop()"/> to control the lifecycle.
/// This class relies on your scheduler/tick loop; it does not schedule itself.
/// </remarks>
/// <example>
/// ```java
/// DreamBlockMask mask = new DreamBlockMask.Builder()
///     .ignoreAir(true)
///     .minDistance(2.0)
///     .maxX(8).maxY(6).maxZ(8)
///     .blockExceptions(Material.STONE, Material.BARRIER)
///     .CreateMask(player);
///
/// mask.play();
/// // in your tick loop:
/// mask.displayNextFrame();
/// ```
/// </example>
public class DreamBlockMask {

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

    /// <summary>
    /// Indicates whether the mask is currently paused (true) or playing (false).
    /// </summary>
    @Getter
    private volatile boolean actionBarPaused = true;

    private DreamBlockMask() { }

    // ----------------------------- Mutation helpers -----------------------------

    /// <summary>
    /// Adds/merges block type exceptions into this mask.
    /// </summary>
    /// <param name="blockExceptions">Map of actual block type → “view” block type.</param>
    /// <exception cref="IllegalArgumentException">If <paramref name="blockExceptions"/> is null.</exception>
    public void addToExceptions(Map<Material, Material> blockExceptions){
        if (blockExceptions == null) throw new IllegalArgumentException("Block exceptions cannot be null.");
        this.blockExceptions = mergeExceptions(this.blockExceptions, blockExceptions);
    }

    // ----------------------------- Frame cycle -----------------------------

    /// <summary>
    /// Computes and applies the next frame. Call this periodically (e.g., once per tick)
    /// while <see cref="#isActionBarPaused()"/> is <c>false</c>.
    /// </summary>
    /// <remarks>
    /// Fires <see cref="BlockMaskFrameComputedEvent"/> before sending,
    /// and <see cref="BlockMaskFrameAppliedEvent"/> after sending.
    /// </remarks>
    /// <returns><c>true</c> when no work was performed (no player or paused); otherwise <c>false</c>.</returns>
    public boolean displayNextFrame(){
        if (player == null || !player.isOnline() || actionBarPaused) return true;

        final Location playerLoc = player.getLocation();
        final double px = playerLoc.getX();
        final double py = playerLoc.getY();
        final double pz = playerLoc.getZ();

        final Map<Vector, BlockState> previousFrameStates = new HashMap<>();
        final Map<Vector, BlockState> newFrameStates = new HashMap<>();

        // NOTE: Iterate using doubles to preserve exact behavior of your original logic.
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

    /// <summary>
    /// Pauses the mask and restores any pending last/trail states to the player.
    /// </summary>
    public void pause() {
        if (!actionBarPaused) {
            actionBarPaused = true;
            // restore what the player currently sees for last frame and persistent trail
            player.sendBlockChanges(lastFrameBlockStates.values());
            player.sendBlockChanges(visitedTrailLocations.values());
            new BlockMaskPausedEvent(player, this);
        }
    }

    /// <summary>
    /// Resumes the mask if paused.
    /// </summary>
    public void play() {
        if (actionBarPaused) {
            actionBarPaused = false;
            new BlockMaskStartedEvent(player, this);
        }
    }

    /// <summary>
    /// Stops the mask, restores visible changes, fires stopped event, and removes it from the core registry.
    /// </summary>
    /// <returns>
    /// The removed mask instance from <see cref="DreamCore"/> (previous value under the player's UUID).
    /// </returns>
    /// <remarks>
    /// Note: this returns the previous mapping if any, because it delegates to <c>Map.remove</c> in the registry.
    /// </remarks>
    public DreamBlockMask stop(){
        actionBarPaused = true;
        player.sendBlockChanges(lastFrameBlockStates.values());
        player.sendBlockChanges(visitedTrailLocations.values());
        new BlockMaskStoppedEvent(player, this);
        return DreamCore.DreamBlockMasks.remove(player.getUniqueId());
    }

    // ----------------------------- Utilities -----------------------------

    /// <summary>
    /// Returns an immutable view of the persistent trail entries.
    /// </summary>
    public Map<Vector, BlockState> getVisitedTrailLocationsView() {
        return Collections.unmodifiableMap(visitedTrailLocations);
    }

    private static Map<Material, Material> mergeExceptions(Map<Material, Material> base, Map<Material, Material> add) {
        Map<Material, Material> out = new HashMap<>(base);
        out.putAll(add);
        return Collections.unmodifiableMap(out);
    }

    // ----------------------------- Builder -----------------------------

    /// <summary>
    /// Fluent builder for <see cref="DreamBlockMask"/>.
    /// </summary>
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

        /// <summary>
        /// Bulk add block exceptions (actual → view).
        /// </summary>
        /// <param name="blockExceptions">Mapping of original types to viewed types.</param>
        /// <returns>This builder.</returns>
        public Builder blockExceptions(Map<Material, Material> blockExceptions){
            if (blockExceptions == null) throw new IllegalArgumentException("Block exceptions cannot be null.");
            this.blockExceptions.putAll(blockExceptions);
            return this;
        }

        /// <summary>
        /// Add a single block exception mapping.
        /// </summary>
        /// <param name="target">Original/real block type.</param>
        /// <param name="view">Viewed/masked block type.</param>
        /// <returns>This builder.</returns>
        public Builder blockExceptions(Material target, Material view){
            if (target == null || view == null) throw new IllegalArgumentException("Block type cannot be null.");
            this.blockExceptions.put(target, view);
            return this;
        }

        /// <summary>
        /// Delete mask automatically when the player reference becomes invalid.
        /// </summary>
        /// <param name="deleteMaskOnNull">Whether to auto-delete on null/invalid player.</param>
        /// <returns>This builder.</returns>
        public Builder deleteMaskOnNull(boolean deleteMaskOnNull){
            this.deleteMaskOnNull = deleteMaskOnNull;
            return this;
        }

        /// <summary>
        /// Ignore AIR blocks when producing the mask.
        /// </summary>
        /// <param name="ignoreAir">If true, air blocks are skipped.</param>
        /// <returns>This builder.</returns>
        public Builder ignoreAir(boolean ignoreAir){
            this.ignoreAir = ignoreAir;
            return this;
        }

        /// <summary>
        /// Restore blocks from the previous frame if they are not in the new frame (unless retained by trail).
        /// </summary>
        /// <param name="resetLastFrames">Whether to back-fill from the last frame.</param>
        /// <returns>This builder.</returns>
        public Builder resetLastFrames(boolean resetLastFrames){
            this.resetLastFrames = resetLastFrames;
            return this;
        }

        /// <summary>
        /// Keep a persistent trail of masked blocks across frames.
        /// </summary>
        /// <param name="keepTrailTheSame">If true, visited blocks remain masked persistently.</param>
        /// <returns>This builder.</returns>
        public Builder keepTrailTheSame(boolean keepTrailTheSame){
            this.keepTrailTheSame = keepTrailTheSame;
            return this;
        }

        /// <summary>
        /// Minimum distance from the player under which blocks are not affected.
        /// </summary>
        /// <param name="minDistance">Non-negative minimum distance.</param>
        /// <returns>This builder.</returns>
        public Builder minDistance(double minDistance){
            if (minDistance < 0) throw new IllegalArgumentException("minDistance cannot be negative.");
            this.minDistance = minDistance;
            return this;
        }

        /// <summary>Set X axis bound around the player for the mask region.</summary>
        /// <param name="maxX">Non-negative maximum offset on X.</param>
        /// <returns>This builder.</returns>
        public Builder maxX(double maxX){
            if (maxX < 0) throw new IllegalArgumentException("Max X cannot be negative.");
            this.maxX = maxX;
            return this;
        }

        /// <summary>Set Y axis bound around the player for the mask region.</summary>
        /// <param name="maxY">Non-negative maximum offset on Y.</param>
        /// <returns>This builder.</returns>
        public Builder maxY(double maxY){
            if (maxY < 0) throw new IllegalArgumentException("Max Y cannot be negative.");
            this.maxY = maxY;
            return this;
        }

        /// <summary>Set Z axis bound around the player for the mask region.</summary>
        /// <param name="maxZ">Non-negative maximum offset on Z.</param>
        /// <returns>This builder.</returns>
        public Builder maxZ(double maxZ){
            if (maxZ < 0) throw new IllegalArgumentException("Max Z cannot be negative.");
            this.maxZ = maxZ;
            return this;
        }

        /// <summary>
        /// Creates (or merges with) the player's mask. If a mask already exists for the player,
        /// it will be returned with new exceptions merged.
        /// </summary>
        /// <param name="player">Target player.</param>
        /// <returns>
        /// The created/existing mask stored in <see cref="DreamCore"/>.
        /// </returns>
        /// <exception cref="IllegalArgumentException">If <paramref name="player"/> is null.</exception>
        public DreamBlockMask CreateMask(Player player){
            if (player == null) throw new IllegalArgumentException("Player cannot be null.");
            var stored = DreamCore.DreamBlockMasks.getOrDefault(player.getUniqueId(), null);
            if (stored != null){
                stored.addToExceptions(this.blockExceptions);
                return stored;
            }

            DreamBlockMask mask = new DreamBlockMask();
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
            // NOTE: As written, this returns the previous value (if any) — not the newly created mask.
            // Left unchanged to avoid behavior changes without your approval.
            return DreamCore.DreamBlockMasks.put(player.getUniqueId(), mask);
        }
    }
}