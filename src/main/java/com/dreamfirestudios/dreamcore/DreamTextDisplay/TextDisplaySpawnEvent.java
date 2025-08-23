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
package com.dreamfirestudios.dreamcore.DreamTextDisplay;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.TextDisplay;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/// <summary>
/// Event fired after a <see cref="TextDisplay"/> is spawned by <see cref="DreamTextDisplay"/>.
/// </summary>
/// <remarks>
/// Use to apply extra initialization such as metadata or PDC tags.
/// </remarks>
/// <example>
/// <code>
/// @EventHandler
/// public void onSpawn(TextDisplaySpawnEvent e) {
///     e.getTextDisplay().text(Component.text("Overridden!"));
/// }
/// </code>
/// </example>
@Getter
public class TextDisplaySpawnEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    /// <summary>The spawned TextDisplay instance.</summary>
    private final TextDisplay textDisplay;

    /// <summary>Constructs and dispatches the event.</summary>
    public TextDisplaySpawnEvent(TextDisplay textDisplay){
        this.textDisplay = textDisplay;
        Bukkit.getPluginManager().callEvent(this);
    }

    /// <summary>Static handler accessor.</summary>
    public static HandlerList getHandlerList() { return handlers; }
    /// <inheritdoc />
    public @NotNull HandlerList getHandlers() { return handlers; }
}