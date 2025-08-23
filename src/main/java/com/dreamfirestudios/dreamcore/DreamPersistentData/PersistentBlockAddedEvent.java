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
package com.dreamfirestudios.dreamcore.DreamPersistentData;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/// <summary>
/// Event fired when a persistent data entry is added to a <see cref="Block"/>.
/// </summary>
/// <remarks>
/// This event is not cancellable. Fired immediately after the value is set and <c>TileState#update()</c> is called.
/// </remarks>
/// <example>
/// ```java
/// @EventHandler
/// public void onPersistentBlockAdded(PersistentBlockAddedEvent e) {
///     Block b = e.getBlock();
///     getLogger().info("Added key " + e.getNamespacedKey() + " to block at " + b.getLocation());
/// }
/// ```
/// </example>
@Getter
public class PersistentBlockAddedEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    /// <summary>Block that received the entry.</summary>
    private final Block block;

    /// <summary>Key under which the value was stored.</summary>
    private final NamespacedKey namespacedKey;

    /// <summary>The stored value.</summary>
    private final Object value;

    /// <summary>
    /// Initializes a new event and calls it through the plugin manager.
    /// </summary>
    /// <param name="block">Target block.</param>
    /// <param name="namespacedKey">Stored key.</param>
    /// <param name="value">Stored value.</param>
    public PersistentBlockAddedEvent(Block block, NamespacedKey namespacedKey, Object value) {
        this.block = block;
        this.namespacedKey = namespacedKey;
        this.value = value;
        Bukkit.getPluginManager().callEvent(this);
    }

    /// <summary>Static handler list accessor.</summary>
    public static HandlerList getHandlerList() { return handlers; }

    /// <inheritdoc />
    @Override public @NotNull HandlerList getHandlers() { return handlers; }
}