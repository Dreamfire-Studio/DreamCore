/*
 * MIT License
 *
 * Copyright (c) 2025 Dreamfire Studio
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.dreamfirestudios.dreamcore.DreamMessagingChannel;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;
import java.util.logging.Level;

/// <summary>
/// Utility class for handling BungeeCord plugin messaging channels in Paper/Spigot.
/// </summary>
/// <remarks>
/// This class abstracts common BungeeCord messaging subcommands, such as:
/// <list type="bullet">
///   <item><description>Connect → Moves a player to another server</description></item>
///   <item><description>PlayerCount → Requests player count of a server</description></item>
///   <item><description>Forward / ForwardToPlayer → Forwards payloads to other servers or specific players</description></item>
/// </list>
/// </remarks>
public final class DreamPluginMessage {

    private DreamPluginMessage() {}

    /// <summary>Outer Bungee channel name (constant).</summary>
    public static final String BUNGEE_CHANNEL = "BungeeCord";

    // Subcommands
    public static final String SUB_CONNECT            = "Connect";
    public static final String SUB_PLAYER_COUNT       = "PlayerCount";
    public static final String SUB_FORWARD            = "Forward";
    public static final String SUB_FORWARD_TO_PLAYER  = "ForwardToPlayer";

    /// <summary>
    /// Registers this plugin to listen and send on the BungeeCord channel.
    /// </summary>
    /// <param name="plugin">Owning JavaPlugin instance.</param>
    /// <param name="listener">PluginMessageLibrary listener implementation.</param>
    /// <example>
    /// <code>
    /// DreamPluginMessage.register(this, new MyMessageListener());
    /// </code>
    /// </example>
    public static void register(JavaPlugin plugin, PluginMessageLibrary listener) {
        Objects.requireNonNull(plugin, "plugin");
        Objects.requireNonNull(listener, "listener");
        Bukkit.getMessenger().registerOutgoingPluginChannel(plugin, BUNGEE_CHANNEL);
        Bukkit.getMessenger().registerIncomingPluginChannel(plugin, BUNGEE_CHANNEL, listener);
        plugin.getLogger().log(Level.INFO, "Registered BungeeCord plugin messaging channels.");
    }

    /// <summary>
    /// Unregisters all plugin messaging channels for this plugin.
    /// </summary>
    /// <param name="plugin">Owning plugin instance.</param>
    public static void unregister(JavaPlugin plugin) {
        Objects.requireNonNull(plugin, "plugin");
        Bukkit.getMessenger().unregisterOutgoingPluginChannel(plugin);
        Bukkit.getMessenger().unregisterIncomingPluginChannel(plugin);
        plugin.getLogger().log(Level.INFO, "Unregistered all plugin messaging channels.");
    }

    /// <summary>
    /// Connects a player to another BungeeCord server.
    /// </summary>
    /// <param name="plugin">Owning plugin instance.</param>
    /// <param name="player">Player to transfer.</param>
    /// <param name="serverName">Target server name.</param>
    /// <example>
    /// <code>
    /// DreamPluginMessage.connect(this, player, "Hub");
    /// </code>
    /// </example>
    public static void connect(JavaPlugin plugin, Player player, String serverName) {
        if (!preflight(plugin, player)) return;
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF(SUB_CONNECT);
        out.writeUTF(serverName);
        player.sendPluginMessage(plugin, BUNGEE_CHANNEL, out.toByteArray());
    }

    /// <summary>
    /// Requests player count of a target server.
    /// </summary>
    /// <remarks>
    /// The response will be handled in <see cref="PluginMessageLibrary.onPlayerCount(String, int)"/>.
    /// </remarks>
    /// <param name="plugin">Owning plugin instance.</param>
    /// <param name="player">Player proxy to send the request through.</param>
    /// <param name="serverName">Target server.</param>
    public static void requestPlayerCount(JavaPlugin plugin, Player player, String serverName) {
        if (!preflight(plugin, player)) return;
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF(SUB_PLAYER_COUNT);
        out.writeUTF(serverName);
        player.sendPluginMessage(plugin, BUNGEE_CHANNEL, out.toByteArray());
    }

    /// <summary>
    /// Forwards raw payload to another server or ALL servers.
    /// </summary>
    /// <param name="plugin">Owning plugin.</param>
    /// <param name="player">Player proxy to send through.</param>
    /// <param name="targetServer">Target server ("ALL" for all servers).</param>
    /// <param name="subChannel">Custom logical subchannel name.</param>
    /// <param name="payload">Binary payload (inner length-prefixed).</param>
    public static void forward(JavaPlugin plugin, Player player, String targetServer, String subChannel, byte[] payload) {
        if (!preflight(plugin, player)) return;
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF(SUB_FORWARD);
        out.writeUTF(targetServer);
        out.writeUTF(subChannel);
        out.writeShort(payload.length);
        out.write(payload);
        player.sendPluginMessage(plugin, BUNGEE_CHANNEL, out.toByteArray());
    }

    /// <summary>
    /// Forwards a single UTF string to another server.
    /// </summary>
    /// <param name="plugin">Owning plugin.</param>
    /// <param name="player">Player proxy.</param>
    /// <param name="targetServer">Target server.</param>
    /// <param name="subChannel">Custom subchannel.</param>
    /// <param name="message">Message to forward.</param>
    /// <example>
    /// <code>
    /// DreamPluginMessage.forwardUtf(this, player, "ALL", "Chat", "Hello!");
    /// </code>
    /// </example>
    public static void forwardUtf(JavaPlugin plugin, Player player, String targetServer, String subChannel, String message) {
        if (!preflight(plugin, player)) return;
        ByteArrayDataOutput msg = ByteStreams.newDataOutput();
        msg.writeUTF(message == null ? "" : message);
        forward(plugin, player, targetServer, subChannel, msg.toByteArray());
    }

    /// <summary>
    /// Validates state before sending a plugin message.
    /// </summary>
    /// <param name="plugin">Owning plugin instance.</param>
    /// <param name="player">Player proxy.</param>
    /// <returns>True if messaging is possible, false otherwise.</returns>
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