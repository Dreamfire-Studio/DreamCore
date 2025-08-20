package com.dreamfirestudios.dreamCore.DreamfireActionBar;

import com.dreamfirestudios.dreamCore.DreamfireChat.DreamfireMessage;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.util.function.Function;

public record  DreamfireActionBarData(Function<Player, String> messageProvider) {

    /**
     * Displays the action bar for a given player.
     *
     * @param player The player for whom the action bar message will be displayed
     */
    public void displayActionBar(Player player) {
        if (player == null) return;
        String message = messageProvider.apply(player);
        if (message == null || message.isEmpty()) return;
        synchronized (this) {
            var formattedMessage = DreamfireMessage.formatMessage(message, player);
            player.sendActionBar(formattedMessage);
        }
    }
}

