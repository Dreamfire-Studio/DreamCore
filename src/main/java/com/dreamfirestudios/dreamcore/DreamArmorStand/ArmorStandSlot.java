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
package com.dreamfirestudios.dreamcore.DreamArmorStand;

/// <summary>
/// Represents the equipment slots of an <see cref="org.bukkit.entity.ArmorStand"/>.
/// </summary>
public enum ArmorStandSlot {
    /// <summary>
    /// The head slot of the armor stand (helmet or head item).
    /// </summary>
    HEAD,

    /// <summary>
    /// The chest slot of the armor stand (chestplate or elytra).
    /// </summary>
    CHEST,

    /// <summary>
    /// The legs slot of the armor stand (leggings).
    /// </summary>
    LEGS,

    /// <summary>
    /// The feet slot of the armor stand (boots).
    /// </summary>
    FEET,

    /// <summary>
    /// The main hand slot of the armor stand.
    /// </summary>
    HAND,

    /// <summary>
    /// The offhand slot of the armor stand (shield or secondary item).
    /// </summary>
    OFFHAND;
}