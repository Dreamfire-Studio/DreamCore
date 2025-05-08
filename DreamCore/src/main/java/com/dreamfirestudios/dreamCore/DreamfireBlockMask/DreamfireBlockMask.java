package com.dreamfirestudios.dreamCore.DreamfireBlockMask;

import com.dreamfirestudios.dreamCore.DreamfireBlockMask.Events.BlockMaskCreatedEvent;
import com.dreamfirestudios.dreamCore.DreamfireBlockMask.Events.BlockMaskPausedEvent;
import com.dreamfirestudios.dreamCore.DreamfireBlockMask.Events.BlockMaskStartedEvent;
import com.dreamfirestudios.dreamCore.DreamfireBlockMask.Events.BlockMaskStoppedEvent;
import com.dreamfirestudios.dreamCore.DreamCore;
import com.dreamfirestudios.dreamCore.DreamfirePlayer.DreamfirePlayerAction;
import com.dreamfirestudios.dreamCore.DreamfirePlayer.DreamfirePlayerActionAPI;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.HashMap;

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
    @Getter private HashMap<Material, Material> blockExceptions;
    private HashMap<org.bukkit.util.Vector, BlockState> lastFrameBlockStates = new HashMap<>();
    private final HashMap<org.bukkit.util.Vector, BlockState> visitedTrailLocations = new HashMap<>();
    @Getter
    private boolean actionBarPaused = true;

    /**
     * Add block type exceptions to the mask.
     *
     * @param blockExceptions The map of block type exceptions.
     */
    public void addToExceptions(HashMap<Material, Material> blockExceptions){
        if(blockExceptions == null) throw new IllegalArgumentException("Block exceptions cannot be null.");
        for(var key : blockExceptions.keySet()) this.blockExceptions.put(key, blockExceptions.get(key));
    }

    public void displayNextFrame(){
        if(player == null || !player.isOnline() || actionBarPaused) return;

        var previousFrameStates = new HashMap<org.bukkit.util.Vector, BlockState>();
        var newFrameBlockStates = new HashMap<Vector, BlockState>();

        for(var x = player.getLocation().getX() - maxX; x < player.getLocation().getX() + maxX; x++){
            for(var y = player.getLocation().getY() - maxY; y < player.getLocation().getY() + maxY; y++){
                for(var z = player.getLocation().getZ() - maxZ; z < player.getLocation().getZ() + maxZ; z++){

                    var affectedBlock = new Location(player.getWorld(), x, y, z).getBlock();
                    var affectedBlockVector = affectedBlock.getLocation().toVector();
                    if(affectedBlock.getLocation().distance(player.getLocation()) < minDistance) continue;

                    if(affectedBlock.getType() == Material.AIR && ignoreAir) continue;
                    var newBlockType = blockExceptions.getOrDefault(affectedBlock.getType(), Material.BARRIER);
                    if(newBlockType == null) continue;

                    previousFrameStates.put(affectedBlockVector, affectedBlock.getState());

                    var newBlockState = affectedBlock.getState();
                    newBlockState.setType(newBlockType);
                    newFrameBlockStates.put(affectedBlockVector, newBlockState);
                }
            }
        }

        if(resetLastFrames){
            for(var vector : lastFrameBlockStates.keySet()){
                if(!newFrameBlockStates.containsKey(vector) && !visitedTrailLocations.containsKey(vector)) newFrameBlockStates.put(vector, lastFrameBlockStates.get(vector));
            }
        }

        player.sendBlockChanges(newFrameBlockStates.values());
        lastFrameBlockStates = previousFrameStates;
    }

    /**
     * Pauses the block mask effect, returning the blocks to their previous states.
     */
    public void pause() {
        if(!actionBarPaused){
            actionBarPaused = true;
            player.sendBlockChanges(lastFrameBlockStates.values());
            player.sendBlockChanges(visitedTrailLocations.values());
            new BlockMaskPausedEvent(player, this);
        }
    }

    /**
     * Resumes the block mask effect from the paused state.
     */
    public void play() {
        if(actionBarPaused){
            actionBarPaused = false;
            new BlockMaskStartedEvent(player, this);
        }
    }

    /**
     * Stops the block mask effect and cleans up associated resources.
     *
     * @return The current DreamfireBlockMask instance for further chaining.
     */
    public DreamfireBlockMask stop(){
        actionBarPaused = true;
        player.sendBlockChanges(lastFrameBlockStates.values());
        player.sendBlockChanges(visitedTrailLocations.values());
        new BlockMaskStoppedEvent(player, this);
        return DreamCore.GetDreamfireCore().DeleteBlockMask(player.getUniqueId());
    }

    public static class Builder {
        private final HashMap<Material, Material> blockExceptions = new HashMap<>();
        private boolean deleteMaskOnNull = false;
        private boolean ignoreAir = true;
        private boolean resetLastFrames = true;
        private boolean keepTrailTheSame = false;
        private double minDistance = 0;
        private double maxX = 5;
        private double maxY = 5;
        private double maxZ = 5;

        /**
         * Adds a map of block exceptions where certain block types are replaced by other block types.
         *
         * @param blockExceptions A map where keys are block types to be replaced, and values are the block types to replace them with.
         * @return The builder instance for chaining.
         * @throws IllegalArgumentException if the blockExceptions map is null.
         */
        public Builder blockExceptions(HashMap<Material, Material> blockExceptions){
            if(blockExceptions == null) throw new IllegalArgumentException("Block exceptions cannot be null.");
            for(var key : blockExceptions.keySet()) this.blockExceptions.put(key, blockExceptions.get(key));
            return this;
        }

        /**
         * Adds a single block exception, where a specific block type will be replaced by another block type.
         *
         * @param target The block type to be replaced.
         * @param view The block type to replace it with.
         * @return The builder instance for chaining.
         * @throws IllegalArgumentException if either target or view is null.
         */
        public Builder blockExceptions(Material target, Material view){
            if(target == null || view == null) throw new IllegalArgumentException("Block type cannot be null.");
            blockExceptions.put(target, view);
            return this;
        }

        /**
         * Sets whether the block mask should be deleted when the player associated with it is no longer valid.
         *
         * @param deleteMaskOnNull If true, the block mask is deleted when the player is no longer valid.
         * @return The builder instance for chaining.
         */
        public Builder deleteMaskOnNull(boolean deleteMaskOnNull){
            this.deleteMaskOnNull = deleteMaskOnNull;
            return this;
        }

        /**
         * Sets whether air blocks should be ignored by the mask. If true, air blocks are not replaced.
         *
         * @param ignoreAir If true, air blocks are ignored and not replaced.
         * @return The builder instance for chaining.
         */
        public Builder ignoreAir(boolean ignoreAir){
            this.ignoreAir = ignoreAir;
            return this;
        }

        /**
         * Sets whether the last frame's block states should be reset when creating a new frame.
         * This is useful for ensuring blocks from previous frames are restored if not part of the current mask.
         *
         * @param resetLastFrames If true, the last frames' blocks are reset.
         * @return The builder instance for chaining.
         */
        public Builder resetLastFrames(boolean resetLastFrames){
            this.resetLastFrames = resetLastFrames;
            return this;
        }

        /**
         * Sets whether the trail of block replacements should remain the same between frames.
         * This is useful for keeping a consistent effect over time.
         *
         * @param keepTrailTheSame If true, the trail of replaced blocks stays the same.
         * @return The builder instance for chaining.
         */
        public Builder keepTrailTheSame(boolean keepTrailTheSame){
            this.keepTrailTheSame = keepTrailTheSame;
            return this;
        }

        /**
         * Sets the minimum distance for blocks to be affected by the mask. Blocks closer than this distance to the player are not affected.
         *
         * @param minDistance The minimum distance to the player for a block to be affected.
         * @return The builder instance for chaining.
         * @throws IllegalArgumentException if the minDistance is negative.
         */
        public Builder minDistance(double minDistance){
            this.minDistance = minDistance;
            return this;
        }

        /**
         * Sets the maximum distance along the X axis for blocks to be affected.
         * This controls the range of block replacements in the X direction.
         *
         * @param maxX The maximum distance on the X axis.
         * @return The builder instance for chaining.
         * @throws IllegalArgumentException if the maxX is negative.
         */

        public Builder maxX(double maxX){
            if(maxX < 0) throw new IllegalArgumentException("Max X cannot be negative.");
            this.maxX = maxX;
            return this;
        }

        /**
         * Sets the maximum distance along the Y axis for blocks to be affected.
         * This controls the range of block replacements in the Y direction.
         *
         * @param maxY The maximum distance on the Y axis.
         * @return The builder instance for chaining.
         * @throws IllegalArgumentException if the maxY is negative.
         */
        public Builder maxY(double maxY){
            if(maxY < 0) throw new IllegalArgumentException("Max Y cannot be negative.");
            this.maxY = maxY;
            return this;
        }

        /**
         * Sets the maximum distance along the Z axis for blocks to be affected.
         * This controls the range of block replacements in the Z direction.
         *
         * @param maxZ The maximum distance on the Z axis.
         * @return The builder instance for chaining.
         * @throws IllegalArgumentException if the maxZ is negative.
         */
        public Builder maxZ(double maxZ){
            if(maxZ < 0) throw new IllegalArgumentException("Max Z cannot be negative.");
            this.maxZ = maxZ;
            return this;
        }

        /**
         * Creates a new DreamfireBlockMask instance based on the current builder settings.
         * If a block mask already exists for the player, it will be returned with the new exceptions added.
         *
         * @param player The player to associate with the block mask.
         * @return A new DreamfireBlockMask instance.
         * @throws IllegalArgumentException if the player is null.
         */
        public DreamfireBlockMask CreateMask(Player player){
            if(player == null) throw new IllegalArgumentException("Player cannot be null.");
            if(!DreamfirePlayerActionAPI.CanPlayerAction(DreamfirePlayerAction.PlayerBlockMask, player.getUniqueId())) return null;

            var storedBlockMask = DreamCore.GetDreamfireCore().GetBlockMask(player.getUniqueId());
            if(storedBlockMask != null){
                storedBlockMask.addToExceptions(blockExceptions);
                return storedBlockMask;
            }

            var createdBlockMask = new DreamfireBlockMask();
            createdBlockMask.player = player;
            createdBlockMask.deleteMaskOnNull = deleteMaskOnNull;
            createdBlockMask.resetLastFrames = resetLastFrames;
            createdBlockMask.keepTrailTheSame = keepTrailTheSame;
            createdBlockMask.minDistance = minDistance;
            createdBlockMask.maxX = maxX;
            createdBlockMask.maxY = maxY;
            createdBlockMask.maxZ = maxZ;
            createdBlockMask.ignoreAir = ignoreAir;
            createdBlockMask.blockExceptions = blockExceptions;

            new BlockMaskCreatedEvent(createdBlockMask, player);
            return DreamCore.GetDreamfireCore().AddBlockMask(player.getUniqueId(), createdBlockMask);
        }
    }
}
