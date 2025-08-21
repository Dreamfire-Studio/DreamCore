package com.dreamfirestudios.dreamcore.DreamActionBar;

import com.dreamfirestudios.dreamcore.DreamJava.DreamClassID;
import com.dreamfirestudios.dreamcore.DreamCore;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/// <summary>
/// Represents an action bar instance that cycles through frames and displays them to viewers.
/// </summary>
public class DreamActionBar extends DreamClassID {
    private final List<DreamActionBarData> barFrames = new ArrayList<>();
    private final List<Player> viewers = new ArrayList<>();
    private int currentFrameIndex;
    private boolean paused;

    /// <summary>
    /// Checks if a player is currently viewing this action bar.
    /// </summary>
    /// <param name="player">Player to check.</param>
    /// <returns>True if the player is viewing, otherwise false.</returns>
    public boolean isPlayerViewing(Player player) {
        if (player == null) throw new IllegalArgumentException("Player cannot be null");
        return viewers.contains(player);
    }

    /// <summary>
    /// Adds a player as a viewer of this action bar.
    /// </summary>
    /// <param name="player">The player to add.</param>
    /// <param name="multipleActionBars">Whether multiple action bars per player are allowed.</param>
    public void addViewer(Player player, boolean multipleActionBars) {
        if (player == null) throw new IllegalArgumentException("Player cannot be null");
        if(DreamActionBarAPI.IsPlayerInActionBar(player) && !multipleActionBars) return;
        if(viewers.contains(player) || new DreamActionBarPlayerAdded(this, player).isCancelled()) return;
        viewers.add(player);
    }

    /// <summary>
    /// Removes a player from the viewer list.
    /// </summary>
    /// <param name="player">The player to remove.</param>
    public void removeViewer(Player player) {
        if (player == null) throw new IllegalArgumentException("Player cannot be null");
        if(!viewers.contains(player) || new DreamActionBarPlayerRemoved(this, player).isCancelled()) return;
        viewers.remove(player);
    }

    /// <summary>
    /// Clears all current viewers.
    /// </summary>
    public void clearViewers() {
        viewers.forEach(this::removeViewer);
        viewers.clear();
    }

    /// <summary>
    /// Updates a specific frame with new data.
    /// </summary>
    /// <param name="index">Frame index to update.</param>
    /// <param name="dreamActionBarData">The new frame data.</param>
    public void updateFrame(int index, DreamActionBarData dreamActionBarData) {
        if (dreamActionBarData == null) throw new IllegalArgumentException("DreamfireActionBarData cannot be null");
        if (index >= 0 && index < barFrames.size()) barFrames.set(index, dreamActionBarData);
    }

    /// <summary>
    /// Displays the next frame in the sequence to all viewers.
    /// </summary>
    /// <returns>True if finished, false otherwise.</returns>
    public boolean displayNextFrame() {
        if (barFrames.isEmpty() || viewers.isEmpty()) return true;
        if(paused) return false;
        DreamActionBarData dreamActionBarData = barFrames.get(currentFrameIndex);
        if (dreamActionBarData == null) throw new IllegalArgumentException("DreamfireActionBarData cannot be null");
        viewers.parallelStream().forEach(dreamActionBarData::displayActionBar);
        currentFrameIndex = (currentFrameIndex + 1) % barFrames.size();
        return false;
    }

    /// <summary>
    /// Pauses this action bar.
    /// </summary>
    public void pause() {
        if (!paused && !new DreamActionBarPaused(this).isCancelled()) {
            paused = true;
        }
    }

    /// <summary>
    /// Resumes this action bar if paused.
    /// </summary>
    public void play() {
        if (paused && !new DreamActionBarPlayed(this).isCancelled()) {
            paused = false;
        }
    }

    /// <summary>
    /// Stops this action bar, removing it from DreamCore and clearing viewers.
    /// </summary>
    public void stop() {
        if(!new DreamActionBarStopped(this).isCancelled()){
            DreamCore.DreamActionBars.remove(getClassID());
            clearViewers();
        }
    }

    /// <summary>
    /// Builder for creating DreamActionBar instances.
    /// </summary>
    public static class Builder {
        private final List<DreamActionBarData> barFrames = new ArrayList<>();
        private final List<Player> viewers = new ArrayList<>();

        /// <summary>
        /// Adds a frame to the builder with repetition.
        /// </summary>
        /// <param name="frame">Frame data.</param>
        /// <param name="repeatFrames">How many times the frame should repeat.</param>
        /// <returns>The builder instance.</returns>
        public Builder addFrame(DreamActionBarData frame, int repeatFrames) {
            if (frame == null || repeatFrames <= 0) return this;
            for (int i = 0; i < repeatFrames; i++) barFrames.add(frame);
            return this;
        }

        /// <summary>
        /// Adds a viewer to the builder.
        /// </summary>
        /// <param name="player">Player to add.</param>
        /// <returns>The builder instance.</returns>
        public Builder addViewer(Player player) {
            if (player == null) throw new IllegalArgumentException("Player cannot be null");
            viewers.add(player);
            return this;
        }

        /// <summary>
        /// Builds the DreamActionBar instance.
        /// </summary>
        /// <param name="allowMultipleActionBars">Whether multiple action bars are allowed.</param>
        /// <returns>The built DreamActionBar instance.</returns>
        public DreamActionBar build(boolean allowMultipleActionBars) {
            if (barFrames.isEmpty()) throw new IllegalArgumentException("At least one frame must be added.");
            DreamActionBar actionBar = new DreamActionBar();
            actionBar.barFrames.addAll(barFrames);
            actionBar.viewers.addAll(viewers);
            DreamCore.DreamActionBars.put(actionBar.getClassID(), actionBar);
            return actionBar;
        }
    }
}