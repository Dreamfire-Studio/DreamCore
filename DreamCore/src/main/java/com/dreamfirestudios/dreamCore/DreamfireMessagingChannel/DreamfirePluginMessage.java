package com.dreamfirestudios.dreamCore.DreamfireMessagingChannel;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.logging.Level;

public class DreamfirePluginMessage {
    public static final String BungeeCordChannel = "BungeeCord";
    public static final String PlayerCountChannel = "PlayerCount";
    public static final String PlayerMessageChannel = "MessageChannel";

    public static void TerminateConnections(JavaPlugin javaPlugin){
        javaPlugin.getServer().getMessenger().unregisterOutgoingPluginChannel(javaPlugin);
        javaPlugin.getServer().getMessenger().unregisterIncomingPluginChannel(javaPlugin);
    }

    public static void ConnectToSubServer(JavaPlugin javaPlugin, Player player, String serverName, String channelName){
        var out = ByteStreams.newDataOutput();
        out.writeUTF("Connect");
        out.writeUTF(serverName);
        player.sendPluginMessage(javaPlugin, channelName, out.toByteArray());
    }

    public static void RequestSendMessage(JavaPlugin javaPlugin, Player player, String message, String serverName, String channelName) {
        if (player == null) {
            Bukkit.getConsoleSender().sendMessage("Player is null; cannot send request.");
            return;
        }

        if (!Bukkit.getMessenger().isOutgoingChannelRegistered(javaPlugin, channelName)) {
            Bukkit.getConsoleSender().sendMessage("Outgoing channel '" + channelName + "' is not registered.");
            return;
        }

        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Forward");  // Command for forwarding.
        out.writeUTF(serverName); // Target server (e.g., "ALL" or a specific server name).
        out.writeUTF(DreamfirePluginMessage.PlayerMessageChannel); // Subchannel name.
        out.writeUTF(message);    // Actual message content.

        Bukkit.getConsoleSender().sendMessage(String.format(
                "Sending message: '%s' to server: '%s' on channel: '%s'",
                message, serverName, channelName
        ));

        Bukkit.getConsoleSender().sendMessage("Message payload: " + Arrays.toString(out.toByteArray()));
        Bukkit.getConsoleSender().sendMessage("Outgoing channels: " + Bukkit.getMessenger().getOutgoingChannels(javaPlugin));
        player.sendPluginMessage(javaPlugin, channelName, out.toByteArray());
    }


    public static void RequestPlayerCount(JavaPlugin javaPlugin, Player player, String serverName, String channelName) {
        if (player == null){
            Bukkit.getConsoleSender().sendMessage("Player is null; cannot send player count request for server: " + serverName);
            return;
        }
        if (!Bukkit.getMessenger().isOutgoingChannelRegistered(javaPlugin, channelName)){
            Bukkit.getConsoleSender().sendMessage("Outgoing channel '" + channelName + "' is not registered. Cannot send request for server: " + serverName);
            return;
        }
        var out = ByteStreams.newDataOutput();
        try {
            out.writeUTF(PlayerCountChannel);
            out.writeUTF(serverName);
            player.sendPluginMessage(javaPlugin, channelName, out.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
