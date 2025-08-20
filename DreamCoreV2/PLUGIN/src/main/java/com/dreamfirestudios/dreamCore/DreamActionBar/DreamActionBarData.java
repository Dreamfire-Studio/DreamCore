package com.dreamfirestudios.dreamCore.DreamActionBar;

import com.dreamfirestudios.dreamCore.DreamChat.DreamMessageFormatter;
import com.dreamfirestudios.dreamCore.DreamChat.DreamMessageSettings;
import org.bukkit.entity.Player;

import java.util.function.Function;

public record DreamActionBarData(Function<Player, String> messageProvider, Function<Player, DreamMessageSettings> settingsProvider) {
    public void displayActionBar(Player player) {
        if (player == null) return;
        String message = messageProvider.apply(player);
        if (message == null) return;
        DreamMessageSettings dreamMessageSettings = settingsProvider.apply(player);
        if(dreamMessageSettings == null) dreamMessageSettings = DreamMessageSettings.all();
        player.sendActionBar(DreamMessageFormatter.format(message, player, dreamMessageSettings));
    }
}
