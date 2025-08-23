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
package com.dreamfirestudios.dreamcore.DreamChat;

/// <summary>
/// Immutable settings that control how <see cref="DreamMessageFormatter"/> processes messages.
/// </summary>
/// <param name="usePlaceholders">Whether PlaceholderAPI should be applied (when a player is available).</param>
/// <param name="allowMiniMessage">Whether MiniMessage parsing/deserialization is allowed.</param>
/// <param name="allowColors">Whether color tags are allowed when MiniMessage is enabled.</param>
/// <param name="allowFormatting">Whether style tags (bold/italic/etc.) are allowed.</param>
/// <param name="allowClickAndHover">Whether click/hover actions are allowed.</param>
public record DreamMessageSettings(
        boolean usePlaceholders,
        boolean allowMiniMessage,
        boolean allowColors,
        boolean allowFormatting,
        boolean allowClickAndHover
) {

    /// <summary>
    /// Full‑feature preset: placeholders + MiniMessage + colors + formatting + click/hover.
    /// </summary>
    public static DreamMessageSettings all() {
        return new DreamMessageSettings(true, true, true, true, true);
    }

    /// <summary>
    /// Safer preset: placeholders + MiniMessage with colors/formatting, but no click/hover actions.
    /// </summary>
    public static DreamMessageSettings safeChat() {
        return new DreamMessageSettings(true, true, true, true, false);
    }

    /// <summary>
    /// Plain preset: no placeholders, no MiniMessage, no colors/formatting/actions.
    /// </summary>
    public static DreamMessageSettings plain() {
        return new DreamMessageSettings(false, false, false, false, false);
    }
}