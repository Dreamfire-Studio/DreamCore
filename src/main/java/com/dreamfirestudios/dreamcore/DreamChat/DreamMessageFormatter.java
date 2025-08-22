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

/// <summary>
/// Adventure‑first message formatter that supports:
/// <list type="bullet">
///   <item><description>MiniMessage parsing (configurable via <see cref="DreamMessageSettings"/>)</description></item>
///   <item><description>PlaceholderAPI expansion when a <see cref="Player"/> is supplied</description></item>
///   <item><description>Both <c>String</c> and <see cref="Component"/> inputs/outputs</description></item>
/// </list>
/// </summary>
/// <remarks>
/// When formatting a <see cref="Component"/> and placeholders are enabled, the formatter serializes to MiniMessage,
/// applies PlaceholderAPI (if present), sanitizes tags according to settings, and deserializes back.
/// </remarks>
public final class DreamMessageFormatter {

    private DreamMessageFormatter() {}

    private static final MiniMessage MM = MiniMessage.miniMessage();

    // Basic scrubbers for when MiniMessage or specific tag families are disabled in settings
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

    /// <summary>
    /// Formats a raw string to an Adventure <see cref="Component"/> using settings.
    /// </summary>
    /// <param name="message">Raw message (MiniMessage or plain).</param>
    /// <param name="settings">Formatting controls (null uses <c>DreamMessageSettings.all()</c>).</param>
    /// <returns>Formatted component (never null).</returns>
    public static @NotNull Component format(@Nullable String message,
                                            @Nullable DreamMessageSettings settings) {
        return format(message, null, settings);
    }

