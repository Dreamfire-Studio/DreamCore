package com.dreamfirestudios.dreamCore.DreamfireBossBar;

import com.dreamfirestudios.dreamCore.DreamCore;
import com.dreamfirestudios.dreamCore.DreamfireBossBar.Events.*;
import com.dreamfirestudios.dreamCore.DreamfirePlayer.DreamfirePlayerAction;
import com.dreamfirestudios.dreamCore.DreamfirePlayer.DreamfirePlayerActionAPI;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class DreamfireBossBar {
    @Getter private UUID bossBarID;
    private List<DreamfireBossBarData> dreamfireBossBarData;
    private BarFlag[] barFlags;
    @Getter private BossBar bossBar;
    private int currentFrameIndex;
    @Getter private boolean bossBarPaused = true;
    private final List<UUID> persistentPlayers = new ArrayList<>();

    /**
     * Checks if the player has the boss bar displayed.
     *
     * @param player The player to check.
     * @return True if the player is seeing this boss bar, false otherwise.
     */
    public boolean IisPlayer(Player player){
        return persistentPlayers.contains(player.getUniqueId());
    }

    /**
     * Resets the boss bar to the first frame and reinitializes it.
     */
    public void resetBossBar(){
        currentFrameIndex = 0;
        var firstBarData = dreamfireBossBarData.get(currentFrameIndex);
        this.bossBar = Bukkit.createBossBar(firstBarData.messageProvider().apply(null), firstBarData.barColor(), firstBarData.barStyle(), barFlags);
    }

    /**
     * Adds one or more players to the boss bar.
     *
     * @param players The players to add.
     */
    public void addPlayer(Player... players){
        for(var player : players) addPlayer(player);
    }

    /**
     * Adds a single player to the boss bar.
     *
     * @param player The player to add.
     */
    public void addPlayer(Player player) {
        if(persistentPlayers.contains(player.getUniqueId())) return;
        if(new BossBarPlayerAddedEvent(this, player).isCancelled()) return;
        if(!DreamfirePlayerActionAPI.CanPlayerAction(DreamfirePlayerAction.PlayerBossBar, player.getUniqueId())) return;
        bossBar.addPlayer(player);
        persistentPlayers.add(player.getUniqueId());
    }

    /**
     * Removes a player from the boss bar.
     *
     * @param player The player to remove.
     */
    public void removePlayer(Player player){
        if(!persistentPlayers.contains(player.getUniqueId())) return;
        new BossBarPlayerRemovedEvent(this, player);
        bossBar.removePlayer(player);
        persistentPlayers.remove(player.getUniqueId());
    }

    /**
     * Removes all players from the boss bar.
     */
    public void removeAllPlayers(){
        for(var playerUUID : persistentPlayers){
            var player = Bukkit.getPlayer(playerUUID);
            if(player == null) continue;
            removePlayer(player);
        }
    }

    /**
     * Displays the next frame in the boss bar sequence.
     */
    public void displayNextFrame(){
        if (dreamfireBossBarData.isEmpty() || persistentPlayers.isEmpty() || bossBarPaused) return;
        var currentFrame = dreamfireBossBarData.get(currentFrameIndex);
        for (var playerUUID : persistentPlayers){
            var player = Bukkit.getPlayer(playerUUID);
            if(player == null) continue;
            currentFrame.DisplayBarData(bossBar, player);
        }
        currentFrameIndex = (currentFrameIndex + 1) % dreamfireBossBarData.size();
    }

    /**
     * Pauses the boss bar.
     */
    public void pause(){
        if(!bossBarPaused){
            bossBarPaused = true;
            new BossBarPausedEvent(this);
        }
    }

    /**
     * Resumes the boss bar.
     */
    public void play(){
        if(bossBarPaused){
            bossBarPaused = false;
            new BossBarStartedEvent(this);
        }
    }

    /**
     * Stops the boss bar, removes all players, and deletes the instance.
     *
     * @return The instance of the stopped boss bar.
     */
    public DreamfireBossBar stop() {
        bossBarPaused = true;
        new BossBarStoppedEvent(this);
        removeAllPlayers();
        return DreamCore.GetDreamfireCore().DeleteDreamfireBossBar(bossBarID);
    }

    public static final class Builder {
        private UUID bossBarID = UUID.randomUUID();
        private List<DreamfireBossBarData> dreamfireBossBarData = new ArrayList<>();
        private List<Player> players = new ArrayList<>();

        /**
         * Set the unique identifier for the boss bar.
         *
         * @param bossBarID The UUID for the boss bar.
         * @return The builder instance for chaining.
         */
        public Builder bossBarID(UUID bossBarID) {
            this.bossBarID = bossBarID;
            return this;
        }

        /**
         * Set the bar data (frames) for the boss bar.
         *
         * @param barData      The data for the boss bar frame.
         * @param numberOfFrames The number of times to repeat the frame data.
         * @return The builder instance for chaining.
         */
        public Builder dreamfireBossBarData(DreamfireBossBarData barData, int numberOfFrames) {
            if (numberOfFrames <= 0) throw new IllegalArgumentException("Number of frames must be greater than zero");
            for (var i = 0; i < numberOfFrames; i++) this.dreamfireBossBarData.add(barData);
            return this;
        }

        /**
         * Add players to the boss bar.
         *
         * @param toAdd The players to add.
         * @return The builder instance for chaining.
         */
        public Builder players(Player... toAdd) {
            if (toAdd == null || toAdd.length == 0) throw new IllegalArgumentException("Players cannot be null or empty");
            this.players.addAll(Arrays.asList(toAdd));
            return this;
        }

        /**
         * Build the DreamfireBossBar instance.
         *
         * @param barFlags Optional flags to customize the boss bar.
         * @return The created DreamfireBossBar instance.
         */
        public DreamfireBossBar build(BarFlag... barFlags) {
            if (dreamfireBossBarData.isEmpty()) throw new IllegalArgumentException("Boss bar data must not be empty");
            var storedBossBar = DreamCore.GetDreamfireCore().GetDreamfireBossBar(bossBarID);
            if(storedBossBar != null){
                players.forEach(storedBossBar::addPlayer);
                return storedBossBar;
            }
            var dreamfireBossBar = new DreamfireBossBar();
            dreamfireBossBar.bossBarID = bossBarID;
            dreamfireBossBar.dreamfireBossBarData = dreamfireBossBarData;
            dreamfireBossBar.barFlags = barFlags;
            players.forEach(storedBossBar::addPlayer);
            return DreamCore.GetDreamfireCore().AddDreamfireBossBar(bossBarID, dreamfireBossBar);
        }
    }
}
