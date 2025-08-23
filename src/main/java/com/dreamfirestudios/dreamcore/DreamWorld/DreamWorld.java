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
package com.dreamfirestudios.dreamcore.DreamWorld;

import com.dreamfirestudios.dreamcore.DreamCore;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.UUID;

/// <summary>
/// Per-world locks for time, difficulty, and player attributes such as gamemode,
/// health, hunger, and saturation.
/// </summary>
/// <remarks>
/// Call <see cref="TickWorld()"/> periodically (e.g., each tick) to enforce locks.
/// World lookup uses the stored <see cref="UUID"/>; if missing, the instance
/// unregisters itself from <see cref="DreamCore#DreamWorlds"/>.
/// </remarks>
/// <example>
/// <code>
/// var dw = new DreamWorld(world.getUID());
/// dw.setTimeLock(TimeLock.Noon);
/// dw.setDifficultyLock(Difficulty.HARD);
/// DreamCore.DreamWorlds.put(world.getUID(), dw);
/// </code>
/// </example>
public class DreamWorld {

    /// <summary>
    /// Unique identifier of the world this wrapper controls.
    /// </summary>
    @Getter private UUID worldUUID;

    /// <summary>
    /// Optional time lock; if set, the world's time is fixed to this value.
    /// </summary>
    @Setter private TimeLock timeLock;

    /// <summary>
    /// Optional difficulty lock; if set, the world's difficulty is fixed.
    /// </summary>
    @Setter private Difficulty difficultyLock;

    /// <summary>
    /// Optional gamemode lock; if set, players in this world are forced into this mode.
    /// </summary>
    @Setter private GameMode gameModeLock;

    /// <summary>
    /// Optional health lock; if set, players in this world have their health fixed to this value.
    /// </summary>
    @Setter private Integer heartLock;

    /// <summary>
    /// Optional hunger lock; if set, players in this world have their hunger fixed to this value.
    /// </summary>
    @Setter private Integer hungerLock;

    /// <summary>
    /// Optional saturation lock; if set, players in this world have their saturation fixed to this value.
    /// </summary>
    @Setter private Integer saturationLock;

    /// <summary>
    /// Constructs a DreamWorld wrapper for the given world UUID.
    /// </summary>
    /// <param name="worldUUID">The UUID of the Bukkit world to control.</param>
    public DreamWorld(UUID worldUUID){
        this.worldUUID = worldUUID;
    }

    /// <summary>
    /// Resolves the Bukkit <see cref="World"/> for this UUID.
    /// </summary>
    /// <remarks>
    /// If no world is found for this UUID, the DreamWorld is automatically removed
    /// from <see cref="DreamCore#DreamWorlds"/>.
    /// </remarks>
    /// <returns>The Bukkit <see cref="World"/> instance, or <c>null</c> if none exists.</returns>
    public World GetWorld(){
        var world = Bukkit.getWorld(worldUUID);
        if (world == null) DreamCore.DreamWorlds.remove(worldUUID);
        return world;
    }

    /// <summary>
    /// Enforces all configured locks on the world and its players.
    /// </summary>
    /// <remarks>
    /// Should be called periodically, for example in a tick loop or scheduled task,
    /// to keep world state consistent with lock settings.
    /// </remarks>
    public void TickWorld(){
        var world = GetWorld();
        if (world == null) return;
        TickTimeLock(world);
        TickDifficultyLock(world);
        for (var player : Bukkit.getOnlinePlayers()){
            if (!player.getWorld().getUID().equals(worldUUID)) continue;
            TickGameModeLock(player);
            TickHeartLock(player);
            TickHungerLock(player);
            TickSaturationLock(player);
        }
    }

    /// <summary>
    /// Applies the time lock to the given world.
    /// </summary>
    /// <param name="world">The world to lock time in.</param>
    private void TickTimeLock(World world){
        if (timeLock == null) return;
        world.setTime(timeLock.time);
    }

    /// <summary>
    /// Applies the difficulty lock to the given world.
    /// </summary>
    /// <param name="world">The world to lock difficulty in.</param>
    private void TickDifficultyLock(World world){
        if (difficultyLock == null) return;
        world.setDifficulty(difficultyLock);
    }

    /// <summary>
    /// Applies the gamemode lock to the given player.
    /// </summary>
    /// <param name="player">The player to lock gamemode for.</param>
    private void TickGameModeLock(Player player){
        if (gameModeLock == null) return;
        player.setGameMode(gameModeLock);
    }

    /// <summary>
    /// Applies the health lock to the given player.
    /// </summary>
    /// <param name="player">The player to lock health for.</param>
    private void TickHeartLock(Player player){
        if (heartLock == null) return;
        player.setHealth(heartLock);
    }

    /// <summary>
    /// Applies the hunger lock to the given player.
    /// </summary>
    /// <param name="player">The player to lock hunger for.</param>
    private void TickHungerLock(Player player){
        if (hungerLock == null) return;
        player.setFoodLevel(hungerLock);
    }

    /// <summary>
    /// Applies the saturation lock to the given player.
    /// </summary>
    /// <param name="player">The player to lock saturation for.</param>
    private void TickSaturationLock(Player player){
        if (saturationLock == null) return;
        player.setSaturation(saturationLock);
    }
}