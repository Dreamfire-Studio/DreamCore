package com.dreamfirestudios.dreamcore.DreamChat;

import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Adventure-first formatter for strings and components.
 *
 * <p>Pipeline (string input):
 * PAPI → legacy codes? → bare-hex promotion → sanitize → MiniMessage parse (if enabled).</p>
 */
public final class DreamMessageFormatter {
    private DreamMessageFormatter() {}

    private static final MiniMessage MM = MiniMessage.miniMessage();

    /** Strip patterns when settings disallow a tag family. */
    private static final Pattern COLOR_TAGS  = Pattern.compile("</?#[0-9a-fA-F]{6}>|</?color(?:\\s*:\\s*#[0-9a-fA-F]{6})?>|</?gradient(?:\\s*:[^>]+)?>|</?rainbow(?:\\s*:[^>]+)?>");
    private static final Pattern FORMAT_TAGS = Pattern.compile("</?(bold|b|italic|i|underlined|u|strikethrough|st|obfuscated|obf)>");
    private static final Pattern ACTION_TAGS = Pattern.compile("</?click(?:\\s*:[^>]+)?>|</?hover(?:\\s*:[^>]+)?>");

    /** QoL: detect bare hex (#RRGGBB) and wrap with MiniMessage color. */
    private static final Pattern BARE_HEX    = Pattern.compile("(?i)#[0-9a-f]{6}");

    /** Detect legacy Bukkit codes (&/§ + color/format) so we can translate them. */
    private static final Pattern LEGACY_CODE = Pattern.compile("(?i)(?:&|§)[0-9A-FK-OR]");

    // ---------------------------------------------------------------------
    // String -> Component
    // ---------------------------------------------------------------------

    /**
     * Format a raw string to a {@link Component}.
     */
    public static @NotNull Component format(@Nullable String message,
                                            @Nullable DreamMessageSettings settings) {
        return format(message, null, settings);
    }

    /**
     * Format a raw string to a {@link Component} with optional PAPI + resolvers.
     *
     * <p>Order:
     * <ol>
     *   <li>PAPI expansion (if enabled)</li>
     *   <li>Legacy translation if legacy codes found</li>
     *   <li>Bare-hex promotion</li>
     *   <li>Sanitize and parse with MiniMessage (if enabled)</li>
     * </ol>
     */
    public static @NotNull Component format(@Nullable String message,
                                            @Nullable Player player,
                                            @Nullable DreamMessageSettings settings,
                                            TagResolver... resolvers) {
        if (message == null) return Component.empty();
        final DreamMessageSettings s = nonNull(settings);

        String processed = message;

        // 1) PlaceholderAPI expansion first so placeholders can introduce color codes too.
        if (s.usePlaceholders() && player != null && isPapiAvailable()) {
            processed = PlaceholderAPI.setPlaceholders(player, processed);
        }

        // 2) If we detect legacy codes (&/§), translate via Legacy serializer.
        if (LEGACY_CODE.matcher(processed).find()) {
            // Supports &x§ hex style and standard &/§ colors/formats.
            return LegacyComponentSerializer.legacyAmpersand().deserialize(processed);
        }

        // 3) QoL: promote bare “#RRGGBB” to MiniMessage color.
        if (s.allowColors()) {
            processed = BARE_HEX.matcher(processed).replaceAll(m -> "<" + m.group() + ">");
        }

        // 4) MiniMessage path or plain text fallback.
        if (!s.allowMiniMessage()) {
            processed = sanitize(processed, s);
            return Component.text(processed);
        }

        processed = sanitize(processed, s);
        final TagResolver resolver = (resolvers != null && resolvers.length > 0)
                ? TagResolver.resolver(resolvers)
                : TagResolver.empty();
        return MM.deserialize(processed, resolver);
    }

    /** Overload without resolvers. */
    public static @NotNull Component format(@Nullable String message,
                                            @Nullable Player player,
                                            @Nullable DreamMessageSettings settings) {
        return format(message, player, settings, TagResolver.empty());
    }

    // ---------------------------------------------------------------------
    // Component -> Component
    // ---------------------------------------------------------------------

    /**
     * Pass-through for components (PAPI only applicable if we round-trip through MiniMessage).
     */
    public static @NotNull Component format(@Nullable Component component,
                                            @Nullable DreamMessageSettings settings) {
        return format(component, null, settings, TagResolver.empty());
    }

    /**
     * Full-control component formatting (PAPI + resolvers).
     */
    public static @NotNull Component format(@Nullable Component component,
                                            @Nullable Player player,
                                            @Nullable DreamMessageSettings settings,
                                            TagResolver... resolvers) {
        if (component == null) return Component.empty();
        final DreamMessageSettings s = nonNull(settings);

        // If we don't need MiniMessage or PAPI, keep as-is.
        if (!s.allowMiniMessage() && !(s.usePlaceholders() && player != null && isPapiAvailable())) {
            return component;
        }

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
    // Presentation helpers (unchanged API)
    // ---------------------------------------------------------------------

    public static @Nullable String centerMessage(@Nullable String message, int width) {
        if (message == null) return null;
        final int w = Math.max(width, message.length());
        final int pad = w - message.length();
        final int left = pad / 2;
        final int right = pad - left;
        return " ".repeat(left) + message + " ".repeat(right);
    }

    public static @NotNull Component centerMessage(@NotNull Component component, int width) {
        final String plain = PlainTextComponentSerializer.plainText().serialize(component);
        final String centered = centerMessage(plain, width);
        return Component.text(centered == null ? "" : centered);
    }

    public static @NotNull String limitMessage(@Nullable String message, int maxLen) {
        if (message == null || maxLen <= 0) return "...";
        if (message.length() <= maxLen) return message;
        return message.substring(0, Math.max(0, maxLen)) + "...";
    }

    public static @NotNull Component limitMessage(@NotNull Component component, int maxLen) {
        final String plain = PlainTextComponentSerializer.plainText().serialize(component);
        return Component.text(limitMessage(plain, maxLen));
    }

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

    public static @Nullable String reverseMessage(@Nullable String message) {
        if (message == null) return null;
        return new StringBuilder(message).reverse().toString();
    }

    // ---------------------------------------------------------------------
    // Placeholder helpers (unchanged API)
    // ---------------------------------------------------------------------

    public static @NotNull TagResolver placeholder(@NotNull String key, @Nullable String value) {
        return Placeholder.unparsed(key, value == null ? "" : value);
    }

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

    private static @NotNull String sanitize(@NotNull String input, @NotNull DreamMessageSettings s) {
        String out = input;
        if (!s.allowColors())        out = COLOR_TAGS.matcher(out).replaceAll("");
        if (!s.allowFormatting())    out = FORMAT_TAGS.matcher(out).replaceAll("");
        if (!s.allowClickAndHover()) out = ACTION_TAGS.matcher(out).replaceAll("");
        return out;
    }
}