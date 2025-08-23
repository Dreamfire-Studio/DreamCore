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