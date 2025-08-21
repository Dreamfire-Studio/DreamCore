package com.dreamfirestudios.dreamcore.DreamActionBar;

import com.dreamfirestudios.dreamcore.DreamJava.DreamClassID;
import com.dreamfirestudios.dreamcore.DreamCore;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class DreamActionBar extends DreamClassID {
    private final List<DreamActionBarData> barFrames = new ArrayList<>();
    private final List<Player> viewers = new ArrayList<>();
    private int currentFrameIndex;
    private boolean paused;

    public boolean isPlayerViewing(Player player) {
        if (player == null) throw new IllegalArgumentException("Player cannot be null");
        return viewers.contains(player);
    }

    public void addViewer(Player player, boolean multipleActionBars) {
        if (player == null) throw new IllegalArgumentException("Player cannot be null");
        if(DreamActionBarAPI.IsPlayerInActionBar(player) && !multipleActionBars) return;
        if(viewers.contains(player) || new DreamActionBarPlayerAdded(this, player).isCancelled()) return;
        viewers.add(player);
    }

    public void removeViewer(Player player) {
        if (player == null) throw new IllegalArgumentException("Player cannot be null");
        if(!viewers.contains(player) || new DreamActionBarPlayerRemoved(this, player).isCancelled()) return;
        viewers.remove(player);
    }

    public void clearViewers() {
        viewers.forEach(this::removeViewer);
        viewers.clear();
    }

    public void updateFrame(int index, DreamActionBarData dreamActionBarData) {
        if (dreamActionBarData == null) throw new IllegalArgumentException("DreamfireActionBarData cannot be null");
        if (index >= 0 && index < barFrames.size()) barFrames.set(index, dreamActionBarData);
    }

    public boolean displayNextFrame() {
        if (barFrames.isEmpty() || viewers.isEmpty()) return true;
        if(paused) return false;
        DreamActionBarData dreamActionBarData = barFrames.get(currentFrameIndex);
        if (dreamActionBarData == null) throw new IllegalArgumentException("DreamfireActionBarData cannot be null");
        viewers.parallelStream().forEach(dreamActionBarData::displayActionBar);
        currentFrameIndex = (currentFrameIndex + 1) % barFrames.size();
        return false;
    }

    public void pause() {
        if (!paused && !new DreamActionBarPaused(this).isCancelled()) {
            paused = true;
        }
    }

    public void play() {
        if (paused && !new DreamActionBarPlayed(this).isCancelled()) {
            paused = false;
        }
    }

    public void stop() {
        if(!new DreamActionBarStopped(this).isCancelled()){
            DreamCore.DreamActionBars.remove(getClassID());
            clearViewers();
        }
    }

    public static class Builder {
        private final List<DreamActionBarData> barFrames = new ArrayList<>();
        private final List<Player> viewers = new ArrayList<>();

        public Builder addFrame(DreamActionBarData frame, int repeatFrames) {
            if (frame == null || repeatFrames <= 0) return this;
            for (int i = 0; i < repeatFrames; i++) barFrames.add(frame);
            return this;
        }

        public Builder addViewer(Player player) {
            if (player == null) throw new IllegalArgumentException("Player cannot be null");
            viewers.add(player);
            return this;
        }

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
