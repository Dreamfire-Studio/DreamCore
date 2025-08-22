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

/// <summary>
/// Central registry/dispatcher for DreamCore PlaceholderAPI placeholders.
/// </summary>
/// <remarks>
/// Features:
/// <list type="bullet">
///   <item><description>O(1) lookup via <see cref="ConcurrentHashMap"/></description></item>
///   <item><description>Argument support: <c>%identifier:key:arg1:arg2%</c> or <c>%identifier:key_arg1_arg2%</c></description></item>
///   <item><description>Configurable identifier/author/version</description></item>
///   <item><description>Safe fallbacks (empty string on unknown keys)</description></item>
/// </list>
/// </remarks>
/// <example>
/// <code>
/// DreamPlaceholderManager mgr = new DreamPlaceholderManager("dreamcore", "Dreamfire Studios", "1.0.0");
/// mgr.register(new BalancePlaceholder()); // key() -> "balance"
/// mgr.register(); // call PlaceholderAPI's register() if not auto-registered elsewhere
/// </code>
/// </example>
public class DreamPlaceholderManager extends PlaceholderExpansion {

    private final String identifier;
    private final String author;
    private final String version;

    // key -> provider
    private final Map<String, IDreamPlaceholder> placeholders = new ConcurrentHashMap<>();

    /// <summary>
    /// Constructs a new placeholder manager/expansion.
    /// </summary>
    /// <param name="identifier">Expansion identifier used in tokens: <c>%identifier_key%</c>.</param>
    /// <param name="author">Author string reported to PlaceholderAPI.</param>
    /// <param name="version">Version string reported to PlaceholderAPI.</param>
    public DreamPlaceholderManager(@NotNull String identifier,
                                   @NotNull String author,
                                   @NotNull String version) {
        this.identifier = Objects.requireNonNull(identifier, "identifier").toLowerCase();
        this.author = Objects.requireNonNull(author, "author");
        this.version = Objects.requireNonNull(version, "version");
    }

    /* ----------------------------- Expansion meta ----------------------------- */

    /// <inheritdoc />
    @Override public @NotNull String getIdentifier() { return identifier; }
    /// <inheritdoc />
    @Override public @NotNull String getAuthor()     { return author; }
    /// <inheritdoc />
    @Override public @NotNull String getVersion()    { return version; }
    /// <inheritdoc />
    @Override public boolean canRegister()           { return true; }
    /// <inheritdoc />
    @Override public boolean persist()               { return true; } // survive /papi reload

    /* ----------------------------- Registration API ----------------------------- */

    /// <summary>
    /// Registers a placeholder provider. Keys are case-insensitive and stored lower-case.
    /// </summary>
    /// <param name="provider">Provider to add.</param>
    /// <returns><c>true</c> if registered; <c>false</c> if a provider with the same key already exists.</returns>
    /// <example>
    /// <code>
    /// mgr.register(new BalancePlaceholder());
    /// </code>
    /// </example>
    public boolean register(@NotNull IDreamPlaceholder provider) {
        Objects.requireNonNull(provider, "provider");
        final String key = provider.key().toLowerCase();
        return placeholders.putIfAbsent(key, provider) == null;
    }

    /// <summary>
    /// Unregisters a provider by key (case-insensitive).
    /// </summary>
    /// <param name="key">Root key to remove.</param>
    /// <returns><c>true</c> if removed, <c>false</c> if no provider was registered for the key.</returns>
    public boolean unregister(@NotNull String key) {
        Objects.requireNonNull(key, "key");
        return placeholders.remove(key.toLowerCase()) != null;
    }

    /// <summary>
    /// Removes all registered providers.
    /// </summary>
    public void clear() {
        placeholders.clear();
    }

    /* ----------------------------- Request handling ----------------------------- */

    /// <summary>
    /// PlaceholderAPI (modern) hook. Uses <see cref="OfflinePlayer"/> for broad compatibility.
    /// </summary>
    /// <param name="offlinePlayer">Requesting player (may be null for some server contexts).</param>
    /// <param name="params">Parameter string after the identifier (e.g., <c>key:arg1:arg2</c>).</param>
    /// <returns>Resolved value or empty string.</returns>
    @Override
    public @Nullable String onRequest(OfflinePlayer offlinePlayer, @NotNull String params) {
        return resolve(offlinePlayer, params);
    }

    /// <summary>
    /// PlaceholderAPI (legacy) hook for <see cref="Player"/>. Delegates to <see cref="onRequest(OfflinePlayer, String)"/>.
    /// </summary>
    /// <param name="player">Requesting player.</param>
    /// <param name="params">Parameter string.</param>
    /// <returns>Resolved value or empty string.</returns>
    @Override
    public @Nullable String onPlaceholderRequest(Player player, @NotNull String params) {
        return resolve(player, params);
    }

    /// <summary>
    /// Resolves a placeholder request like <c>"key:arg1:arg2"</c> or <c>"key_arg1_arg2"</c>.
    /// Returns empty string on unknown keys to avoid leaking raw tokens into UI.
    /// </summary>
    /// <param name="offlinePlayer">Player context (may be null).</param>
    /// <param name="rawParams">Raw parameter string.</param>
    /// <returns>Resolved value (never null; empty on failure).</returns>
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