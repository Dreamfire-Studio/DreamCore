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
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeSet;
import java.util.function.Supplier;

/// <summary>
/// Represents a single “frame” of a sidebar scoreboard: a title plus a set of numbered lines.
/// </summary>
/// <remarks>
/// Lines are keyed by their score; higher scores render nearer the top.
/// </remarks>
/// <example>
/// <code>
/// DreamScoreboardLines frame = DreamScoreboardLines.builder()
///     .addLine(5, new DreamScoreboardData(i -&gt; "Top"))
///     .addLine(1, new DreamScoreboardData(i -&gt; "Bottom"))
///     .build(() -&gt; "Title");
/// </code>
/// </example>
public class DreamScoreboardLines {

    private Supplier<String> titleSupplier;
    private Map<Integer, DreamScoreboardData> lines = new LinkedHashMap<>();

    /// <summary>
    /// Creates the sidebar objective and line teams/entries for this frame.
    /// </summary>
    /// <param name="scoreboard">Target scoreboard.</param>
    /// <param name="objectiveId">Stable objective id.</param>
    public void createSidebar(Scoreboard scoreboard, String objectiveId) {
        Objective obj = scoreboard.getObjective(objectiveId);
        if (obj == null) {
            obj = scoreboard.registerNewObjective(objectiveId, Criteria.DUMMY, Component.text(titleSupplier.get()));
        }
        obj.displayName(Component.text(titleSupplier.get()));
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);

        // Build lines once (higher score is rendered nearer the top)
        int teamCount = 0;
        for (Integer score : new TreeSet<>(lines.keySet()).descendingSet()) {
            lines.get(score).createLine(scoreboard, score, teamCount++);
        }
    }

    /// <summary>
    /// Updates the sidebar title and each line’s text for this frame.
    /// </summary>
    /// <param name="scoreboard">Target scoreboard.</param>
    public void updateSidebar(Scoreboard scoreboard) {
        Objective obj = scoreboard.getObjective(DisplaySlot.SIDEBAR);
        if (obj != null) obj.displayName(Component.text(titleSupplier.get()));
        for (Map.Entry<Integer, DreamScoreboardData> e : lines.entrySet()) {
            e.getValue().updateLine(scoreboard, e.getKey());
        }
    }

    // ---- Backward-compat names (match older calls) ----

    /// <summary>Back-compat. Use <see cref="createSidebar(Scoreboard, String)"/>.</summary>
    @Deprecated
    public void CreateLine(Scoreboard scoreboard, String objectiveId) {
        createSidebar(scoreboard, objectiveId);
    }

    /// <summary>Back-compat. Use <see cref="updateSidebar(Scoreboard)"/>.</summary>
    @Deprecated
    public void UpdateLine(Scoreboard scoreboard) {
        updateSidebar(scoreboard);
    }

    // ---- Builder ----

    /// <summary>
    /// Creates a builder for <see cref="DreamScoreboardLines"/>.
    /// </summary>
    public static PulseScoreboardLinesBuilder builder(){ return new PulseScoreboardLinesBuilder(); }

    /// <summary>
    /// Builder for constructing a scoreboard frame (title + lines).
    /// </summary>
    public static class PulseScoreboardLinesBuilder {
        private final Map<Integer, DreamScoreboardData> lines = new LinkedHashMap<>();

        /// <summary>
        /// Adds a line at a particular score.
        /// </summary>
        /// <param name="score">Score ordering (higher = higher on board).</param>
        /// <param name="line">Line data provider.</param>
        /// <returns>Self (for chaining).</returns>
        public PulseScoreboardLinesBuilder addLine(Integer score, DreamScoreboardData line){
            lines.put(score, line);
            return this;
        }

        /// <summary>
        /// Finalizes the frame with a title supplier.
        /// </summary>
        /// <param name="titleSupplier">Title supplier invoked on each update.</param>
        /// <returns>Constructed frame.</returns>
        public DreamScoreboardLines build(Supplier<String> titleSupplier){
            DreamScoreboardLines f = new DreamScoreboardLines();
            f.titleSupplier = titleSupplier;
            f.lines = this.lines;
            return f;
        }
    }
}