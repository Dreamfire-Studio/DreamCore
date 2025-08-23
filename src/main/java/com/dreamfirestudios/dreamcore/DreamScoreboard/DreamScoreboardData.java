/*
 * MIT License
 *
 * Copyright (c) 2025 Dreamfire Studio
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
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