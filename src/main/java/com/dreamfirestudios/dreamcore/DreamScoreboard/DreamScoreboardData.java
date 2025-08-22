// src/main/java/com/dreamfirestudios/dreamcore/DreamScoreboard/DreamScoreboardData.java
package com.dreamfirestudios.dreamcore.DreamScoreboard;

import net.kyori.adventure.text.Component;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Scoreboard;

import java.util.function.Function;

/// <summary>
/// Holds a single scoreboard line's dynamic text function.
/// </summary>
/// <remarks>
/// The function is called with the line score/index every update to produce text.
/// </remarks>
/// <example>
/// <code>
/// DreamScoreboardData line = new DreamScoreboardData(score -&gt; "Kills: " + getKills());
/// </code>
/// </example>
public record DreamScoreboardData(Function<Integer, String> text) {

    /// <summary>
    /// Creates the team/entry for a line and places it on the sidebar at the given score.
    /// </summary>
    /// <param name="scoreboard">Target scoreboard.</param>
    /// <param name="score">Score value for ordering (higher = higher on the board).</param>
    /// <param name="teamIndex">Unique index to generate distinct entries.</param>
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

    /// <summary>
    /// Updates the prefix (visible text) for the existing team at this score.
    /// </summary>
    /// <param name="scoreboard">Target scoreboard.</param>
    /// <param name="score">Line score.</param>
    public void updateLine(Scoreboard scoreboard, int score) {
        var team = scoreboard.getTeam("Line" + score);
        if (team == null) return;
        team.prefix(Component.text(text.apply(score)));
    }

    // -------- Back-compat wrappers (keep old PascalCase calls compiling) --------

    /// <summary>
    /// Back-compat. Use <see cref="createLine(Scoreboard, int, int)"/>.
    /// </summary>
    @Deprecated public void CreateLine(Scoreboard scoreboard, int score, int teamIndex) {
        createLine(scoreboard, score, teamIndex);
    }

    /// <summary>
    /// Back-compat. Use <see cref="updateLine(Scoreboard, int)"/>.
    /// </summary>
    @Deprecated public void UpdateLine(Scoreboard scoreboard, int score) {
        updateLine(scoreboard, score);
    }
}