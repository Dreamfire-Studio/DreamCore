package com.dreamfirestudios.dreamcore.DreamPlaceholder;

import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/// <summary>
/// Contract for a single DreamCore PlaceholderAPI provider.
/// </summary>
/// <remarks>
/// Implementations expose a unique root <see cref="key()"/> and a resolver accepting optional args.
/// </remarks>
/// <example>
/// <code>
/// public final class BalancePlaceholder implements IDreamPlaceholder {
///     public @NotNull String key() { return "balance"; }
///     public @NotNull String resolve(@Nullable OfflinePlayer player, @NotNull String[] args) {
///         // ... return balance
///     }
/// }
/// </code>
/// </example>
public interface IDreamPlaceholder {

    /// <summary>
    /// The root key (case-insensitive). Must be unique within the manager.
    /// </summary>
    /// <returns>Key such as <c>"balance"</c>, <c>"rank"</c>, <c>"server_time"</c>.</returns>
    @NotNull String key();

    /// <summary>
    /// Resolves a placeholder value.
    /// </summary>
    /// <param name="player">Associated player (may be null if context lacks a player).</param>
    /// <param name="args">Optional arguments derived from the token.</param>
    /// <returns>Resolved string (never null). Return empty string if you have nothing to show.</returns>
    @NotNull String resolve(@Nullable OfflinePlayer player, @NotNull String[] args);
}
