package com.dreamfirestudios.dreamcore.DreamTitle;

import com.dreamfirestudios.dreamcore.DreamChat.DreamMessageFormatter;
import com.dreamfirestudios.dreamcore.DreamChat.DreamMessageSettings;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.time.Duration;

public class DreamTitle {
    public static void sendTitleToPlayer(String title, String subtitle, int fadeIn, int stay, int fadeOut){
        for(var player : Bukkit.getOnlinePlayers()) sendTitleToPlayer(player, title, subtitle, fadeIn, stay, fadeOut);
    }

    public static void sendTitleToPlayer(Player player, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        if (player == null) return;
        Component compTitle = DreamMessageFormatter.format(title == null ? "" : title, player, DreamMessageSettings.all());
        Component compSub = DreamMessageFormatter.format(subtitle == null ? "" : subtitle, player, DreamMessageSettings.all());
        player.showTitle(Title.title(compTitle, compSub, Title.Times.times(Duration.ofSeconds(fadeIn), Duration.ofSeconds(stay), Duration.ofSeconds(fadeOut))));
    }
}
