package com.dreamfirestudios.dreamCore.DreamfireTitle;

import com.dreamfirestudios.dreamCore.DreamfireChat.DreamfireMessage;
import com.dreamfirestudios.dreamCore.DreamfirePlayer.DreamfirePlayerAction;
import com.dreamfirestudios.dreamCore.DreamfirePlayer.DreamfirePlayerActionAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.time.Duration;

public class DreamfireTitle {
    public static void sendTitleToPlayer(String title, String subtitle, int fadeIn, int stay, int fadeOut){
        for(var player : Bukkit.getOnlinePlayers()) sendTitleToPlayer(player, title, subtitle, fadeIn, stay, fadeOut);
    }

    public static void sendTitleToPlayer(Player player, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        if (player == null) return;
        if (!DreamfirePlayerActionAPI.CanPlayerAction(DreamfirePlayerAction.PlayerTitle, player.getUniqueId())) return;
        Component compTitle = DreamfireMessage.formatMessage(title == null ? "" : title, player);
        Component compSub = DreamfireMessage.formatMessage(subtitle == null ? "" : subtitle, player);
        player.showTitle(Title.title(compTitle, compSub, Title.Times.times(Duration.ofSeconds(fadeIn), Duration.ofSeconds(stay), Duration.ofSeconds(fadeOut))));
    }
}
