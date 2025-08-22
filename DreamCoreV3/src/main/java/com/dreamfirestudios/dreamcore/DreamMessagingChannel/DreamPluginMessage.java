package com.dreamfirestudios.dreamcore.DreamMessagingChannel;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;
import java.util.logging.Level;

/**
 * BungeeCord plugin-messaging helpers (Paper-ready).
 *
 * Wire formats (outer channel = "BungeeCord"):
 * - Connect:      out.writeUTF("Connect"), out.writeUTF(server)
 * - PlayerCount:  out.writeUTF("PlayerCount"), out.writeUTF(server)
 * - Forward:      out.writeUTF("Forward"), out.writeUTF(target), out.writeUTF(subchannel),
 *                 out.writeShort(inner.length), out.write(inner)
 */
public final class DreamPluginMessage {

    private DreamPluginMessage() {}

    public static final String BUNGEE_CHANNEL = "BungeeCord";

    // Bungee subcommands
    public static final String SUB_CONNECT      = "Connect";
    public static final String SUB_PLAYER_COUNT = "PlayerCount";
    public static final String SUB_FORWARD      = "Forward";
    public static final String SUB_FORWARD_TO_PLAYER = "ForwardToPlayer";

    /**
     * Register incoming/outgoing Bungee plugin channels.
     */
    public static void register(JavaPlugin plugin, PluginMessageLibrary listener) {
        Objects.requireNonNull(plugin, "plugin");
        Objects.requireNonNull(listener, "listener");
        Bukkit.getMessenger().registerOutgoingPluginChannel(plugin, BUNGEE_CHANNEL);
        Bukkit.getMessenger().registerIncomingPluginChannel(plugin, BUNGEE_CHANNEL, listener);
        plugin.getLogger().log(Level.INFO, "Registered BungeeCord plugin messaging channels.");
    }

    /**
     * Unregister all incoming/outgoing channels for this plugin.
     */
    public static void unregister(JavaPlugin plugin) {
        Objects.requireNonNull(plugin, "plugin");
        Bukkit.getMessenger().unregisterOutgoingPluginChannel(plugin);
        Bukkit.getMessenger().unregisterIncomingPluginChannel(plugin);
        plugin.getLogger().log(Level.INFO, "Unregistered all plugin messaging channels.");
    }

    /**
     * Send the player to another server on the Bungee network.
     */
    public static void connect(JavaPlugin plugin, Player player, String serverName) {
        if (!preflight(plugin, player)) return;

        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF(SUB_CONNECT);
        out.writeUTF(serverName);
        player.sendPluginMessage(plugin, BUNGEE_CHANNEL, out.toByteArray());
    }

    /**
     * Request player count for a server. Response comes back on the same channel;
     * see {@link PluginMessageLibrary#onPlayerCount(String, int)}.
     */
    public static void requestPlayerCount(JavaPlugin plugin, Player player, String serverName) {
        if (!preflight(plugin, player)) return;

        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF(SUB_PLAYER_COUNT);
        out.writeUTF(serverName);
        player.sendPluginMessage(plugin, BUNGEE_CHANNEL, out.toByteArray());
    }

    /**
     * Forward raw bytes to all players on a target server (or "ALL") on a custom subchannel.
     * Bungee requires a length-prefixed inner payload (short).
     */
    public static void forward(JavaPlugin plugin, Player player, String targetServer, String subChannel, byte[] payload) {
        if (!preflight(plugin, player)) return;

        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF(SUB_FORWARD);          // command
        out.writeUTF(targetServer);         // e.g., "ALL" or server name
        out.writeUTF(subChannel);           // your logical subchannel

        // Inner payload must be length-prefixed (short) then raw bytes
        out.writeShort(payload.length);
        out.write(payload);

        player.sendPluginMessage(plugin, BUNGEE_CHANNEL, out.toByteArray());
    }

    /**
     * Forward a single UTF-8 string as the inner payload. On the receiver, read that back as UTF.
     */
    public static void forwardUtf(JavaPlugin plugin, Player player, String targetServer, String subChannel, String message) {
        if (!preflight(plugin, player)) return;

        // Build the inner payload first (UTF string)
        ByteArrayDataOutput msg = ByteStreams.newDataOutput();
        msg.writeUTF(message == null ? "" : message);

        forward(plugin, player, targetServer, subChannel, msg.toByteArray());
    }

    /* ------------------------------------------------------------------ */

    private static boolean preflight(JavaPlugin plugin, Player player) {
        if (plugin == null) {
            Bukkit.getLogger().log(Level.SEVERE, "DreamPluginMessage: plugin is null.");
            return false;
        }
        if (player == null) {
            plugin.getLogger().log(Level.WARNING, "DreamPluginMessage: player is null.");
            return false;
        }
        if (!Bukkit.getMessenger().isOutgoingChannelRegistered(plugin, BUNGEE_CHANNEL)) {
            plugin.getLogger().log(Level.WARNING, "Outgoing channel '" + BUNGEE_CHANNEL + "' not registered for this plugin.");
            return false;
        }
        return true;
    }
}