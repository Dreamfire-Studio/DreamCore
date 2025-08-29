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
package com.dreamfirestudios.dreamcore.DreamKeyPressed;

/// <summary>
/// Enumeration of normalized key inputs used by the Dream key system.
/// </summary>
/// <remarks>
/// These values are mapped from Bukkit events by <see cref="DreamKeyBukkitAdapter"/>
/// to provide a uniform abstraction for pattern evaluation.
/// </remarks>
/// <example>
/// Detecting a simple combo:
/// <code>
/// DreamKeyPatternBuilder.create()
///     .inOrder()
///     .key(DreamPressedKeys.SNEAK)
///     .key(DreamPressedKeys.LEFT_CLICK)
///     .build();
/// </code>
/// </example>
public enum DreamPressedKeys {
    /// <summary>Pressing the sneak key (usually Shift).</summary>
    SNEAK,
    /// <summary>Pressing the sprint key (usually Ctrl).</summary>
    SPRINT,
    /// <summary>Dropped an item from inventory.</summary>
    DROP_ITEM,
    /// <summary>Swapped items between main hand and off hand.</summary>
    SWAP_HANDS,
    /// <summary>Opened an inventory GUI.</summary>
    INVENTORY_OPEN,

    /// <summary>Selected hotbar slot 1.</summary>
    HOTBAR_1,
    /// <summary>Selected hotbar slot 2.</summary>
    HOTBAR_2,
    /// <summary>Selected hotbar slot 3.</summary>
    HOTBAR_3,
    /// <summary>Selected hotbar slot 4.</summary>
    HOTBAR_4,
    /// <summary>Selected hotbar slot 5.</summary>
    HOTBAR_5,
    /// <summary>Selected hotbar slot 6.</summary>
    HOTBAR_6,
    /// <summary>Selected hotbar slot 7.</summary>
    HOTBAR_7,
    /// <summary>Selected hotbar slot 8.</summary>
    HOTBAR_8,
    /// <summary>Selected hotbar slot 9.</summary>
    HOTBAR_9,

    /// <summary>Left-click input (air or block).</summary>
    LEFT_CLICK,
    /// <summary>Right-click input (air or block).</summary>
    RIGHT_CLICK
}