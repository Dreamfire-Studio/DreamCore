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