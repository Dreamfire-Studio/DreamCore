package com.dreamfirestudios.dreamcore.DreamScoreboard;

import net.kyori.adventure.text.Component;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Scoreboard;

import java.util.function.Function;

/**
 * Holder for one scoreboard line's content. The function is invoked with the
 * line's score/index each time we render/update.
 */
public record DreamScoreboardData(Function<Integer, String> text) {

    /**
     * Create a line entry (team + unique entry) and place it on the sidebar with the given score.
     *
     * @param scoreboard the scoreboard
     * @param score      score value for this line (higher score renders higher)
     * @param teamIndex  running index to help generate a unique entry string
     */
    public void createLine(Scoreboard scoreboard, int score, int teamIndex) {
        final String teamName = "Line" + score;
        if (scoreboard.getTeam(teamName) != null) return;

        // Unique entry per line: repeat §r N times so each entry differs.
        final StringBuilder entry = new StringBuilder();
        for (int i = 0; i < teamIndex; i++) entry.append('§').append('r');

        var team = scoreboard.registerNewTeam(teamName);
        team.addEntry(entry.toString());
        team.prefix(Component.text(text.apply(score)));

        var obj = scoreboard.getObjective(DisplaySlot.SIDEBAR);
        if (obj != null) obj.getScore(entry.toString()).setScore(score);
    }

    /**
     * Update the prefix (visible text) for the existing team of this score.
     */
    public void updateLine(Scoreboard scoreboard, int score) {
        var team = scoreboard.getTeam("Line" + score);
        if (team == null) return;
        team.prefix(Component.text(text.apply(score)));
    }

    // -------- Back-compat wrappers (keep old PascalCase calls compiling) --------

    /** @deprecated use {@link #createLine(Scoreboard, int, int)} */
    @Deprecated public void CreateLine(Scoreboard scoreboard, int score, int teamIndex) {
        createLine(scoreboard, score, teamIndex);
    }

    /** @deprecated use {@link #updateLine(Scoreboard, int)} */
    @Deprecated public void UpdateLine(Scoreboard scoreboard, int score) {
        updateLine(scoreboard, score);
    }
}