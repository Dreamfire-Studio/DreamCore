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
package com.dreamfirestudios.dreamcore.DreamfireStorage;

import com.dreamfirestudios.dreamcore.DreamCore;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * <summary>
 * Fired before a <see cref="DreamfireStorageObject"/> is stored in
 * <see cref="DreamfireStorageManager"/>.
 * </summary>
 *
 * <remarks>
 * <list type="bullet">
 *   <item>This event is <b>cancellable</b>. Cancelling prevents the put operation.</item>
 *   <item>The event is dispatched on the main thread via <c>BukkitScheduler#runTask</c>.</item>
 * </list>
 * </remarks>
 * <example>
 * <code>
 * @EventHandler
 * public void onAdd(StorageObjectAddedEvent e) {
 *     if (e.getStorageObject().storageData() instanceof Integer i &amp;&amp; i &lt; 0) {
 *         e.setCancelled(true); // prevent storing negative numbers
 *     }
 * }
 * </code>
 * </example>
 */
public class StorageObjectAddedEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    /// <summary>The object proposed for storage.</summary>
    @Getter
    private final DreamfireStorageObject<?> storageObject;

    /// <summary>Cancellation flag.</summary>
    private boolean cancelled;

    /**
     * <summary>
     * Constructs the event and schedules synchronous dispatch via the Bukkit plugin manager.
     * </summary>
     * <param name="storageObject">The object proposed for storage.</param>
     */
    public StorageObjectAddedEvent(final DreamfireStorageObject<?> storageObject) {
        this.storageObject = storageObject;
        Bukkit.getScheduler().runTask(DreamCore.DreamCore, () -> {Bukkit.getPluginManager().callEvent(this);});
    }

    /** <returns>The static handler list (Bukkit requirement).</returns> */
    public static HandlerList getHandlerList() {
        return handlers;
    }

    /** <inheritdoc /> */
    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    /** <inheritdoc /> */
    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    /** <inheritdoc /> */
    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}