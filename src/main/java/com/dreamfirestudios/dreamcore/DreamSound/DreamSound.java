// src/main/java/com/dreamfirestudios/dreamcore/DreamSound/DreamSound.java
package com.dreamfirestudios.dreamcore.DreamSound;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

/// <summary>
/// Convenience wrappers for playing sounds to players or in the world.
/// </summary>
/// <remarks>
/// These helpers mirror Bukkit's sound APIs while providing a compact call site.
/// </remarks>
/// <example>
/// <code>
/// DreamSound.PlaySound(Sound.UI_BUTTON_CLICK, player, player.getLocation(), 1, 1);
/// DreamSound.PlaySound(Sound.BLOCK_ANVIL_LAND, location, 1, 1);
/// </code>
/// </example>
public class DreamSound {

    /// <summary>
    /// Plays a sound only for a specific player at a location.
    /// </summary>
    /// <param name="minecraftSound">Sound enum value.</param>
    /// <param name="player">Target player.</param>
    /// <param name="location">Location where the sound originates.</param>
    /// <param name="volume">Volume (int; Bukkit will coerce).</param>
    /// <param name="pitch">Pitch (int; Bukkit will coerce).</param>
    public static void PlaySound(Sound minecraftSound, Player player, Location location, int volume, int pitch){
        player.playSound(location, minecraftSound, volume, pitch);
    }

    /// <summary>
    /// Plays a sound globally in the world at a location.
    /// </summary>
    /// <param name="minecraftSound">Sound enum value.</param>
    /// <param name="location">World location.</param>
    /// <param name="volume">Volume (int; Bukkit will coerce).</param>
    /// <param name="pitch">Pitch (int; Bukkit will coerce).</param>
    public static void PlaySound(Sound minecraftSound, Location location, int volume, int pitch){
        if(location.getWorld() == null) return;
        location.getWorld().playSound(location, minecraftSound, volume, pitch);
    }
}