package com.dreamfirestudios.dreamcore.DreamChat;

import com.dreamfirestudios.dreamcore.DreamLuckPerms.DreamLuckPerms;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Facade for sending formatted messages to console and players.
 *
 * <p>All methods build Adventure {@link Component}s via {@link DreamMessageFormatter}
 * and send those components directly.</p>
 */
public final class DreamChat {
    private DreamChat() {}

    /** Send to console. */
    public static void SendMessageToConsole(@NotNull String message, @NotNull DreamMessageSettings settings){
        for (var part : split(message)) {
            Component comp = DreamMessageFormatter.format(part, settings);
            Bukkit.getConsoleSender().sendMessage(comp);
        }
    }

    /** Send to a single player (PAPI expands with this player). */
    public static void SendMessageToPlayer(@NotNull Player player,
                                           @NotNull String message,
                                           @NotNull DreamMessageSettings settings){
        for (var part : split(message)) {
            Component comp = DreamMessageFormatter.format(part, player, settings);
            player.sendMessage(comp);
        }
    }

    /** Broadcast to all players. */
    public static void BroadcastMessage(@NotNull String message, @NotNull DreamMessageSettings settings){
        for (var p : Bukkit.getOnlinePlayers()) SendMessageToPlayer(p, message, settings);
    }

    /** Send to players with a permission (enum name used as the node). */
    public static <T extends Enum<T>> void SendMessageToPerm(@NotNull String message,
                                                             @NotNull T permission,
                                                             @NotNull DreamMessageSettings settings){
        for (var p : Bukkit.getOnlinePlayers()){
            var user = DreamLuckPerms.getUser(p);
            if (DreamLuckPerms.hasPermission(user, permission.toString())) {
                SendMessageToPlayer(p, message, settings);
            }
        }
    }

    /** Send to everyone in a world by name (case-insensitive). */
    public static void SendMessageToWorld(@NotNull String message,
                                          @NotNull String worldName,
                                          @NotNull DreamMessageSettings settings){
        for (var p : Bukkit.getOnlinePlayers()){
            if (p.getWorld().getName().equalsIgnoreCase(worldName)) {
                SendMessageToPlayer(p, message, settings);
            }
        }
    }

    /** Send to everyone in a world by UUID. */
    public static void SendMessageToWorld(@NotNull String message,
                                          @NotNull UUID worldUUID,
                                          @NotNull DreamMessageSettings settings){
        for (var p : Bukkit.getOnlinePlayers()){
            if (p.getWorld().getUID().equals(worldUUID)) {
                SendMessageToPlayer(p, message, settings);
            }
        }
    }

    private static @NotNull List<String> split(@NotNull String message){
        return Arrays.asList(message.split(FormatTags.SplitLine.tag));
    }
}