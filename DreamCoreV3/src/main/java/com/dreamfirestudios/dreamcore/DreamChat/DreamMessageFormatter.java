package com.dreamfirestudios.dreamcore.DreamChat;

import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Adventure-first message formatter that supports:
 * <ul>
 *   <li>MiniMessage parsing (configurable via {@link DreamMessageSettings})</li>
 *   <li>PlaceholderAPI expansion when a {@link Player} is supplied</li>
 *   <li>Both String and Component inputs/outputs</li>
 * </ul>
 *
 * Notes:
 * <p>When formatting a {@link Component} and placeholders are enabled, we serialize to MiniMessage,
 * apply PlaceholderAPI (if present), sanitize tags according to settings, and deserialize back.</p>
 */
public final class DreamMessageFormatter {

    private DreamMessageFormatter() {}

    private static final MiniMessage MM = MiniMessage.miniMessage();

    // Basic scrubbers for when MiniMessage is disabled in settings
    private static final Pattern COLOR_TAGS = Pattern.compile(
            "</?#[0-9a-fA-F]{6}>|</?color(?:\\s*:\\s*#[0-9a-fA-F]{6})?>|</?gradient(?:\\s*:[^>]+)?>|</?rainbow(?:\\s*:[^>]+)?>"
    );
    private static final Pattern FORMAT_TAGS = Pattern.compile(
            "</?(bold|b|italic|i|underlined|u|strikethrough|st|obfuscated|obf)>"
    );
    private static final Pattern ACTION_TAGS = Pattern.compile(
            "</?click(?:\\s*:[^>]+)?>|</?hover(?:\\s*:[^>]+)?>"
    );

    // ---------------------------------------------------------------------
    // Public API — String -> Component
    // ---------------------------------------------------------------------

    /**
     * Formats a raw string to an Adventure Component using settings.
     *
     * @param message  raw message (MiniMessage or plain)
     * @param settings formatting controls (null uses {@code DreamMessageSettings.all()})
     * @return formatted Component (never null)
     */
    public static @NotNull Component format(@Nullable String message,
                                            @Nullable DreamMessageSettings settings) {
        return format(message, null, settings);
    }

    /**
     * Formats a raw string with optional PlaceholderAPI expansion for the given player.
     *
     * @param message  raw message (MiniMessage or plain)
     * @param player   player context for PAPI (optional)
     * @param settings formatting controls (null uses {@code DreamMessageSettings.all()})
     * @param resolvers optional MiniMessage tag resolvers
     * @return formatted Component (never null)
     */
    public static @NotNull Component format(@Nullable String message,
                                            @Nullable Player player,
                                            @Nullable DreamMessageSettings settings,
                                            TagResolver... resolvers) {
        if (message == null) return Component.empty();
        final DreamMessageSettings s = nonNull(settings);

        String processed = message;
        if (s.usePlaceholders() && player != null && isPapiAvailable()) {
            processed = PlaceholderAPI.setPlaceholders(player, processed);
        }

        if (!s.allowMiniMessage()) {
            // Sanitization just strips tags if disallowed; then emit as plain text
            processed = sanitize(processed, s);
            return Component.text(processed);
        }

        processed = sanitize(processed, s);
        final TagResolver resolver = (resolvers != null && resolvers.length > 0)
                ? TagResolver.resolver(resolvers)
                : TagResolver.empty();
        return MM.deserialize(processed, resolver);
    }

    /**
     * Formats a raw string with optional PlaceholderAPI expansion for the given player.
     *
     * @param message  raw message (MiniMessage or plain)
     * @param player   player context for PAPI (optional)
     * @param settings formatting controls (null uses {@code DreamMessageSettings.all()})
     * @return formatted Component (never null)
     */
    public static @NotNull Component format(@Nullable String message,
                                            @Nullable Player player,
                                            @Nullable DreamMessageSettings settings) {
        return format(message, player, settings, TagResolver.empty());
    }

    // ---------------------------------------------------------------------
    // Public API — Component -> Component
    // ---------------------------------------------------------------------

    /**
     * Pass-through formatting for a {@link Component}. If MiniMessage is disabled, the component is returned as-is.
     * If placeholders are enabled and a player is provided, we serialize to MiniMessage, apply PAPI, sanitize, and
     * deserialize back into a new Component to preserve formatting.
     *
     * @param component input Component (may be pre-built elsewhere)
     * @param settings  formatting controls (null uses {@code DreamMessageSettings.all()})
     * @return formatted Component (never null)
     */
    public static @NotNull Component format(@Nullable Component component,
                                            @Nullable DreamMessageSettings settings) {
        return format(component, null, settings, TagResolver.empty());
    }

