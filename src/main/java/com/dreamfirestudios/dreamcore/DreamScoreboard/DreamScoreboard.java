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

/// <summary>
/// Manages an animated sidebar scoreboard composed of multiple frames.
/// </summary>
/// <remarks>
/// Use the <see cref="builder()"/> to create instances and attach players.
/// Call <see cref="displayNextFrame()"/> from your tick/task loop to animate.
/// </remarks>
/// <example>
/// <code>
/// var frame = DreamScoreboardLines.builder()
///     .addLine(3, new DreamScoreboardData(i -&gt; "Line 3"))
///     .addLine(2, new DreamScoreboardData(i -&gt; "Line 2"))
///     .addLine(1, new DreamScoreboardData(i -&gt; "Line 1"))
///     .build(() -&gt; "My Title");
///
/// DreamScoreboard board = DreamScoreboard.builder()
///     .addLineHolder(frame, 20) // show same frame for 20 ticks
///     .addPlayer(player)
///     .paused(false)
///     .create(true);
///
/// // in a repeating task:
/// board.displayNextFrame();
/// </code>
/// </example>
public class DreamScoreboard extends DreamClassID {
    private Scoreboard scoreboard;
    private List<DreamScoreboardLines> frames = new ArrayList<>();
    private int frameIndex = 0;

    /// <summary>Players currently viewing this scoreboard.</summary>
    @Getter private final List<Player> viewers = new ArrayList<>();
    /// <summary>Whether animation is paused.</summary>
    @Getter private boolean paused = true;
    private boolean sidebarInitialized = false;

    /// <summary>
    /// Checks whether a player is currently viewing this scoreboard.
    /// </summary>
    /// <param name="player">Player to check.</param>
    /// <returns><c>true</c> if the player is a viewer; otherwise <c>false</c>.</returns>
    public boolean isPlayerViewing(Player player) {
        return player != null && viewers.contains(player);
    }

    /// <summary>
    /// Adds a player as a viewer of this scoreboard.
    /// </summary>
    /// <param name="player">Player to add.</param>
    /// <remarks>
    /// Fires <see cref="ScoreboardPlayerAddedEvent"/> (cancellable).
    /// If cancelled, the player will not be added.
    /// </remarks>
    public void addPlayer(Player player){
        if (player == null || isPlayerViewing(player)) return;

        // fire cancellable event and honor it
        ScoreboardPlayerAddedEvent event = new ScoreboardPlayerAddedEvent(this, player);
        if (event.isCancelled()) return;

        player.setScoreboard(scoreboard);
        viewers.add(player);
    }

    /// <summary>
    /// Removes a player from viewing this scoreboard.
    /// </summary>
    /// <param name="player">Player to remove.</param>
    /// <remarks>
    /// Fires <see cref="ScoreboardPlayerRemovedEvent"/> (cancellable).
    /// If not cancelled, the player's scoreboard is reset to the main scoreboard.
    /// </remarks>
    public void removePlayer(Player player) {
        if (player == null || !isPlayerViewing(player)) return;

        ScoreboardPlayerRemovedEvent event = new ScoreboardPlayerRemovedEvent(this, player);
        if (event.isCancelled()) return;

        if (Bukkit.getScoreboardManager() != null) {
            player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
        }
        viewers.remove(player);
    }

    /// <summary>
    /// Removes all viewers from this scoreboard.
    /// </summary>
    public void clearViewers() {
        // copy to avoid CME
        for (Player p : new ArrayList<>(viewers)) removePlayer(p);
    }

    /// <summary>
    /// Advances the animated scoreboard by one frame.
    /// Call this from your repeating task.
    /// </summary>
    public void displayNextFrame() {
        if (frames.isEmpty() || viewers.isEmpty() || paused) return;

        ensureSidebarCreated();

        final DreamScoreboardLines current = frames.get(frameIndex);
        current.UpdateLine(scoreboard);

        frameIndex = (frameIndex + 1) % frames.size();
    }

    /// <summary>
    /// Sets the paused state for animation.
    /// </summary>
    /// <param name="paused"><c>true</c> to pause; <c>false</c> to resume.</param>
    public void setPaused(boolean paused) {
        this.paused = paused;
    }

    /// <summary>
    /// Ensures the sidebar objective and entries are created using the first frame.
    /// </summary>
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

    /// <summary>
    /// Creates a builder for constructing a <see cref="DreamScoreboard"/>.
    /// </summary>
    /// <returns>Builder instance.</returns>
    public static PulseScoreboardBuilder builder(){ return new PulseScoreboardBuilder(); }

    /// <summary>
    /// Builder for assembling a scoreboard with frames and initial viewers.
    /// </summary>
    public static class PulseScoreboardBuilder {
        private final List<Player> initialPlayers = new ArrayList<>();
        private final List<DreamScoreboardLines> frames = new ArrayList<>();
        private boolean startPaused = true;

        /// <summary>
        /// Adds a frame multiple times to extend its on-screen duration.
        /// </summary>
        /// <param name="frame">Frame to repeat.</param>
        /// <param name="repeatFrames">Number of repetitions (&gt; 0).</param>
        /// <returns>Self (for chaining).</returns>
        /// <exception cref="IllegalArgumentException">If <paramref name="frame"/> is null or <paramref name="repeatFrames"/> ≤ 0.</exception>
        /// <example>
        /// <code>
        /// builder.addLineHolder(frame, 20); // ~1 second at 20 TPS
        /// </code>
        /// </example>
        public PulseScoreboardBuilder addLineHolder(DreamScoreboardLines frame, int repeatFrames){
            if (frame == null || repeatFrames <= 0)
                throw new IllegalArgumentException("frame cannot be null and repeatFrames must be > 0");
            for (int i = 0; i < repeatFrames; i++) frames.add(frame);
            return this;
        }

        /// <summary>
        /// Adds one or more players as initial viewers.
        /// </summary>
        /// <param name="players">Players to add.</param>
        /// <returns>Self (for chaining).</returns>
        public PulseScoreboardBuilder addPlayer(Player... players){
            if (players != null) Collections.addAll(initialPlayers, players);
            return this;
        }

        /// <summary>
        /// Sets the initial paused state.
        /// </summary>
        /// <param name="paused"><c>true</c> to start paused; otherwise <c>false</c>.</param>
        /// <returns>Self (for chaining).</returns>
        public PulseScoreboardBuilder paused(boolean paused) {
            this.startPaused = paused;
            return this;
        }

        /// <summary>
        /// Builds the scoreboard, fires <see cref="ScoreboardCreatedEvent"/>, and adds initial viewers.
        /// </summary>
        /// <param name="replaceExisting">Kept for API compatibility; does not alter behavior in this implementation.</param>
        /// <returns>The constructed and registered scoreboard.</returns>
        /// <example>
        /// <code>
        /// DreamScoreboard board = DreamScoreboard.builder()
        ///     .addLineHolder(frame, 40)
        ///     .addPlayer(player1, player2)
        ///     .paused(false)
        ///     .create(true);
        /// </code>
        /// </example>
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