package com.dreamfirestudios.dreamCore.DreamfireChat;

import com.dreamfirestudios.dreamCore.DreamfireLuckPerms.DreamfireLuckPerms;
import com.dreamfirestudios.dreamCore.DreamfireLuckPerms.DreamfirePermissions;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;

public class DreamfireMessage {

    public static Component formatMessage(String message, Player player, char colorChar) {
        boolean allowLegacy = player == null || DreamfireLuckPerms.hasPermission(DreamfireLuckPerms.getUser(player), DreamfirePermissions.translateColorCodes);
        boolean allowHex = player == null || DreamfireLuckPerms.hasPermission(DreamfireLuckPerms.getUser(player), DreamfirePermissions.translateHexCodes);
        String afterPapi = (player != null ? PlaceholderAPI.setPlaceholders(player, message) : message);
        LegacyComponentSerializer serializer = allowHex ? LegacyComponentSerializer.builder().character(colorChar).hexColors().build() : LegacyComponentSerializer.builder().character(colorChar).build();
        return allowLegacy ? serializer.deserialize(afterPapi) : Component.text(afterPapi);
    }

    public static Component formatMessage(String message, Player player) {
        return formatMessage(message, player, '&');
    }

    public static Component formatMessage(String message, char colorChar) {
        return formatMessage(message, null, colorChar);
    }

    public static Component formatMessage(String message) {
        return formatMessage(message, null, '&');
    }

    public static String toLegacyString(Component comp) {
        return LegacyComponentSerializer.legacySection().serialize(comp);
    }

    public static String centerMessage(String message, int stringLength) {
        if (message == null) return null;
        stringLength = Math.max(message.length(), stringLength);
        int totalPadding = stringLength - message.length();
        int leftPadding  = totalPadding / 2;
        int rightPadding = totalPadding - leftPadding;
        return " ".repeat(leftPadding) + message + " ".repeat(rightPadding);
    }

    public static String limitMessage(String message, int messageSize) {
        if (message == null || messageSize <= 0) {
            return "...";
        }
        if (message.length() <= messageSize) {
            return message;
        }
        return message.substring(0, messageSize) + "...";
    }

    public static String capitalizeWords(String message) {
        if (message == null || message.isEmpty()) return message;
        String[] words = message.split(" ");
        StringBuilder sb = new StringBuilder();
        for (String word : words) {
            if (word.isEmpty()) continue;
            sb.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1).toLowerCase()).append(' ');
        }
        return sb.toString().trim();
    }

    public static String reverseMessage(String message) {
        if (message == null) return null;
        return new StringBuilder(message).reverse().toString();
    }
}