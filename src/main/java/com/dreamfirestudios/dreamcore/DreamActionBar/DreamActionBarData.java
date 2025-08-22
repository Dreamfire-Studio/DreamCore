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