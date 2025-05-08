package com.dreamfirestudios.dreamCore.DreamfireSound;

import com.dreamfirestudios.dreamCore.DreamfirePlayer.DreamfirePlayerAction;
import com.dreamfirestudios.dreamCore.DreamfirePlayer.DreamfirePlayerActionAPI;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class DreamfireSound {
    public static void PlaySound(Sound minecraftSound, Player player, Location location, int volume, int pitch){
        if(!DreamfirePlayerActionAPI.CanPlayerAction(DreamfirePlayerAction.PlayerSounds, player.getUniqueId())) return;
        player.playSound(location, minecraftSound, volume, pitch);
    }

    public static void PlaySound(Sound minecraftSound, Location location, int volume, int pitch){
        if(location.getWorld() == null) return;
        location.getWorld().playSound(location, minecraftSound, volume, pitch);
    }
}
