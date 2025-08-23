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
package com.dreamfirestudios.dreamcore.DreamInventory;

/// <summary>
/// Logical locations for items when scanning entities or players.
/// </summary>
/// <remarks>
/// Provides classification beyond raw inventory indices (e.g., hands, armor).
/// Useful for search/filter logic when working with <see cref="ItemRef"/>.
/// </remarks>
public enum ItemLocation {
    /// <summary>Entity’s main hand (slot = <see cref="DreamInventory.SLOT_MAIN_HAND"/>).</summary>
    ENTITY_MAIN_HAND,

    /// <summary>Entity’s off hand (slot = <see cref="DreamInventory.SLOT_OFF_HAND"/>).</summary>
    ENTITY_OFF_HAND,

    /// <summary>Any armor slot (see slot index for which).</summary>
    ENTITY_ARMOR,

    /// <summary>Any backpack/hotbar slot.</summary>
    ENTITY_INVENTORY,

    /// <summary>Logical marker for broken items (helper classification).</summary>
    BROKEN_ITEM,

    /// <summary>Future expansion: container slots (e.g., chest, barrel).</summary>
    CONTAINER,

    /// <summary>Future expansion: dropped item entities in the world.</summary>
    DROPPED_ITEM
}