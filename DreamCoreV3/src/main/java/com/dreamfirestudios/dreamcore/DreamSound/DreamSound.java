package com.dreamfirestudios.dreamcore.DreamSound;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class DreamSound {
    public static void PlaySound(Sound minecraftSound, Player player, Location location, int volume, int pitch){
        player.playSound(location, minecraftSound, volume, pitch);
    }

    public static void PlaySound(Sound minecraftSound, Location location, int volume, int pitch){
        if(location.getWorld() == null) return;
        location.getWorld().playSound(location, minecraftSound, volume, pitch);
    }
}

