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
package com.dreamfirestudios.dreamcore.DreamBossBar;

import com.dreamfirestudios.dreamcore.DreamChat.DreamMessageFormatter;
import com.dreamfirestudios.dreamcore.DreamChat.DreamMessageSettings;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

import java.util.function.Function;

/// <summary>
/// Immutable data for a single boss bar frame.
/// </summary>
public record DreamBossBarData(BarColor barColor,
                               BarStyle barStyle,
                               double barProgress,
                               Function<Player, String> messageProvider) {

    /// <summary>
    /// Applies this frame to a boss bar for a specific player.
    /// </summary>
    /// <param name="bossBar">Target boss bar.</param>
    /// <param name="player">Target player (for title formatting).</param>
    /// <exception cref="IllegalArgumentException">If <paramref name="bossBar"/> or <paramref name="player"/> is null.</exception>
    public void DisplayBarData(BossBar bossBar, Player player){
        if (bossBar == null) throw new IllegalArgumentException("BossBar cannot be null.");
        if (player == null) throw new IllegalArgumentException("Player cannot be null.");

        bossBar.setTitle(safeTitle(player));
        bossBar.setColor(barColor);
        bossBar.setStyle(barStyle);
        bossBar.setProgress(clampedProgress());
    }

    /// <summary>
    /// Returns a safe, formatted plain-text title using the message provider.
    /// </summary>
    /// <param name="player">Player used for dynamic formatting (may be null).</param>
    /// <returns>Plain-text title (never null).</returns>
    String safeTitle(Player player) {
        String raw = (messageProvider == null ? "" : messageProvider.apply(player));
        if (raw == null) raw = "";
        return PlainTextComponentSerializer.plainText()
                .serialize(DreamMessageFormatter.format(raw, player, DreamMessageSettings.all()));
    }

    /// <summary>
    /// Returns progress clamped to the range [0, 1].
    /// </summary>
    /// <returns>Clamped progress value.</returns>
    double clampedProgress() {
        return Math.max(0.0, Math.min(1.0, barProgress));
    }
}