    /// <summary>
    /// Formats a raw string with optional PlaceholderAPI expansion for the given player.
    /// </summary>
    /// <param name="message">Raw message (MiniMessage or plain).</param>
    /// <param name="player">Player context for PAPI (optional).</param>
    /// <param name="settings">Formatting controls (null uses <c>DreamMessageSettings.all()</c>).</param>
    /// <param name="resolvers">Optional MiniMessage tag resolvers.</param>
    /// <returns>Formatted component (never null).</returns>
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
            // Sanitization strips MiniMessage tags if disallowed; then emit as plain text.
            processed = sanitize(processed, s);
            return Component.text(processed);
        }

        processed = sanitize(processed, s);
        final TagResolver resolver = (resolvers != null && resolvers.length > 0)
                ? TagResolver.resolver(resolvers)
                : TagResolver.empty();
        return MM.deserialize(processed, resolver);
    }

    /// <summary>
    /// Formats a raw string with optional PlaceholderAPI expansion for the given player.
    /// </summary>
    /// <param name="message">Raw message (MiniMessage or plain).</param>
    /// <param name="player">Player context for PAPI (optional).</param>
    /// <param name="settings">Formatting controls (null uses <c>DreamMessageSettings.all()</c>).</param>
    /// <returns>Formatted component (never null).</returns>
    public static @NotNull Component format(@Nullable String message,
                                            @Nullable Player player,
                                            @Nullable DreamMessageSettings settings) {
        return format(message, player, settings, TagResolver.empty());
    }

    // ---------------------------------------------------------------------
    // Public API — Component -> Component
    // ---------------------------------------------------------------------

    /// <summary>
    /// Pass‑through formatting for a <see cref="Component"/>. If MiniMessage is disabled, the component is returned as‑is.
    /// If placeholders are enabled and a player is provided, we serialize to MiniMessage, apply PAPI, sanitize, and
    /// deserialize back into a new Component to preserve formatting.
    /// </summary>
    /// <param name="component">Input component (may be pre‑built elsewhere).</param>
    /// <param name="settings">Formatting controls (null uses <c>DreamMessageSettings.all()</c>).</param>
    /// <returns>Formatted component (never null).</returns>
    public static @NotNull Component format(@Nullable Component component,
                                            @Nullable DreamMessageSettings settings) {
        return format(component, null, settings, TagResolver.empty());
    }

    /// <summary>
    /// Full control formatting for a <see cref="Component"/> with PAPI and MiniMessage resolvers.
    /// </summary>
    /// <param name="component">Input component.</param>
    /// <param name="player">Player context for PAPI (optional).</param>
    /// <param name="settings">Formatting controls (null uses <c>DreamMessageSettings.all()</c>).</param>
    /// <param name="resolvers">Optional MiniMessage tag resolvers.</param>
    /// <returns>Formatted component (never null).</returns>
    public static @NotNull Component format(@Nullable Component component,
                                            @Nullable Player player,
                                            @Nullable DreamMessageSettings settings,
                                            TagResolver... resolvers) {
        if (component == null) return Component.empty();
        final DreamMessageSettings s = nonNull(settings);

        // If no MiniMessage or no placeholders are needed, return as-is (minor optimization).
        if (!s.allowMiniMessage() && !(s.usePlaceholders() && player != null && isPapiAvailable())) {
            return component;
        }

        // Serialize to MiniMessage so PAPI & sanitization operate on a string representation.
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

    /// <summary>
    /// Centers a string to a given width using spaces (monospaced display assumption).
    /// </summary>
    /// <param name="message">Input string.</param>
    /// <param name="width">Target width in characters.</param>
    /// <returns>Centered string or <c>null</c> if input was <c>null</c>.</returns>
    public static @Nullable String centerMessage(@Nullable String message, int width) {
        if (message == null) return null;
        final int w = Math.max(width, message.length());
        final int pad = w - message.length();
        final int left = pad / 2;
        final int right = pad - left;
        return " ".repeat(left) + message + " ".repeat(right);
        // For proportional fonts, consider pixel-width centering instead.
    }

    /// <summary>
    /// Centers a <see cref="Component"/> by converting to plain text, centering, then returning as plain text.
    /// </summary>
    /// <param name="component">Input component.</param>
    /// <param name="width">Target width in characters.</param>
    /// <returns>Centered plain‑text component.</returns>
    public static @NotNull Component centerMessage(@NotNull Component component, int width) {
        final String plain = PlainTextComponentSerializer.plainText().serialize(component);
        final String centered = centerMessage(plain, width);
        return Component.text(centered == null ? "" : centered);
    }

    /// <summary>
    /// Truncates a string to <paramref name="maxLen"/> characters and appends ellipsis when needed.
    /// </summary>
    /// <param name="message">Input string.</param>
    /// <param name="maxLen">Maximum length.</param>
    /// <returns>Possibly truncated string; returns <c>"..."</c> for null or non‑positive max.</returns>
    public static @NotNull String limitMessage(@Nullable String message, int maxLen) {
        if (message == null || maxLen <= 0) return "...";
        if (message.length() <= maxLen) return message;
        return message.substring(0, Math.max(0, maxLen)) + "...";
    }

    /// <summary>
    /// Truncates a component's plain‑text view and returns a plain text component.
    /// </summary>
    /// <param name="component">Input component.</param>
    /// <param name="maxLen">Maximum length.</param>
    /// <returns>Truncated plain‑text component.</returns>
    public static @NotNull Component limitMessage(@NotNull Component component, int maxLen) {
        final String plain = PlainTextComponentSerializer.plainText().serialize(component);
        return Component.text(limitMessage(plain, maxLen));
    }

    /// <summary>
    /// Capitalizes every word in a string (simple ASCII rules).
    /// </summary>
    /// <param name="message">Input string.</param>
    /// <returns>Capitalized string; input unchanged if null/empty.</returns>
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

    /// <summary>
    /// Reverses a string.
    /// </summary>
    /// <param name="message">Input string.</param>
    /// <returns>Reversed string or <c>null</c> if input was <c>null</c>.</returns>
    public static @Nullable String reverseMessage(@Nullable String message) {
        if (message == null) return null;
        return new StringBuilder(message).reverse().toString();
    }

    // ---------------------------------------------------------------------
    // Placeholder helpers
    // ---------------------------------------------------------------------

    /// <summary>
    /// Creates an unparsed placeholder (MiniMessage <c>&lt;key&gt;</c> replaced with literal value).
    /// </summary>
    /// <param name="key">Placeholder key.</param>
    /// <param name="value">Literal value (null becomes empty string).</param>
    /// <returns>Tag resolver for MiniMessage.</returns>
    public static @NotNull TagResolver placeholder(@NotNull String key, @Nullable String value) {
        return Placeholder.unparsed(key, value == null ? "" : value);
    }

    /// <summary>
    /// Creates a component placeholder (MiniMessage <c>&lt;key&gt;</c> replaced with a component).
    /// </summary>
    /// <param name="key">Placeholder key.</param>
    /// <param name="value">Component value.</param>
    /// <returns>Tag resolver for MiniMessage.</returns>
    public static @NotNull TagResolver placeholder(@NotNull String key, @NotNull Component value) {
        return Placeholder.component(key, value);
    }

    // ---------------------------------------------------------------------
    // Internals
    // ---------------------------------------------------------------------

    /// <summary>
    /// Returns whether PlaceholderAPI is available and enabled.
    /// </summary>
    private static boolean isPapiAvailable() {
        return Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI");
    }

    /// <summary>
    /// Returns a non‑null settings instance, defaulting to <see cref="DreamMessageSettings.all()"/>.
    /// </summary>
    private static @NotNull DreamMessageSettings nonNull(@Nullable DreamMessageSettings s) {
        return Objects.requireNonNullElse(s, DreamMessageSettings.all());
    }

    /// <summary>
    /// Removes disallowed MiniMessage tags according to settings.
    /// When MiniMessage is disabled, this prevents obvious tag leakage when outputting as plain text.
    /// </summary>
    /// <param name="input">Input MiniMessage string.</param>
    /// <param name="s">Active settings.</param>
    /// <returns>Sanitized MiniMessage/Plain string.</returns>
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