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

import java.util.UUID;

/// <summary>
/// Optional context passed during matches for advanced scenarios (reserved for future use).
/// </summary>
/// <remarks>
/// <para>
/// Implementations may carry additional state in <see cref="state()"/> for debugging or analytics.
/// Current core flow does not require this interface but keeps it for extensibility.
/// </para>
/// </remarks>
/// <example>
/// <code>
/// IDreamKeyMatchContext ctx = new IDreamKeyMatchContext() {
///     public UUID playerId() { return player.getUniqueId(); }
///     public boolean isInInventory() { return player.getOpenInventory() != null; }
///     public Object state() { return Map.of("debug", true); }
/// };
/// </code>
/// </example>
public interface IDreamKeyMatchContext {
    /// <summary>Target player identifier.</summary>
    UUID playerId();

    /// <summary>Whether the player is currently in an inventory GUI.</summary>
    boolean isInInventory();

    /// <summary>Arbitrary opaque state for callers.</summary>
    Object state();
}