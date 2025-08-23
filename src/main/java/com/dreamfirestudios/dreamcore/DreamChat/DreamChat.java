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
package com.dreamfirestudios.dreamcore.DreamChat;

import com.dreamfirestudios.dreamcore.DreamLuckPerms.DreamLuckPerms;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/// <summary>
/// Convenience helpers for sending formatted messages to console, players, worlds,
/// and permission‑gated audiences using <see cref="DreamMessageFormatter"/>.
/// </summary>
/// <remarks>
/// All APIs are Adventure‑first via <see cref="DreamMessageFormatter"/> and honor
/// <see cref="DreamMessageSettings"/> flags for MiniMessage, placeholders, and tag allowances.
/// </remarks>
/// <example>
/// ```java
/// DreamChat.SendMessageToPlayer(player, "<green>Hello, <name>!", DreamMessageSettings.all());
/// DreamChat.BroadcastMessage("<yellow>Server restarting soon!", DreamMessageSettings.safeChat());
/// ```
/// </example>
public final class DreamChat {

    private DreamChat() { }

    /// <summary>
    /// Formats and sends a message to the Minecraft console sender.
    /// </summary>
    /// <param name="message">Raw message (MiniMessage or plain text).</param>
    /// <param name="settings">Formatting settings; see <see cref="DreamMessageSettings"/>.</param>
    public static void SendMessageToConsole(String message, DreamMessageSettings settings){
        var cleanMessage = DreamMessageFormatter.format(message, settings);
        for (var splitMessage : SplitMessage(PlainTextComponentSerializer.plainText().serialize(cleanMessage))) {
            Bukkit.getConsoleSender().sendMessage(splitMessage);
        }
    }

    /// <summary>
    /// Formats and sends a message to a specific player.
    /// </summary>
    /// <param name="player">Recipient player.</param>
    /// <param name="message">Raw message (MiniMessage or plain text).</param>
    /// <param name="settings">Formatting settings; see <see cref="DreamMessageSettings"/>.</param>
    public static void SendMessageToPlayer(Player player, String message, DreamMessageSettings settings){
        var cleanMessage = DreamMessageFormatter.format(message, player, settings);
        for (var splitMessage : SplitMessage(PlainTextComponentSerializer.plainText().serialize(cleanMessage))) {
            player.sendMessage(splitMessage);
        }
    }

    /// <summary>
    /// Broadcasts a formatted message to all online players.
    /// </summary>
    /// <param name="message">Raw message (MiniMessage or plain text).</param>
    /// <param name="settings">Formatting settings; see <see cref="DreamMessageSettings"/>.</param>
    public static void BroadcastMessage(String message, DreamMessageSettings settings){
        for (var player : Bukkit.getOnlinePlayers()) {
            SendMessageToPlayer(player, message, settings);
        }
    }

    /// <summary>
    /// Sends a formatted message to all online players who have the given enum‑based permission.
    /// </summary>
    /// <typeparam name="T">An enum type that represents permission keys.</typeparam>
    /// <param name="message">Raw message (MiniMessage or plain text).</param>
    /// <param name="permission">Enum constant representing the permission.</param>
    /// <param name="settings">Formatting settings; see <see cref="DreamMessageSettings"/>.</param>
    /// <remarks>
    /// Uses <see cref="DreamLuckPerms"/> to resolve a user and check the permission represented by <typeparamref name="T"/>.
    /// </remarks>
    public static <T extends Enum<T>> void SendMessageToPerm(String message, T permission, DreamMessageSettings settings){
        for (var player : Bukkit.getOnlinePlayers()){
            var luckPermsUser = DreamLuckPerms.getUser(player);
            var cleanMessage = DreamMessageFormatter.format(message, player, settings);
            if (DreamLuckPerms.hasPermission(luckPermsUser, permission)) {
                SendMessageToPlayer(player, PlainTextComponentSerializer.plainText().serialize(cleanMessage), settings);
            }
        }
    }

    /// <summary>
    /// Sends a formatted message to players in a specific world by name (case‑insensitive).
    /// </summary>
    /// <param name="message">Raw message (MiniMessage or plain text).</param>
    /// <param name="worldName">World name (case‑insensitive match).</param>
    /// <param name="settings">Formatting settings; see <see cref="DreamMessageSettings"/>.</param>
    public static void SendMessageToWorld(String message, String worldName, DreamMessageSettings settings){
        for (var player : Bukkit.getOnlinePlayers()){
            var cleanMessage = DreamMessageFormatter.format(message, player, settings);
            if (player.getWorld().getName().equalsIgnoreCase(worldName)) {
                SendMessageToPlayer(player, PlainTextComponentSerializer.plainText().serialize(cleanMessage), settings);
            }
        }
    }

    /// <summary>
    /// Sends a formatted message to players in a specific world by world UUID.
    /// </summary>
    /// <param name="message">Raw message (MiniMessage or plain text).</param>
    /// <param name="worldUUID">UUID of the world.</param>
    /// <param name="settings">Formatting settings; see <see cref="DreamMessageSettings"/>.</param>
    /// <remarks>
    /// ⚠️ Current implementation uses <c>==</c> for UUID comparison (reference equality).
    /// Consider switching to <c>equals</c> for value equality.
    /// </remarks>
    public static void SendMessageToWorld(String message, UUID worldUUID, DreamMessageSettings settings){
        for (var player : Bukkit.getOnlinePlayers()){
            var cleanMessage = DreamMessageFormatter.format(message, player, settings);
            if (player.getWorld().getUID() == worldUUID) {
                SendMessageToPlayer(player, PlainTextComponentSerializer.plainText().serialize(cleanMessage), settings);
            }
        }
    }

    /// <summary>
    /// Splits a plain‑text message into lines using the <see cref="FormatTags.SplitLine"/> token.
    /// </summary>
    /// <param name="message">Plain text message.</param>
    /// <returns>List of lines after splitting.</returns>
    private static List<String> SplitMessage(String message){
        return Arrays.asList(message.split(FormatTags.SplitLine.tag));
    }
}