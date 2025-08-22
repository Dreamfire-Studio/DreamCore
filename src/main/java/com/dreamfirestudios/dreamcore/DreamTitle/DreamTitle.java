package com.dreamfirestudios.dreamcore.DreamTitle;

import com.dreamfirestudios.dreamcore.DreamChat.DreamMessageFormatter;
import com.dreamfirestudios.dreamcore.DreamChat.DreamMessageSettings;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.time.Duration;

/// <summary>
/// Convenience helpers for sending Adventure titles to players.
/// </summary>
/// <remarks>
/// Uses <see cref="DreamMessageFormatter"/> to format MiniMessage input into components.
/// Durations are specified in seconds and converted to <see cref="Duration"/>.
/// </remarks>
public class DreamTitle {

    /// <summary>
    /// Sends a title to all online players.
    /// </summary>
    /// <param name="title">MiniMessage for the title line (null → empty).</param>
    /// <param name="subtitle">MiniMessage for the subtitle (null → empty).</param>
    /// <param name="fadeIn">Fade-in seconds.</param>
    /// <param name="stay">Stay seconds.</param>
    /// <param name="fadeOut">Fade-out seconds.</param>
    /// <example>
    /// <code>
    /// DreamTitle.sendTitleToPlayer("&lt;gold&gt;Welcome!", "&lt;gray&gt;Have fun", 1, 3, 1);
    /// </code>
    /// </example>
    public static void sendTitleToPlayer(String title, String subtitle, int fadeIn, int stay, int fadeOut){
        for (var player : Bukkit.getOnlinePlayers())
            sendTitleToPlayer(player, title, subtitle, fadeIn, stay, fadeOut);
    }

    /// <summary>
    /// Sends a title to a single player.
    /// </summary>
    /// <param name="player">Target player (ignored if null).</param>
    /// <param name="title">MiniMessage for the title line (null → empty).</param>
    /// <param name="subtitle">MiniMessage for the subtitle (null → empty).</param>
    /// <param name="fadeIn">Fade-in seconds.</param>
    /// <param name="stay">Stay seconds.</param>
    /// <param name="fadeOut">Fade-out seconds.</param>
    /// <example>
    /// <code>
    /// DreamTitle.sendTitleToPlayer(player, "&lt;green&gt;Level Up!", "", 1, 2, 1);
    /// </code>
    /// </example>
    public static void sendTitleToPlayer(Player player, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        if (player == null) return;
        Component compTitle = DreamMessageFormatter.format(title == null ? "" : title, player, DreamMessageSettings.all());
        Component compSub = DreamMessageFormatter.format(subtitle == null ? "" : subtitle, player, DreamMessageSettings.all());
        player.showTitle(Title.title(
                compTitle,
                compSub,
                Title.Times.times(Duration.ofSeconds(fadeIn), Duration.ofSeconds(stay), Duration.ofSeconds(fadeOut))
        ));
    }
}