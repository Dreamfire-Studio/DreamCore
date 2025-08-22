// src/main/java/com/dreamfirestudios/dreamcore/DreamScoreboard/DreamScoreboard.java
package com.dreamfirestudios.dreamcore.DreamScoreboard;

import com.dreamfirestudios.dreamcore.DreamCore;
import com.dreamfirestudios.dreamcore.DreamJava.DreamClassID;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DreamScoreboard extends DreamClassID {
    private Scoreboard scoreboard;
    private List<DreamScoreboardLines> frames = new ArrayList<>();
    private int frameIndex = 0;

    @Getter private final List<Player> viewers = new ArrayList<>();
    @Getter private boolean paused = true;
    private boolean sidebarInitialized = false;

    public boolean isPlayerViewing(Player player) {
        return player != null && viewers.contains(player);
    }

    public void addPlayer(Player player){
        if (player == null || isPlayerViewing(player)) return;

        // fire cancellable event and honor it
        ScoreboardPlayerAddedEvent event = new ScoreboardPlayerAddedEvent(this, player);
        if (event.isCancelled()) return;

        player.setScoreboard(scoreboard);
        viewers.add(player);
    }

    public void removePlayer(Player player) {
        if (player == null || !isPlayerViewing(player)) return;

        ScoreboardPlayerRemovedEvent event = new ScoreboardPlayerRemovedEvent(this, player);
        if (event.isCancelled()) return;

        if (Bukkit.getScoreboardManager() != null) {
            player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
        }
        viewers.remove(player);
    }

    public void clearViewers() {
        // copy to avoid CME
        for (Player p : new ArrayList<>(viewers)) removePlayer(p);
    }

    /**
     * Advances the animated scoreboard by one frame.
     * Call this from your tick loop.
     */
    public void displayNextFrame() {
        if (frames.isEmpty() || viewers.isEmpty() || paused) return;

        ensureSidebarCreated();

        final DreamScoreboardLines current = frames.get(frameIndex);
        current.UpdateLine(scoreboard);

        frameIndex = (frameIndex + 1) % frames.size();
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }

    private void ensureSidebarCreated() {
        if (sidebarInitialized) return;

        // create a stable objective id; unique per-board
        final String objectiveId = "sb_" + getClassID().toString().replace("-", "").substring(0, 12);
        Objective existing = scoreboard.getObjective(DisplaySlot.SIDEBAR);
        if (existing == null) {
            // use the first frame to create teams/entries
            frames.get(0).CreateLine(scoreboard, objectiveId);
        }
        sidebarInitialized = true;
    }

    // -------- builder --------

    public static PulseScoreboardBuilder builder(){ return new PulseScoreboardBuilder(); }

    public static class PulseScoreboardBuilder {
        private final List<Player> initialPlayers = new ArrayList<>();
        private final List<DreamScoreboardLines> frames = new ArrayList<>();
        private boolean startPaused = true;

        /** Add the same frame repeatedly to stretch its on-screen time. */
        public PulseScoreboardBuilder addLineHolder(DreamScoreboardLines frame, int repeatFrames){
            if (frame == null || repeatFrames <= 0)
                throw new IllegalArgumentException("frame cannot be null and repeatFrames must be > 0");
            for (int i = 0; i < repeatFrames; i++) frames.add(frame);
            return this;
        }

        public PulseScoreboardBuilder addPlayer(Player... players){
            if (players != null) Collections.addAll(initialPlayers, players);
            return this;
        }

        public PulseScoreboardBuilder paused(boolean paused) {
            this.startPaused = paused;
            return this;
        }

        public DreamScoreboard create(boolean replaceExisting){
            DreamScoreboard board = new DreamScoreboard();
            // create backing board
            if (Bukkit.getScoreboardManager() == null)
                throw new IllegalStateException("ScoreboardManager is not ready");
            board.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
            board.frames = new ArrayList<>(frames);
            board.paused = startPaused;

            // fire created event
            new ScoreboardCreatedEvent(board);

            // attach viewers
            initialPlayers.forEach(board::addPlayer);

            // register in core (if you keep a registry)
            return DreamCore.DreamScoreboards.put(board.getClassID(), board);
        }
    }
}