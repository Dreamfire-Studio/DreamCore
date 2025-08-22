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

/**
 * One “frame” of a sidebar scoreboard: a title + a set of numbered lines.
 * Lines are keyed by their score (higher score renders higher on the board).
 */
public class DreamScoreboardLines {

    private Supplier<String> titleSupplier;
    private Map<Integer, DreamScoreboardData> lines = new LinkedHashMap<>();

    /**
     * Create the sidebar objective and teams for this frame.
     * This is the method DreamScoreboard.ensureSidebarCreated() expects.
     */
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

    /**
     * Update the sidebar title + each line’s text for this frame.
     */
    public void updateSidebar(Scoreboard scoreboard) {
        Objective obj = scoreboard.getObjective(DisplaySlot.SIDEBAR);
        if (obj != null) obj.displayName(Component.text(titleSupplier.get()));
        for (Map.Entry<Integer, DreamScoreboardData> e : lines.entrySet()) {
            e.getValue().updateLine(scoreboard, e.getKey());
        }
    }

    // ---- Backward-compat names (match older calls) ----
    /** @deprecated use {@link #createSidebar(Scoreboard, String)} */
    @Deprecated
    public void CreateLine(Scoreboard scoreboard, String objectiveId) {
        createSidebar(scoreboard, objectiveId);
    }

    /** @deprecated use {@link #updateSidebar(Scoreboard)} */
    @Deprecated
    public void UpdateLine(Scoreboard scoreboard) {
        updateSidebar(scoreboard);
    }

    // ---- Builder ----
    public static PulseScoreboardLinesBuilder builder(){ return new PulseScoreboardLinesBuilder(); }

    public static class PulseScoreboardLinesBuilder {
        private final Map<Integer, DreamScoreboardData> lines = new LinkedHashMap<>();

        public PulseScoreboardLinesBuilder addLine(Integer score, DreamScoreboardData line){
            lines.put(score, line);
            return this;
        }

        public DreamScoreboardLines build(Supplier<String> titleSupplier){
            DreamScoreboardLines f = new DreamScoreboardLines();
            f.titleSupplier = titleSupplier;
            f.lines = this.lines;
            return f;
        }
    }
}