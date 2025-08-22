/*  Copyright (c) Dreamfire Studios
 *  This file is part of DreamfireV2 (industry-level code quality initiative).
 *  Style: DocFX-friendly XML docs, consistent with ChaosGalaxyTCG headers.
 */

package com.dreamfirestudios.dreamcore.DreamMessagingChannel;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.util.logging.Level;

/// <summary>
/// Base listener for BungeeCord plugin messages (channel: <c>"BungeeCord"</c>).
/// </summary>
/// <remarks>
/// Parses common subchannels and exposes overridable hooks:
/// <list type="bullet">
///   <item><description><c>PlayerCount</c> → <see cref="onPlayerCount(String, int)"/></description></item>
///   <item><description><c>Forward</c> → <see cref="onForward(String, byte[], Player)"/></description></item>
///   <item><description><c>ForwardToPlayer</c> → <see cref="onForwardToPlayer(String, byte[], Player)"/></description></item>
/// </list>
/// To activate, register via <see cref="DreamPluginMessage.register(JavaPlugin, PluginMessageLibrary)"/>.
/// </remarks>
public abstract class PluginMessageLibrary implements PluginMessageListener {

    /// <summary>
    /// Returns the plugin messaging channel name to listen on.
    /// </summary>
    /// <param name="plugin">Owning plugin.</param>
    /// <returns>Channel identifier. Defaults to <c>"BungeeCord"</c>.</returns>
    public String getChannelName(JavaPlugin plugin) {
        return DreamPluginMessage.BUNGEE_CHANNEL;
    }

    /// <summary>
    /// Internal dispatcher for plugin messages; do not override. Override the hook methods instead.
    /// </summary>
    /// <param name="channel">Incoming channel name.</param>
    /// <param name="player">Player through which the message arrived.</param>
    /// <param name="message">Raw message bytes.</param>
    /// <remarks>
    /// Handles subcommands:
    /// <list type="bullet">
    ///   <item><description><c>PlayerCount</c> → Reads server (UTF) then count (int).</description></item>
    ///   <item><description><c>Forward</c>/<c>ForwardToPlayer</c> → Reads subchannel (UTF), length (short), payload (bytes).</description></item>
    /// </list>
    /// </remarks>
    @Override
    public final void onPluginMessageReceived(String channel, Player player, byte[] message) {
        final JavaPlugin plugin = getOwningPlugin();
        if (plugin == null) return;

        if (!getChannelName(plugin).equals(channel)) return;

        try {
            final ByteArrayDataInput in = ByteStreams.newDataInput(message);
            final String sub = in.readUTF(); // e.g., "PlayerCount", "Forward", etc.

            switch (sub) {
                case DreamPluginMessage.SUB_PLAYER_COUNT: {
                    final String server = in.readUTF();
                    final int count = in.readInt();
                    onPlayerCount(server, count);
                    break;
                }

                case DreamPluginMessage.SUB_FORWARD: {
                    final String subChannel = in.readUTF();
                    final short len = in.readShort();
                    final byte[] payload = new byte[len];
                    in.readFully(payload);
                    onForward(subChannel, payload, player);
                    break;
                }

                case DreamPluginMessage.SUB_FORWARD_TO_PLAYER: {
                    final String subChannel = in.readUTF();
                    final short len = in.readShort();
                    final byte[] payload = new byte[len];
                    in.readFully(payload);
                    onForwardToPlayer(subChannel, payload, player);
                    break;
                }

                default:
                    onUnknownSubchannel(sub, message, player);
                    break;
            }
        } catch (Throwable t) {
            plugin.getLogger().log(Level.SEVERE, "Error handling plugin message on " + channel, t);
        }
    }

    /// <summary>
    /// Provide the owning plugin instance for logging and registration.
    /// </summary>
    /// <returns>Owning <see cref="JavaPlugin"/> instance.</returns>
    protected abstract JavaPlugin getOwningPlugin();

    /* ===================== Overridable hooks ===================== */

    /// <summary>
    /// Called when <c>PlayerCount</c> response is received.
    /// </summary>
    /// <param name="server">Server name the count refers to.</param>
    /// <param name="count">Current online player count.</param>
    /// <example>
    /// <code>
    /// @Override
    /// protected void onPlayerCount(String server, int count) {
    ///     getOwningPlugin().getLogger().info(server + " has " + count + " players.");
    /// }
    /// </code>
    /// </example>
    protected void onPlayerCount(String server, int count) {
        JavaPlugin plugin = getOwningPlugin();
        if (plugin != null) {
            plugin.getLogger().info("PlayerCount for " + server + ": " + count);
        }
    }

    /// <summary>
    /// Called when a broadcast <c>Forward</c> payload is received.
    /// </summary>
    /// <param name="subChannel">Logical subchannel name.</param>
    /// <param name="payload">Raw payload bytes (length-prefixed in frame).</param>
    /// <param name="receiver">Receiving player (transport).</param>
    /// <remarks>
    /// If you sent with <see cref="DreamPluginMessage.forwardUtf(JavaPlugin, org.bukkit.entity.Player, String, String, String)"/>,
    /// decode via:
    /// <code>
    /// try (DataInputStream din = new DataInputStream(new ByteArrayInputStream(payload))) {
    ///     String txt = din.readUTF();
    /// }
    /// </code>
    /// </remarks>
    protected void onForward(String subChannel, byte[] payload, Player receiver) {
        // no-op default
    }

    /// <summary>
    /// Called when a targeted <c>ForwardToPlayer</c> payload is received.
    /// </summary>
    /// <param name="subChannel">Logical subchannel.</param>
    /// <param name="payload">Raw payload bytes.</param>
    /// <param name="receiver">Receiving player.</param>
    protected void onForwardToPlayer(String subChannel, byte[] payload, Player receiver) {
        // no-op default
    }

    /// <summary>
    /// Called when an unknown subcommand is encountered.
    /// </summary>
    /// <param name="sub">Unknown subcommand.</param>
    /// <param name="raw">Raw bytes of the full message (framed by Bungee).</param>
    /// <param name="player">Transport player.</param>
    protected void onUnknownSubchannel(String sub, byte[] raw, Player player) {
        JavaPlugin plugin = getOwningPlugin();
        if (plugin != null) {
            plugin.getLogger().fine("Unknown Bungee subchannel: " + sub);
        }
    }
}