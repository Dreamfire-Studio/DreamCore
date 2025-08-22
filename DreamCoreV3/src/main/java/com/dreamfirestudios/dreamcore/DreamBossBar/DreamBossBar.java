package com.dreamfirestudios.dreamcore.DreamBossBar;

import com.dreamfirestudios.dreamcore.DreamCore;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

import java.util.*;

/**
 /// <summary>
 /// Animated boss bar with viewer management and frame sequencing.
 /// </summary>
 /// <remarks>
 /// Use <see cref="Builder"/> to create instances.
 /// Fires events for player add/remove, pause/play/stop, and frame advancement.
 /// </remarks>
 */
public class DreamBossBar {

    @Getter private UUID bossBarID;
    private List<DreamBossBarData> frames;
    private BarFlag[] barFlags;
    @Getter private BossBar bossBar;
    private int currentFrameIndex;
    @Getter private boolean bossBarPaused = true;
    private final List<UUID> viewers = new ArrayList<>();

    /// <summary>
    /// Checks if the player currently has this boss bar.
    /// </summary>
    /// <param name="player">The player to check.</param>
    /// <returns>True if the player is a viewer; otherwise false.</returns>
    public boolean isPlayer(Player player){
        if (player == null) throw new IllegalArgumentException("Player cannot be null.");
        return viewers.contains(player.getUniqueId());
    }

    /// <summary>
    /// Resets the bar back to frame 0 and reinitializes the underlying BossBar.
    /// </summary>
    public void resetBossBar(){
        if (frames == null || frames.isEmpty()) return;
        currentFrameIndex = 0;
        var first = frames.get(0);
        if (bossBar == null) {
            bossBar = Bukkit.createBossBar(first.safeTitle(null), first.barColor(), first.barStyle(), barFlags);
        } else {
            bossBar.setTitle(first.safeTitle(null));
            bossBar.setColor(first.barColor());
            bossBar.setStyle(first.barStyle());
            bossBar.setProgress(first.clampedProgress());
        }
    }

    /// <summary>
    /// Adds one or more players to the boss bar.
    /// </summary>
    public void addPlayer(Player... players){
        if (players == null) return;
        for (var p : players) {
            if (p != null) addPlayer(p);
        }
    }

    /// <summary>
    /// Adds a single player to the boss bar.
    /// </summary>
    /// <param name="player">The player to add.</param>
    public void addPlayer(Player player) {
        if (player == null) throw new IllegalArgumentException("Player cannot be null.");
        var id = player.getUniqueId();
        if (viewers.contains(id)) return;
        if (new BossBarPlayerAddedEvent(this, player).isCancelled()) return;
        ensureBossBarInitialized();
        bossBar.addPlayer(player);
        viewers.add(id);
    }

    /// <summary>
    /// Removes a player from the boss bar.
    /// </summary>
    public void removePlayer(Player player){
        if (player == null) throw new IllegalArgumentException("Player cannot be null.");
        var id = player.getUniqueId();
        if (!viewers.contains(id)) return;
        new BossBarPlayerRemovedEvent(this, player);
        if (bossBar != null) bossBar.removePlayer(player);
        viewers.remove(id);
    }

    /// <summary>
    /// Removes all players from the boss bar.
    /// </summary>
    public void removeAllPlayers(){
        if (viewers.isEmpty()) return;
        // Create a copy to avoid CME
        List<UUID> copy = new ArrayList<>(viewers);
        for (var uuid : copy){
            var player = Bukkit.getPlayer(uuid);
            if (player != null) removePlayer(player);
        }
    }

    /// <summary>
    /// Advances to the next frame and updates the bar for all viewers.
    /// </summary>
    public void displayNextFrame(){
        if (frames == null || frames.isEmpty() || viewers.isEmpty() || bossBarPaused) return;
        ensureBossBarInitialized();

        var frame = frames.get(currentFrameIndex);
        for (var uuid : viewers){
            var player = Bukkit.getPlayer(uuid);
            if (player == null) continue;
            frame.DisplayBarData(bossBar, player);
        }

        new BossBarFrameAdvancedEvent(this, currentFrameIndex);

        currentFrameIndex = (currentFrameIndex + 1) % frames.size();
    }

