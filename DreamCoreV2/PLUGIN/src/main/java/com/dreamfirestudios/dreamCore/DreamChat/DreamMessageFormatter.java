package com.dreamfirestudios.dreamCore.DreamChat;

import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Locale;
import java.util.Objects;
import java.util.regex.Pattern;

public class DreamMessageFormatter {
    private static final MiniMessage MM = MiniMessage.miniMessage();

    private static final Pattern COLOR_TAGS = Pattern.compile("</?#[0-9a-fA-F]{6}>|</?color(?:\\s*:\\s*#[0-9a-fA-F]{6})?>|</?gradient(?:\\s*:[^>]+)?>|</?rainbow(?:\\s*:[^>]+)?>");
    private static final Pattern FORMAT_TAGS = Pattern.compile("</?(bold|b|italic|i|underlined|u|strikethrough|st|obfuscated|obf)>");
    private static final Pattern ACTION_TAGS = Pattern.compile("</?click(?:\\s*:[^>]+)?>|</?hover(?:\\s*:[^>]+)?>");

    public static Component format(String message, DreamMessageSettings settings) {
        return format(message, null, settings);
    }

    public static Component format(String message, Player player, DreamMessageSettings settings) {
        if (message == null) return Component.empty();
        settings = Objects.requireNonNullElse(settings, DreamMessageSettings.all());
        String processed = (settings.usePlaceholders() && player != null && isPapiAvailable()) ? PlaceholderAPI.setPlaceholders(player, message) : message;
        if (!settings.allowMiniMessage()) {
            return Component.text(processed);
        }
        processed = sanitize(processed, settings);
        return MM.deserialize(processed);
    }

    public static Component format(String message, Player player,DreamMessageSettings settings, TagResolver... resolvers) {
        if (message == null) return Component.empty();
        settings = Objects.requireNonNullElse(settings, DreamMessageSettings.all());
        String processed = (settings.usePlaceholders() && player != null && isPapiAvailable()) ? PlaceholderAPI.setPlaceholders(player, message) : message;
        if (!settings.allowMiniMessage()) {
            return Component.text(processed);
        }
        processed = sanitize(processed, settings);
        TagResolver resolver = (resolvers != null && resolvers.length > 0)
                ? TagResolver.resolver(resolvers)
                : TagResolver.empty();
        return MM.deserialize(processed, resolver);
    }

    public static String centerMessage(String message, int stringLength) {
        if (message == null) return null;
        int width = Math.max(stringLength, message.length());
        int pad = width - message.length();
        int left = pad / 2;
        int right = pad - left;
        return " ".repeat(left) + message + " ".repeat(right);
    }

    public static String limitMessage(String message, int maxLen) {
        if (message == null || maxLen <= 0) return "...";
        if (message.length() <= maxLen) return message;
        return message.substring(0, maxLen) + "...";
    }

    public static String capitalizeWords(String message) {
        if (message == null || message.isEmpty()) return message;
        String[] words = message.split(" ");
        StringBuilder sb = new StringBuilder(message.length());
        for (String w : words) {
            if (w.isEmpty()) continue;
            sb.append(Character.toUpperCase(w.charAt(0)))
                    .append(w.substring(1).toLowerCase(Locale.ROOT))
                    .append(' ');
        }
        return sb.toString().trim();
    }

    public static String reverseMessage(String message) {
        if (message == null) return null;
        return new StringBuilder(message).reverse().toString();
    }

    public static TagResolver placeholder(String key, String value) {
        return Placeholder.unparsed(key, value == null ? "" : value);
    }

    private static boolean isPapiAvailable() {
        return Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI");
    }

    private static String sanitize(String input, DreamMessageSettings s) {
        String out = input;
        if (!s.allowColors()) {
            out = COLOR_TAGS.matcher(out).replaceAll("");
        }
        if (!s.allowFormatting()) {
            out = FORMAT_TAGS.matcher(out).replaceAll("");
        }
        if (!s.allowClickAndHover()) {
            out = ACTION_TAGS.matcher(out).replaceAll("");
        }
        return out;
    }
}