    /**
     * Full control formatting for a {@link Component} with PAPI + resolvers.
     * See {@link #format(Component, DreamMessageSettings)} for behavior details.
     */
    public static @NotNull Component format(@Nullable Component component,
                                            @Nullable Player player,
                                            @Nullable DreamMessageSettings settings,
                                            TagResolver... resolvers) {
        if (component == null) return Component.empty();
        final DreamMessageSettings s = nonNull(settings);

        // If no MiniMessage or no placeholders are needed, return the component after optional sanitize short-circuit.
        if (!s.allowMiniMessage() && !(s.usePlaceholders() && player != null && isPapiAvailable())) {
            return component;
        }

        // Serialize to MiniMessage so PAPI & sanitization can work over a string representation.
        String mm = MM.serialize(component);

        if (s.usePlaceholders() && player != null && isPapiAvailable()) {
            mm = PlaceholderAPI.setPlaceholders(player, mm);
        }

        mm = sanitize(mm, s);

        final TagResolver resolver = (resolvers != null && resolvers.length > 0)
                ? TagResolver.resolver(resolvers)
                : TagResolver.empty();

        return MM.deserialize(mm, resolver);
    }

    // ---------------------------------------------------------------------
    // Helpers — presentation utilities
    // ---------------------------------------------------------------------

    /**
     * Centers a string to a given width using spaces (monospaced display assumption).
     */
    public static @Nullable String centerMessage(@Nullable String message, int width) {
        if (message == null) return null;
        final int w = Math.max(width, message.length());
        final int pad = w - message.length();
        final int left = pad / 2;
        final int right = pad - left;
        return " ".repeat(left) + message + " ".repeat(right);
        // For chat GUIs with proportional fonts, consider pixel-width centering instead.
    }

    /**
     * Centers a Component by converting to plain text, centering, then returning as a plain text Component.
     * (Useful when you want quick-and-dirty centering without pixel-aware logic.)
     */
    public static @NotNull Component centerMessage(@NotNull Component component, int width) {
        final String plain = PlainTextComponentSerializer.plainText().serialize(component);
        final String centered = centerMessage(plain, width);
        return Component.text(centered == null ? "" : centered);
    }

    /**
     * Truncates a string to maxLen characters and appends ellipsis when needed.
     */
    public static @NotNull String limitMessage(@Nullable String message, int maxLen) {
        if (message == null || maxLen <= 0) return "...";
        if (message.length() <= maxLen) return message;
        return message.substring(0, Math.max(0, maxLen)) + "...";
    }

    /**
     * Truncates a Component's plain-text view and returns a plain text Component result.
     */
    public static @NotNull Component limitMessage(@NotNull Component component, int maxLen) {
        final String plain = PlainTextComponentSerializer.plainText().serialize(component);
        return Component.text(limitMessage(plain, maxLen));
    }

    /**
     * Capitalizes every word in a string.
     */
    public static @Nullable String capitalizeWords(@Nullable String message) {
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

    /**
     * Reverses a string (utility).
     */
    public static @Nullable String reverseMessage(@Nullable String message) {
        if (message == null) return null;
        return new StringBuilder(message).reverse().toString();
    }

    // ---------------------------------------------------------------------
    // Placeholder helpers
    // ---------------------------------------------------------------------

    /**
     * Creates an unparsed placeholder (MiniMessage {@code <key>} is replaced with literal value).
     */
    public static @NotNull TagResolver placeholder(@NotNull String key, @Nullable String value) {
        return Placeholder.unparsed(key, value == null ? "" : value);
    }

    /**
     * Creates a component placeholder (MiniMessage {@code <key>} is replaced with a Component).
     */
    public static @NotNull TagResolver placeholder(@NotNull String key, @NotNull Component value) {
        return Placeholder.component(key, value);
    }

    // ---------------------------------------------------------------------
    // Internals
    // ---------------------------------------------------------------------

    private static boolean isPapiAvailable() {
        return Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI");
    }

    private static @NotNull DreamMessageSettings nonNull(@Nullable DreamMessageSettings s) {
        return Objects.requireNonNullElse(s, DreamMessageSettings.all());
    }

    /**
     * Removes disallowed MiniMessage tags according to settings. When MiniMessage is disabled,
     * this prevents obvious tag leakage when outputting as plain text.
     */
    private static @NotNull String sanitize(@NotNull String input, @NotNull DreamMessageSettings s) {
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