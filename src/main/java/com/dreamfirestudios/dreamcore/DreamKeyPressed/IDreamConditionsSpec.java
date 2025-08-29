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

import java.util.Set;

/// <summary>
/// Declarative conditions that must hold for a step or entire pattern to progress.
/// </summary>
/// <remarks>
/// <para>
/// Implementations can be provided by simple builder classes (e.g., <c>DreamConditions</c>).
/// Default methods return permissive values (no constraints).
/// </para>
/// <para>
/// Evaluation order typically is: location &rarr; posture &rarr; hands.
/// </para>
/// </remarks>
/// <example>
/// <code>
/// IDreamConditionsSpec cond = DreamConditions.builder()
///     .allowedLocations(Set.of(DreamLocationConstraint.ON_GROUND))
///     .requireSneaking(true)
///     .mainHand(HandConditions.only(Material.IRON_SWORD))
///     .offHand(HandConditions.banned(Set.of(Material.TNT)))
///     .build();
/// </code>
/// </example>
public interface IDreamConditionsSpec {
    /// <summary>Allowed player location states. Empty means any location is allowed.</summary>
    /// <returns>Set of <see cref="DreamLocationConstraint"/>; empty for no constraint.</returns>
    default Set<DreamLocationConstraint> allowedLocations() { return Set.of(); }

    /// <summary>Condition for the main-hand item. <c>null</c> means no constraint.</summary>
    default IDreamHandCondition mainHand() { return null; }

    /// <summary>Condition for the off-hand item. <c>null</c> means no constraint.</summary>
    default IDreamHandCondition offHand() { return null; }

    /// <summary>Require the player to be sneaking.</summary>
    default boolean requireSneaking() { return false; }

    /// <summary>Require the player to be sprinting.</summary>
    default boolean requireSprinting() { return false; }
}