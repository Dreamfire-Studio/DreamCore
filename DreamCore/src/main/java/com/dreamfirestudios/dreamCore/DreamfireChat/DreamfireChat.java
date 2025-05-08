package com.dreamfirestudios.dreamCore.DreamfireChat;

import com.dreamfirestudios.dreamCore.DreamfireLuckPerms.DreamfireLuckPerms;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class DreamfireChat {
    public static void SendMessageToConsole(String message){
        var cleanMessage = DreamfireMessage.formatMessage(message);
        for(var splitMessage : SplitMessage(PlainTextComponentSerializer.plainText().serialize(cleanMessage))) Bukkit.getConsoleSender().sendMessage(splitMessage);

    }

    public static void SendMessageToPlayer(Player player, String message){
        var cleanMessage = DreamfireMessage.formatMessage(message, player);
        for(var splitMessage : SplitMessage(PlainTextComponentSerializer.plainText().serialize(cleanMessage))) player.sendMessage(splitMessage);
    }

    public static void BroadcastMessage(String message){
        for(var player : Bukkit.getOnlinePlayers()) SendMessageToPlayer(player, message);
    }

    public static void SendMessageToPerm(String message, String permission){
        for(var player : Bukkit.getOnlinePlayers()){
            var luckPermsUser = DreamfireLuckPerms.getUser(player);
            var cleanMessage = DreamfireMessage.formatMessage(message, player);
            if(DreamfireLuckPerms.hasPermission(luckPermsUser, permission)) SendMessageToPlayer(player, PlainTextComponentSerializer.plainText().serialize(cleanMessage));
        }
    }

    public static void SendMessageToWorld(String message, String worldName){
        for(var player : Bukkit.getOnlinePlayers()){
            var cleanMessage = DreamfireMessage.formatMessage(message, player);
            if(player.getWorld().getName().equalsIgnoreCase(worldName)) SendMessageToPlayer(player, PlainTextComponentSerializer.plainText().serialize(cleanMessage));
        }
    }

    public static void SendMessageToWorld(String message, UUID worldUUID){
        for(var player : Bukkit.getOnlinePlayers()){
            var cleanMessage = DreamfireMessage.formatMessage(message, player);
            if(player.getWorld().getUID() == worldUUID) SendMessageToPlayer(player, PlainTextComponentSerializer.plainText().serialize(cleanMessage));
        }
    }

    private static List<String> SplitMessage(String message){
        return Arrays.asList(message.split(FormatTags.SplitLine.tag));
    }
}
