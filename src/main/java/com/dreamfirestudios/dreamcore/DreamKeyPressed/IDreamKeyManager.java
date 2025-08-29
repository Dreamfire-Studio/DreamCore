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
package com.dreamfirestudios.dreamcore.DreamKeyPressed;

import org.bukkit.entity.Player;

import java.time.Instant;
import java.util.UUID;

/// <summary>
/// Evaluates registered key patterns for players and dispatches listener callbacks.
/// </summary>
/// <remarks>
/// <para>
/// See <see cref="DreamKeyManager"/> for a default implementation.
/// </para>
/// </remarks>
/// <example>
/// <code>
/// IDreamKeyManager manager = new DreamKeyManager();
/// UUID pid = player.getUniqueId();
/// manager.register(pid, spec, new MyListener());
/// manager.handleInput(player, DreamPressedKeys.SNEAK, Instant.now());
/// </code>
/// </example>
public interface IDreamKeyManager {
    /// <summary>Register a pattern/listener pair for a specific player.</summary>
    void register(UUID playerId, IDreamKeyPatternSpec pattern, IDreamKeyPressed listener);

    /// <summary>Remove all patterns for a player.</summary>
    void unregisterAll(UUID playerId);

    /// <summary>Unregister a specific pattern for a player.</summary>
    /// <returns><c>true</c> if something was removed; otherwise <c>false</c>.</returns>
    boolean unregister(UUID playerId, IDreamKeyPatternSpec pattern);

    /// <summary>Remove all players and patterns.</summary>
    void clear();

    /// <summary>Feed a normalized input at a specific instant for evaluation.</summary>
    void handleInput(Player player, DreamPressedKeys key, Instant at);

    /// <summary>Optional maintenance tick (e.g., timeouts, window updates).</summary>
    void tick();
}