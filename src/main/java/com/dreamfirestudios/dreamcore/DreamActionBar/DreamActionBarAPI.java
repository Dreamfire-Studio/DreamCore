package com.dreamfirestudios.dreamcore.DreamActionBar;

import com.dreamfirestudios.dreamcore.DreamCore;
import org.bukkit.entity.Player;

/// <summary>
/// Provides utility methods for querying active <see cref="DreamActionBar"/> instances.
/// </summary>
public class DreamActionBarAPI {

    /// <summary>
    /// Checks if a given player is currently viewing any <see cref="DreamActionBar"/>.
    /// </summary>
    /// <param name="player">The player to check.</param>
    /// <returns>
    /// <c>true</c> if the player is currently in at least one action bar;
    /// otherwise, <c>false</c>.
    /// </returns>
    /// <exception cref="IllegalArgumentException">
    /// Thrown if <paramref name="player"/> is <c>null</c>.
    /// </exception>
    public static boolean IsPlayerInActionBar(Player player) {
        if (player == null) throw new IllegalArgumentException("Player cannot be null");
        for (var dreamActionBar : DreamCore.DreamActionBars.values()) {
            if (dreamActionBar.isPlayerViewing(player)) {
                return true;
            }
        }
        return false;
    }
}