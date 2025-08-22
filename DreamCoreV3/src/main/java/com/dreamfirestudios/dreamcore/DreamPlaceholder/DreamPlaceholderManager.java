package com.dreamfirestudios.dreamcore.DreamPlaceholder;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Central registry/dispatcher for DreamCore PlaceholderAPI placeholders.
 *
 * <p>Features:</p>
 * <ul>
 *   <li>O(1) lookup via ConcurrentHashMap</li>
 *   <li>Argument support: "%identifier:key:arg1:arg2%" or "%identifier:key_arg1_arg2%"</li>
 *   <li>Configurable identifier/author/version</li>
 *   <li>Safe fallbacks (empty string on unknown keys)</li>
 * </ul>
 *
 * <p>Typical usage:</p>
 * <pre>{@code
 * DreamPlaceholderManager mgr = new DreamPlaceholderManager("dreamcore", "Dreamfire Studios", "1.0.0");
 * mgr.register(new BalancePlaceholder());  // key() -> "balance"
 * mgr.register();                          // call PlaceholderAPI's register() if not auto-registered elsewhere
 * }</pre>
 */
public class DreamPlaceholderManager extends PlaceholderExpansion {

    private final String identifier;
    private final String author;
    private final String version;

    // key -> provider
    private final Map<String, IDreamPlaceholder> placeholders = new ConcurrentHashMap<>();

    /**
     * @param identifier expansion identifier used in placeholders: %identifier_key%
     * @param author     author string
     * @param version    version string
     */
    public DreamPlaceholderManager(@NotNull String identifier,
                                   @NotNull String author,
                                   @NotNull String version) {
        this.identifier = Objects.requireNonNull(identifier, "identifier").toLowerCase();
        this.author = Objects.requireNonNull(author, "author");
        this.version = Objects.requireNonNull(version, "version");
    }

    /* ----------------------------- Expansion meta ----------------------------- */

    @Override public @NotNull String getIdentifier() { return identifier; }
    @Override public @NotNull String getAuthor()     { return author; }
    @Override public @NotNull String getVersion()    { return version; }
    @Override public boolean canRegister()           { return true; }
    @Override public boolean persist()               { return true; } // survive /papi reload

    /* ----------------------------- Registration API ----------------------------- */

    /**
     * Registers a placeholder provider. Keys are case-insensitive and stored lower-case.
     * @return true if registered; false if a provider with the same key is already present
     */
    public boolean register(@NotNull IDreamPlaceholder provider) {
        Objects.requireNonNull(provider, "provider");
        final String key = provider.key().toLowerCase();
        return placeholders.putIfAbsent(key, provider) == null;
    }

    /**
     * Unregisters a provider by key (case-insensitive).
     * @return true if removed, false if not present
     */
    public boolean unregister(@NotNull String key) {
        Objects.requireNonNull(key, "key");
        return placeholders.remove(key.toLowerCase()) != null;
    }

    /** Removes all registered providers. */
    public void clear() {
        placeholders.clear();
    }

    /* ----------------------------- Request handling ----------------------------- */

    /**
     * PlaceholderAPI (modern) hook. Uses OfflinePlayer for compatibility with wider contexts.
     * Delegates to {@link #resolve(OfflinePlayer, String)}.
     */
    @Override
    public @Nullable String onRequest(OfflinePlayer offlinePlayer, @NotNull String params) {
        return resolve(offlinePlayer, params);
    }

    /**
     * PlaceholderAPI (legacy) hook for Player. Kept for compatibility with some server setups.
     * Delegates to {@link #resolve(OfflinePlayer, String)}.
     */
    @Override
    public @Nullable String onPlaceholderRequest(Player player, @NotNull String params) {
        return resolve(player, params);
    }

    /**
     * Resolves a placeholder request like "key:arg1:arg2" or "key_arg1_arg2".
     * Returns empty string on unknown keys to avoid literal placeholder leaking into chat/GUI.
     */
    private @NotNull String resolve(@Nullable OfflinePlayer offlinePlayer, @NotNull String rawParams) {
        final String params = rawParams.trim();
        if (params.isEmpty()) return "";

        // Split either by ':' or '_' (first token is the key, the rest are args)
        final String[] colonSplit = params.split(":");
        final String[] parts = (colonSplit.length > 1) ? colonSplit : params.split("_");

        final String key = parts[0].toLowerCase();
        final String[] args = (parts.length > 1) ? Arrays.copyOfRange(parts, 1, parts.length) : new String[0];

        final IDreamPlaceholder provider = placeholders.get(key);
        if (provider == null) {
            // Optional: Uncomment for debugging unknown keys
            // Bukkit.getLogger().fine("[DreamPlaceholder] Unknown key: " + key + " (params=" + params + ")");
            return "";
        }

        try {
            return provider.resolve(offlinePlayer, args);
        } catch (Exception ex) {
            Bukkit.getLogger().warning("[DreamPlaceholder] Error resolving key '" + key + "' with args " +
                    Arrays.toString(args) + ": " + ex.getMessage());
            return "";
        }
    }
}