    /// <summary>
    /// Pauses the animation (no frame progression).
    /// </summary>
    public void pause(){
        if (!bossBarPaused){
            bossBarPaused = true;
            new BossBarPausedEvent(this);
        }
    }

    /// <summary>
    /// Resumes the animation (frame progression continues).
    /// </summary>
    public void play(){
        if (bossBarPaused){
            bossBarPaused = false;
            new BossBarStartedEvent(this);
        }
    }

    /// <summary>
    /// Stops the boss bar, removes viewers, and unregisters from the core registry.
    /// </summary>
    /// <returns>The removed instance, if any, from the core registry.</returns>
    public DreamBossBar stop() {
        bossBarPaused = true;
        new BossBarStoppedEvent(this);
        removeAllPlayers();
        return DreamCore.DreamBossBars.remove(bossBarID);
    }

    /// <summary>
    /// Ensure the underlying Bukkit BossBar exists using the current frame settings.
    /// </summary>
    private void ensureBossBarInitialized() {
        if (bossBar != null) return;
        if (frames == null || frames.isEmpty()) {
            bossBar = Bukkit.createBossBar("", org.bukkit.boss.BarColor.WHITE, org.bukkit.boss.BarStyle.SOLID, barFlags);
            return;
        }
        var first = frames.get(0);
        bossBar = Bukkit.createBossBar(first.safeTitle(null), first.barColor(), first.barStyle(), barFlags);
        bossBar.setProgress(first.clampedProgress());
    }

    /**
     /// <summary>
     /// Builder for <see cref="DreamBossBar"/>.
     /// </summary>
     */
    public static final class Builder {
        private UUID bossBarID = UUID.randomUUID();
        private final List<DreamBossBarData> frames = new ArrayList<>();
        private final List<Player> players = new ArrayList<>();

        /// <summary>Sets the unique identifier for the boss bar.</summary>
        public Builder bossBarID(UUID bossBarID) {
            if (bossBarID != null) this.bossBarID = bossBarID;
            return this;
        }

        /// <summary>
        /// Adds a frame repeated <paramref name="numberOfFrames"/> times.
        /// </summary>
        public Builder dreamfireBossBarData(DreamBossBarData barData, int numberOfFrames) {
            if (barData == null) throw new IllegalArgumentException("Bar data cannot be null.");
            if (numberOfFrames <= 0) throw new IllegalArgumentException("Number of frames must be > 0.");
            for (int i = 0; i < numberOfFrames; i++) frames.add(barData);
            return this;
        }

        /// <summary>Adds players to be attached at build time.</summary>
        public Builder players(Player... toAdd) {
            if (toAdd == null) return this;
            for (var p : toAdd) if (p != null) players.add(p);
            return this;
        }

        /// <summary>
        /// Builds (or reuses) a <see cref="DreamBossBar"/> and registers it with the core.
        /// </summary>
        /// <param name="barFlags">Optional Bukkit bar flags.</param>
        /// <returns>The created or stored instance.</returns>
        public DreamBossBar build(BarFlag... barFlags) {
            if (frames.isEmpty()) throw new IllegalArgumentException("Boss bar frames must not be empty.");

            var existing = DreamCore.DreamBossBars.getOrDefault(bossBarID, null);
            if (existing != null) {
                players.forEach(existing::addPlayer);
                return existing;
            }

            var dbb = new DreamBossBar();
            dbb.bossBarID = bossBarID;
            dbb.frames = new ArrayList<>(frames);
            dbb.barFlags = (barFlags == null ? new BarFlag[0] : barFlags.clone());
            dbb.resetBossBar();

            // Attach initial players to the new instance, not to 'existing'
            players.forEach(dbb::addPlayer);

            return DreamCore.DreamBossBars.put(bossBarID, dbb);
        }
    }
}