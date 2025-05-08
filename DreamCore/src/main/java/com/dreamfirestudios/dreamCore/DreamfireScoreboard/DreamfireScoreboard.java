package com.dreamfirestudios.dreamCore.DreamfireScoreboard;

import com.dreamfirestudios.dreamCore.DreamCore;
import com.dreamfirestudios.dreamCore.DreamfireScoreboard.Events.ScoreboardCreatedEvent;
import com.dreamfirestudios.dreamCore.DreamfireScoreboard.Events.ScoreboardPlayerAddedEvent;
import com.dreamfirestudios.dreamCore.DreamfireScoreboard.Events.ScoreboardPlayerRemovedEvent;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class DreamfireScoreboard {
    @Getter
    private String scoreboardID;
    private Scoreboard scoreboard;
    private List<DreamfireScoreboardLines> scoreboardFrames = new ArrayList<>();
    private int currentFrameIndex = 0;
    @Getter
    private List<Player> viewers;
    @Getter
    private boolean paused = true;

    public boolean isPlayerViewing(Player player) {
        return viewers.contains(player);
    }

    public void AddPlayer(Player player){
        if (!isPlayerViewing(player)){
            new ScoreboardPlayerAddedEvent(this, player);
            player.setScoreboard(scoreboard);
            viewers.add(player);
        }
    }

    public void RemovePlayer(Player player) {
        if(isPlayerViewing(player)){
            new ScoreboardPlayerRemovedEvent(this, player);
            player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
            viewers.remove(player);
        }
    }

    public void clearViewers() {
        for(var player : viewers) RemovePlayer(player);
    }

    public void DisplayNextFrame() {
        if (scoreboardFrames.isEmpty() || viewers.isEmpty() || paused) return;
        var currentFrame = scoreboardFrames.get(currentFrameIndex);
        currentFrame.UpdateLine(scoreboard);
        currentFrameIndex = (currentFrameIndex + 1) % scoreboardFrames.size();
    }

    public static PulseScoreboardBuilder builder(){ return new PulseScoreboardBuilder(); }
    public static class PulseScoreboardBuilder {
        private String scoreboardID = "Scoreboard";
        private List<Player> initialPlayers = new ArrayList<>();
        private List<DreamfireScoreboardLines> scoreboardFrames = new ArrayList<>();

        public PulseScoreboardBuilder scoreboardID(String scoreboardID){
            this.scoreboardID = scoreboardID;
            return this;
        }

        public PulseScoreboardBuilder addLineHolder(DreamfireScoreboardLines frame, int repeatFrames){
            if (frame == null || repeatFrames <= 0) throw new IllegalArgumentException("Frame cannot be null, and repeatFrames must be > 0");
            for (int i = 0; i < repeatFrames; i++) scoreboardFrames.add(frame);
            return this;
        }

        public PulseScoreboardBuilder addPlayer(Player... players){
            initialPlayers.addAll(new ArrayList<>(initialPlayers));
            return this;
        }

        public DreamfireScoreboard create(boolean replaceExisting){
            var existingDreamfireScoreboard = DreamCore.GetDreamfireCore().GetDreamfireScoreboard(scoreboardID);
            if(existingDreamfireScoreboard != null){
                initialPlayers.forEach(existingDreamfireScoreboard::AddPlayer);
                return existingDreamfireScoreboard;
            }
            var dreamfireScoreboard = new DreamfireScoreboard();
            dreamfireScoreboard.scoreboardID = scoreboardID;
            dreamfireScoreboard.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
            dreamfireScoreboard.scoreboardFrames = scoreboardFrames;
            initialPlayers.forEach(dreamfireScoreboard::AddPlayer);
            new ScoreboardCreatedEvent(dreamfireScoreboard);
            return DreamCore.GetDreamfireCore().AddDreamfireScoreboard(scoreboardID, dreamfireScoreboard);
        }
    }
}
