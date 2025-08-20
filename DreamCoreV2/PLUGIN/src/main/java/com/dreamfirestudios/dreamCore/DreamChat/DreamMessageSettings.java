package com.dreamfirestudios.dreamCore.DreamChat;

public record DreamMessageSettings(boolean usePlaceholders, boolean allowMiniMessage, boolean allowColors, boolean allowFormatting, boolean allowClickAndHover) {
    public static DreamMessageSettings all() {
        return new DreamMessageSettings(true, true, true, true, true);
    }

    public static DreamMessageSettings safeChat() {
        return new DreamMessageSettings(true, true, true, true, false);
    }

    public static DreamMessageSettings plain() {
        return new DreamMessageSettings(false, false, false, false, false);
    }
}
