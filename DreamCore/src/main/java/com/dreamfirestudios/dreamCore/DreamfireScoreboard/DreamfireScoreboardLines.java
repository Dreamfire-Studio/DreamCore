package com.dreamfirestudios.dreamCore.DreamfireScoreboard;

import com.dreamfirestudios.dreamCore.DreamfireChat.DreamfireMessage;
import net.kyori.adventure.text.Component;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Scoreboard;

import java.util.HashMap;
import java.util.function.Function;
import java.util.function.Supplier;

public class DreamfireScoreboardLines {
    private Supplier<String> scoreboardTitle;
    private HashMap<Integer, DreamfireScoreboardData> scoreboardLines;

    public void CreateLine(Scoreboard scoreboard, String scoreboardID){
        var objective = scoreboard.registerNewObjective(scoreboardID, Criteria.DUMMY, Component.text(scoreboardTitle.get()));
        objective.displayName(Component.text(scoreboardTitle.get()));
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        var teamCount = 0;
        for(var index : scoreboardLines.keySet()){
            scoreboardLines.get(index).CreateLine(scoreboard, index, teamCount);
            teamCount += 1;
        }
    }

    public void UpdateLine(Scoreboard scoreboard){
        scoreboard.getObjective(DisplaySlot.SIDEBAR).displayName(Component.text(scoreboardTitle.get()));
        for(var index : scoreboardLines.keySet()) scoreboardLines.get(index).UpdateLine(scoreboard, index);
    }

    public static PulseScoreboardLinesBuilder builder(){ return new PulseScoreboardLinesBuilder(); }
    public static class PulseScoreboardLinesBuilder {
        private HashMap<Integer, DreamfireScoreboardData> scoreboardLines = new HashMap<>();

        public PulseScoreboardLinesBuilder addLine(Integer pos, DreamfireScoreboardData scoreboardLine){
            scoreboardLines.put(pos, scoreboardLine);
            return this;
        }

        public DreamfireScoreboardLines build(Supplier<String> scoreboardTitle){
            if(scoreboardLines.isEmpty()) return null;
            var dreamfireScoreboardLines = new DreamfireScoreboardLines();
            dreamfireScoreboardLines.scoreboardTitle = scoreboardTitle;
            dreamfireScoreboardLines.scoreboardLines = scoreboardLines;
            return dreamfireScoreboardLines;
        }
    }
}
