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

import org.bukkit.Material;

import java.util.Set;

/// <summary>
/// Predicate-like constraints for a specific hand (main or off-hand).
/// </summary>
/// <remarks>
/// All sets are treated as inclusive filters unless empty. If multiple sets are provided,
/// they are combined (e.g., must be present AND in allowOnly AND not in banned).
/// </remarks>
/// <example>
/// <code>
/// IDreamHandCondition hc = HandConditions.builder()
///     .requireItemPresent(true)
///     .allowOnlyMaterials(Set.of(Material.BOW, Material.CROSSBOW))
///     .bannedMaterials(Set.of(Material.TIPPED_ARROW)) // example ban
///     .build();
/// </code>
/// </example>
public interface IDreamHandCondition {
    /// <summary>Require that some item is present (not AIR).</summary>
    default boolean requireItemPresent() { return false; }

    /// <summary>Item must be one of these materials (empty means no restriction).</summary>
    default Set<Material> allowOnlyMaterials() { return Set.of(); }

    /// <summary>Item must be at least one of these materials (empty means no requirement).</summary>
    default Set<Material> requireAnyOfMaterials() { return Set.of(); }

    /// <summary>Item must NOT be any of these materials (empty means no bans).</summary>
    default Set<Material> bannedMaterials() { return Set.of(); }
}