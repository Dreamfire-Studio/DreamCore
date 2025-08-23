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

import com.dreamfirestudios.dreamcore.DreamChat.DreamMessageFormatter;
import com.dreamfirestudios.dreamcore.DreamChat.DreamMessageSettings;
import org.bukkit.entity.Player;

import java.util.function.Function;

/// <summary>
/// Represents a single frame of action bar data, including a message provider
/// and a settings provider for formatting.
/// </summary>
/// <param name="messageProvider">
/// Function that generates the message text for a given player.
/// </param>
/// <param name="settingsProvider">
/// Function that provides <see cref="DreamMessageSettings"/> for a given player.
/// </param>
public record DreamActionBarData(Function<Player, String> messageProvider,
                                 Function<Player, DreamMessageSettings> settingsProvider) {

    /// <summary>
    /// Displays the action bar message to the specified player.
    /// </summary>
    /// <param name="player">The player who will see the action bar message.</param>
    /// <remarks>
    /// If <paramref name="player"/> is null, or if the message resolves to null,
    /// nothing will be displayed.
    /// If <see cref="DreamMessageSettings"/> is null, it defaults to <c>DreamMessageSettings.all()</c>.
    /// </remarks>
    public void displayActionBar(Player player) {
        if (player == null) return;
        String message = messageProvider.apply(player);
        if (message == null) return;
        DreamMessageSettings dreamMessageSettings = settingsProvider.apply(player);
        if (dreamMessageSettings == null) dreamMessageSettings = DreamMessageSettings.all();
        player.sendActionBar(DreamMessageFormatter.format(message, player, dreamMessageSettings));
    }
}