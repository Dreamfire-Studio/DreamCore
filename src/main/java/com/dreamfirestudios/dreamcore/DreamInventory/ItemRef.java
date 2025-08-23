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

import org.bukkit.inventory.ItemStack;

/// <summary>
/// Immutable reference to an item found during an inventory scan.
/// </summary>
/// <param name="location">Logical location (inventory, armor, hand, etc.).</param>
/// <param name="slot">Numeric slot index (armor indices are implementation-defined).</param>
/// <param name="stack">Non-null item stack reference.</param>
/// <remarks>
/// Used to avoid using mutable <see cref="ItemStack"/> directly as keys.
/// </remarks>
/// <example>
/// <code>
/// for (ItemRef ref : DreamInventory.scan(player.getInventory())) {
///     System.out.println("Found " + ref.stack().getType() + " at " + ref.location());
/// }
/// </code>
/// </example>
public record ItemRef(ItemLocation location, int slot, ItemStack stack) {}