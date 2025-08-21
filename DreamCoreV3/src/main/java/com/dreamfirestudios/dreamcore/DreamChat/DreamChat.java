package com.dreamfirestudios.dreamcore.DreamChat;

import com.dreamfirestudios.dreamcore.DreamLuckPerms.DreamLuckPerms;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class DreamChat {
    public static void SendMessageToConsole(String message, DreamMessageSettings settings){
        var cleanMessage = DreamMessageFormatter.format(message, settings);
        for(var splitMessage : SplitMessage(PlainTextComponentSerializer.plainText().serialize(cleanMessage))) {
            Bukkit.getConsoleSender().sendMessage(splitMessage);
        }

    }

    public static void SendMessageToPlayer(Player player, String message, DreamMessageSettings settings){
        var cleanMessage = DreamMessageFormatter.format(message, player, settings);
        for(var splitMessage : SplitMessage(PlainTextComponentSerializer.plainText().serialize(cleanMessage))) {
            player.sendMessage(splitMessage);
        }
    }

    public static void BroadcastMessage(String message, DreamMessageSettings settings){
        for(var player : Bukkit.getOnlinePlayers()) {
            SendMessageToPlayer(player, message, settings);
        }
    }

    public static <T extends Enum<T>> void SendMessageToPerm(String message, T permission, DreamMessageSettings settings){
        for(var player : Bukkit.getOnlinePlayers()){
            var luckPermsUser = DreamLuckPerms.getUser(player);
            var cleanMessage = DreamMessageFormatter.format(message, player, settings);
            if(DreamLuckPerms.hasPermission(luckPermsUser, permission)) {
                SendMessageToPlayer(player, PlainTextComponentSerializer.plainText().serialize(cleanMessage), settings);
            }
        }
    }

    public static void SendMessageToWorld(String message, String worldName, DreamMessageSettings settings){
        for(var player : Bukkit.getOnlinePlayers()){
            var cleanMessage = DreamMessageFormatter.format(message, player, settings);
            if(player.getWorld().getName().equalsIgnoreCase(worldName)) {
                SendMessageToPlayer(player, PlainTextComponentSerializer.plainText().serialize(cleanMessage), settings);
            }
        }
    }

    public static void SendMessageToWorld(String message, UUID worldUUID, DreamMessageSettings settings){
        for(var player : Bukkit.getOnlinePlayers()){
            var cleanMessage = DreamMessageFormatter.format(message, player, settings);
            if(player.getWorld().getUID() == worldUUID) {
                SendMessageToPlayer(player, PlainTextComponentSerializer.plainText().serialize(cleanMessage), settings);
            }
        }
    }

    private static List<String> SplitMessage(String message){
        return Arrays.asList(message.split(FormatTags.SplitLine.tag));
    }
}
