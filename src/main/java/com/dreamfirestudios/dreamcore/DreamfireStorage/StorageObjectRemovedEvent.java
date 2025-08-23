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
 * Fired after a <see cref="DreamfireStorageObject"/> has been removed from
 * <see cref="DreamfireStorageManager"/>.
 * </summary>
 *
 * <remarks>
 * <list type="bullet">
 *   <item>This event is <b>cancellable</b>, but the current manager logic removes first, fires after.</item>
 *   <item>Because of that order, cancelling does not reinsert the object (behavior preserved as-is).</item>
 *   <item>The event is dispatched on the main thread via <c>BukkitScheduler#runTask</c>.</item>
 * </list>
 * If you want cancellation to prevent removal, I can rewrite the manager method to check the event first.
 * </remarks>
 * <example>
 * <code>
 * @EventHandler
 * public void onRemoved(StorageObjectRemovedEvent e) {
 *     getLogger().info("Removed: " + e.getStorageObject());
 * }
 * </code>
 * </example>
 */
public class StorageObjectRemovedEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    /// <summary>The object that was removed.</summary>
    @Getter
    private final DreamfireStorageObject<?> storageObject;

    /// <summary>Cancellation flag.</summary>
    private boolean cancelled;

    /**
     * <summary>
     * Constructs the event and schedules synchronous dispatch via the Bukkit plugin manager.
     * </summary>
     * <param name="storageObject">The object that was removed.</param>
     */
    public StorageObjectRemovedEvent(final DreamfireStorageObject<?> storageObject) {
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