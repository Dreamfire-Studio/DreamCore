package com.dreamfirestudios.dreamCore.DreamfireScoreboard;

import com.dreamfirestudios.dreamCore.DreamfireChat.DreamfireMessage;
import net.kyori.adventure.text.Component;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Scoreboard;

import java.util.function.BiFunction;
import java.util.function.Function;

public record DreamfireScoreboardData(Function<Integer, String> text) {
    public void CreateLine(Scoreboard scoreboard, int lineNumber, int teamCount){
        var team = scoreboard.registerNewTeam("Line" + lineNumber);
        var teamText = new StringBuilder();
        teamText.append("§r".repeat(Math.max(0, teamCount)));
        team.addEntry(teamText.toString());
        team.prefix(Component.text(text.apply(lineNumber)));
        scoreboard.getObjective(DisplaySlot.SIDEBAR).getScore(teamText.toString()).setScore(lineNumber);
    }

    public void UpdateLine(Scoreboard scoreboard, int lineNumber){
        var team = scoreboard.getTeam("Line" + lineNumber);
        if(team == null) return;
        team.prefix(Component.text(text.apply(lineNumber)));
    }
}
