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
package com.dreamfirestudios.dreamcore.DreamFakeBlock;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * <summary>
 * Fired when a {@link DreamFakeBlock}'s material is updated and pushed to all observers.
 * </summary>
 *
 * <remarks>
 * Dispatched synchronously from the constructor via the Bukkit PluginManager.
 * </remarks>
 * <example>
 * <![CDATA[
 * @EventHandler
 * public void onUpdated(FakeBlockUpdatedEvent e) {
 *     // react to material changes
 * }
 * ]]>
 * </example>
 */
@Getter
public class FakeBlockUpdatedEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private final DreamFakeBlock fakeBlock;
    private final Material newMaterial;

    /**
     * <summary>Constructs and immediately dispatches the event.</summary>
     * <param name="fakeBlock">The fake block being updated.</param>
     * <param name="newMaterial">The new material applied.</param>
     */
    public FakeBlockUpdatedEvent(DreamFakeBlock fakeBlock, Material newMaterial) {
        this.fakeBlock = fakeBlock;
        this.newMaterial = newMaterial;
        Bukkit.getPluginManager().callEvent(this);
    }

    /** <returns>The static handler list required by Bukkit.</returns> */
    public static HandlerList getHandlerList() { return handlers; }

    /** <returns>The handler list instance.</returns> */
    @Override public @NotNull HandlerList getHandlers() { return handlers; }
}