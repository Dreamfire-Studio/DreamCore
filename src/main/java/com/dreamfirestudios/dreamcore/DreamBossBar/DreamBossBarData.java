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