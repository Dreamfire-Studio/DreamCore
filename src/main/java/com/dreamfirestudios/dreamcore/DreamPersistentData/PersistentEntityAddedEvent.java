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
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/// <summary>
/// Event fired when a persistent data entry is added to an <see cref="Entity"/>.
/// </summary>
/// <remarks>
/// Not cancellable. Fired immediately after the container is updated.
/// </remarks>
@Getter
public class PersistentEntityAddedEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    /// <summary>Entity that received the entry.</summary>
    private final Entity entity;

    /// <summary>Key under which the value was stored.</summary>
    private final NamespacedKey namespacedKey;

    /// <summary>The stored value.</summary>
    private final Object value;

    /// <summary>Creates and dispatches the event.</summary>
    public PersistentEntityAddedEvent(Entity entity, NamespacedKey namespacedKey, Object value){
        this.entity = entity;
        this.namespacedKey = namespacedKey;
        this.value = value;
        Bukkit.getPluginManager().callEvent(this);
    }

    /// <summary>Static handler list accessor.</summary>
    public static HandlerList getHandlerList() { return handlers; }

    /// <inheritdoc />
    @Override public @NotNull HandlerList getHandlers() { return handlers; }
}