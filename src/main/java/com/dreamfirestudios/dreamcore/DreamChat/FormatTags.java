package com.dreamfirestudios.dreamcore.DreamChat;

/// <summary>
/// Reserved inline formatting tokens used by <see cref="DreamChat"/> utilities.
/// </summary>
public enum FormatTags {
    /// <summary>
    /// Split token used to break messages into multiple lines.
    /// Example usage: <c>"Line 1" + FormatTags.SplitLine.tag + "Line 2"</c>
    /// </summary>
    SplitLine("<:::>");

    /// <summary>The literal token value.</summary>
    public final String tag;

    FormatTags(String tag){
        this.tag = tag;
    }
}