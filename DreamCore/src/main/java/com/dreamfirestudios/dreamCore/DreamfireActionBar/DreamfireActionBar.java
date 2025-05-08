package com.dreamfirestudios.dreamCore.DreamfireActionBar;

import com.dreamfirestudios.dreamCore.DreamCore;
import com.dreamfirestudios.dreamCore.DreamfireEvents.DreamfireActionBar.*;
import com.dreamfirestudios.dreamCore.DreamfirePlayer.DreamfirePlayerAction;
import com.dreamfirestudios.dreamCore.DreamfirePlayer.DreamfirePlayerActionAPI;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class DreamfireActionBar {

    @Getter
    private UUID barID;
    private List<DreamfireActionBarData> barFrames = new CopyOnWriteArrayList<>();
    @Getter
    private Set<Player> viewers = ConcurrentHashMap.newKeySet();
    private int currentFrameIndex;
    @Getter
    private volatile boolean actionBarPaused = true;

    private DreamfireActionBar(){}

    /**
     * Checks if a player is currently viewing this action bar.
     *
     * @param player Player to check
     * @return true if the player is viewing the action bar, false otherwise
     */
    public boolean isPlayerViewing(Player player) {
        if (player == null) throw new IllegalArgumentException("Player cannot be null");
        return viewers.contains(player);
    }

    /**
     * Adds a player to the list of viewers for this action bar.
     *
     * @param player The player to add
     */
    public void addViewer(Player player, boolean multipleActionBars) {
        if (player == null) throw new IllegalArgumentException("Player cannot be null");
        if(DreamfireActionBarAPI.IsPlayerInActionBar(player) && !multipleActionBars) return;
        if (!viewers.contains(player)) {
            if (new ActionBarPlayerAddedEvent(this, player).isCancelled()) return;
            if (!DreamfirePlayerActionAPI.CanPlayerAction(DreamfirePlayerAction.PlayerActionBar, player.getUniqueId())) return;
            viewers.add(player);
        }
    }

    /**
     * Removes a player from the list of viewers.
     *
     * @param player The player to remove
     */
    public void removeViewer(Player player) {
        if (player == null) throw new IllegalArgumentException("Player cannot be null");
        if (viewers.remove(player)) new ActionBarPlayerRemovedEvent(this, player);
    }

    /**
     * Clears all viewers from this action bar.
     */
    public void clearViewers() {
        viewers.forEach(this::removeViewer);
        viewers.clear();
    }

    /**
     * Updates a specific frame of the action bar.
     *
     * @param index  Index of the frame to update
     * @param dreamfireActionBarData New frame data
     */
    public void updateFrame(int index, DreamfireActionBarData dreamfireActionBarData) {
        if (dreamfireActionBarData == null) throw new IllegalArgumentException("DreamfireActionBarData cannot be null");
        if (index >= 0 && index < barFrames.size()) barFrames.set(index, dreamfireActionBarData);
    }

    /**
     * Updates multiple frames of the action bar for performance optimization.
     *
     * @param frames A map of frame indices to frame data
     */
    public void updateMultipleFrames(Map<Integer, DreamfireActionBarData> frames) {
        frames.forEach((index, frame) -> {
            if (frame != null && index >= 0 && index < barFrames.size()) {
                barFrames.set(index, frame);
            }
        });
    }

    /**
     * Displays the next frame to all viewers.
     */
    public boolean displayNextFrame() {
        if (barFrames.isEmpty() || viewers.isEmpty()) return true;
        if(actionBarPaused) return false;
        DreamfireActionBarData dreamfireActionBarData = barFrames.get(currentFrameIndex);
        if (dreamfireActionBarData == null) throw new IllegalArgumentException("DreamfireActionBarData cannot be null");
        synchronized (this) {
            viewers.parallelStream().forEach(dreamfireActionBarData::displayActionBar);
        }
        currentFrameIndex = (currentFrameIndex + 1) % barFrames.size();
        return false;
    }

    /**
     * Pauses the action bar.
     */
    public void pause() {
        if (!actionBarPaused) {
            actionBarPaused = true;
            new ActionBarPausedEvent(this);
        }
    }

    /**
     * Resumes the action bar.
     */
    public void play() {
        if (actionBarPaused) {
            actionBarPaused = false;
            new ActionBarStartedEvent(this);
        }
    }

    /**
     * Stops the action bar, clears viewers, and deletes it.
     */
    public void stop() {
        actionBarPaused = true;
        new ActionBarStoppedEvent(this);
        clearViewers();
        DreamCore.GetDreamfireCore().DeleteDreamfireActionBar(barID);
    }

    public String serialize() {
        StringBuilder builder = new StringBuilder();
        builder.append(barID).append(";");
        barFrames.forEach(frame -> builder.append(frame.messageProvider()).append("|"));
        return builder.toString();
    }

    public static class Builder {

        private UUID barID = UUID.randomUUID();
        private final List<DreamfireActionBarData> barFrames = new CopyOnWriteArrayList<>();
        private final Set<Player> initialPlayers = ConcurrentHashMap.newKeySet();

        /**
         * Set a custom barID for the action bar.
         *
         * @param barID The custom UUID to set
         * @return The builder instance
         */
        public Builder barID(UUID barID) {
            if (barID != null) this.barID = barID;
            return this;
        }

        /**
         * Add a frame to the action bar with repeat count.
         *
         * @param frame The frame to add
         * @param repeatFrames The number of times to repeat the frame
         * @return The builder instance
         */
        public Builder addFrame(DreamfireActionBarData frame, int repeatFrames) {
            if (frame == null || repeatFrames <= 0) return this;
            for (int i = 0; i < repeatFrames; i++) barFrames.add(frame);
            return this;
        }

        /**
         * Add players to the initial list of viewers.
         *
         * @param player The player to add
         * @return The builder instance
         */
        public Builder addViewer(Player player) {
            if (player == null) throw new IllegalArgumentException("Player cannot be null");
            initialPlayers.add(player);
            return this;
        }

        /**
         * Builds the DreamfireActionBar with the provided configuration.
         *
         * @return The built action bar
         */
        public DreamfireActionBar build(boolean allowMultipleActionBars) {
            if (barFrames.isEmpty()) throw new IllegalArgumentException("At least one frame must be added.");
            DreamfireActionBar storedActionBar = DreamCore.GetDreamfireCore().GetDreamfireActionBar(barID);
            if (storedActionBar != null) {
                initialPlayers.forEach(player -> storedActionBar.addViewer(player, allowMultipleActionBars));
                return storedActionBar;
            }
            DreamfireActionBar actionBar = new DreamfireActionBar();
            actionBar.barID = barID;
            actionBar.barFrames = barFrames;
            initialPlayers.forEach(player -> storedActionBar.addViewer(player, allowMultipleActionBars));
            return DreamCore.GetDreamfireCore().AddDreamfireActionBar(barID, actionBar);
        }

        public DreamfireActionBar buildFromString(String serialized) {
            var parts = serialized.split(";");
            var barID = UUID.fromString(parts[0]);
            List<DreamfireActionBarData> frames = new CopyOnWriteArrayList<>();
            if (parts.length > 1) {
                var frameStrings = parts[1].split("\\|");
                for (String frameString : frameStrings) frames.add(new DreamfireActionBarData(player -> frameString));
            }
            var actionBar = new DreamfireActionBar();
            actionBar.barID = barID;
            actionBar.barFrames = frames;
            return actionBar;
        }
    }
}