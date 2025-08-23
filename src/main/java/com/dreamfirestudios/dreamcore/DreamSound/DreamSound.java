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