package com.dreamfirestudios.dreamcore.DreamMessagingChannel;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.util.logging.Level;

/**
 * Base listener for BungeeCord plugin messages.
 * <p>
 * Default channel is "BungeeCord". This class parses common subchannels:
 * - PlayerCount: server (UTF), count (int) => {@link #onPlayerCount(String, int)}
 * - Forward: sub-subchannel (UTF), inner payload (short length + bytes) => {@link #onForward(String, byte[], Player)}
 *
 * Extend and override the hooks you need.
 */
public abstract class PluginMessageLibrary implements PluginMessageListener {

    /**
     * Allows overriding the channel name (defaults to "BungeeCord").
     */
    public String getChannelName(JavaPlugin plugin) {
        return DreamPluginMessage.BUNGEE_CHANNEL;
    }

    @Override
    public final void onPluginMessageReceived(String channel, Player player, byte[] message) {
        final JavaPlugin plugin = getOwningPlugin();
        if (plugin == null) return;

        if (!getChannelName(plugin).equals(channel)) return;

        try {
            ByteArrayDataInput in = ByteStreams.newDataInput(message);
            String sub = in.readUTF(); // e.g., "PlayerCount", "Forward", etc.

            switch (sub) {
                case DreamPluginMessage.SUB_PLAYER_COUNT: {
                    String server = in.readUTF();
                    int count = in.readInt();
                    onPlayerCount(server, count);
                    break;
                }

                case DreamPluginMessage.SUB_FORWARD: {
                    // Bungee returns: subchannel (UTF) + short length + inner bytes
                    String subChannel = in.readUTF();
                    short len = in.readShort();
                    byte[] payload = new byte[len];
                    in.readFully(payload);
                    onForward(subChannel, payload, player);
                    break;
                }

                case DreamPluginMessage.SUB_FORWARD_TO_PLAYER: {
                    String subChannel = in.readUTF();
                    short len = in.readShort();
                    byte[] payload = new byte[len];
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

    /**
     * Owning plugin instance for logging/registration; you must provide it.
     */
    protected abstract JavaPlugin getOwningPlugin();

    /* ===================== Hooks to override ===================== */

    /**
     * Fired when a PlayerCount response is received.
     */
    protected void onPlayerCount(String server, int count) {
        JavaPlugin plugin = getOwningPlugin();
        if (plugin != null) {
            plugin.getLogger().info("PlayerCount for " + server + ": " + count);
        }
    }

    /**
     * Fired when a "Forward" (broadcast) payload is received.
     * If you used {@link DreamPluginMessage#forwardUtf}, you can decode here:
     * <pre>
     *   DataInputStream din = new DataInputStream(new ByteArrayInputStream(payload));
     *   String txt = din.readUTF();
     * </pre>
     */
    protected void onForward(String subChannel, byte[] payload, Player receiver) {
        // no-op default
    }

    /**
     * Fired when a "ForwardToPlayer" payload is received for this server.
     */
    protected void onForwardToPlayer(String subChannel, byte[] payload, Player receiver) {
        // no-op default
    }

    /**
     * Called for unrecognized Bungee subcommands.
     */
    protected void onUnknownSubchannel(String sub, byte[] raw, Player player) {
        JavaPlugin plugin = getOwningPlugin();
        if (plugin != null) {
            plugin.getLogger().fine("Unknown Bungee subchannel: " + sub);
        }
    }
}