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
