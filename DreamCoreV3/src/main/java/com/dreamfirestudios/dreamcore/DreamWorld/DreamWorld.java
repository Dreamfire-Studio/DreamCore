package com.dreamfirestudios.dreamcore.DreamWorld;

import com.dreamfirestudios.dreamcore.DreamCore;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DreamWorld {
    @Getter private UUID worldUUID;
    @Setter private TimeLock timeLock;
    @Setter private Difficulty difficultyLock;
    @Setter private GameMode gameModeLock;
    @Setter private Integer heartLock;
    @Setter private Integer hungerLock;
    @Setter private Integer saturationLock;

    public DreamWorld(UUID worldUUID){
        this.worldUUID = worldUUID;
    }

    public World GetWorld(){
        var world = Bukkit.getWorld(worldUUID);
        if(world == null) DreamCore.DreamWorlds.remove(worldUUID);
        return world;
    }

    public void TickWorld(){
        var world = GetWorld();
        if(world == null) return;
        TickTimeLock(world);
        TickDifficultyLock(world);
        for(var player : Bukkit.getOnlinePlayers()){
            if(player.getWorld().getUID() != worldUUID) continue;
            TickGameModeLock(player);
            TickHeartLock(player);
            TickHungerLock(player);
            TickSaturationLock(player);
        }
    }

    private void TickTimeLock(World world){
        if(timeLock == null) return;
        world.setTime(timeLock.time);
    }

    private void TickDifficultyLock(World world){
        if(difficultyLock == null) return;
        world.setDifficulty(difficultyLock);
    }

    private void TickGameModeLock(Player player){
        if(gameModeLock == null) return;
        player.setGameMode(gameModeLock);
    }

    private void TickHeartLock(Player player){
        if(heartLock == null) return;
        player.setHealth(heartLock);
    }

    private void TickHungerLock(Player player){
        if(hungerLock == null) return;
        player.setFoodLevel(hungerLock);
    }

    private void TickSaturationLock(Player player){
        if(saturationLock == null) return;
        player.setSaturation(saturationLock);
    }
}