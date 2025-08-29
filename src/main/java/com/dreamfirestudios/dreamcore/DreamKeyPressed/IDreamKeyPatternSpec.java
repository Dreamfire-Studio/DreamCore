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

import java.time.Duration;
import java.util.List;

/// <summary>
/// Immutable specification describing a key pattern to be evaluated.
/// </summary>
/// <remarks>
/// <para>
/// Built via <see cref="DreamKeyPatternBuilder"/> and consumed by <see cref="IDreamKeyManager"/>.
/// </para>
/// </remarks>
/// <example>
/// <code>
/// IDreamKeyPatternSpec spec = DreamKeyPatternBuilder.create()
///     .inOrder()
///     .totalTimeout(Duration.ofSeconds(4))
///     .resetOnFailure(true)
///     .firstTimeOnly(false)
///     .key(DreamPressedKeys.SNEAK)
///     .chord(DreamPressedKeys.LEFT_CLICK, DreamPressedKeys.RIGHT_CLICK)
///     .build();
/// </code>
/// </example>
public interface IDreamKeyPatternSpec {
    /// <summary>True if this pattern should process while an inventory GUI is open.</summary>
    boolean worksInInventory();

    /// <summary>Whether pattern is evaluated <c>AllAtOnce</c> or <c>InOrder</c>.</summary>
    DreamPressedType pressedType();

    /// <summary>Steps that make up this pattern.</summary>
    List<IDreamKeyStepSpec> steps();

    /// <summary>Total time window from start to completion (nullable for unlimited).</summary>
    default Duration totalTimeout() { return null; }

    /// <summary>Whether to reset to step 0 if a step fails.</summary>
    default boolean resetOnFailure() { return true; }

    /// <summary>Global conditions applied unless overridden by a step (nullable).</summary>
    default IDreamConditionsSpec conditions() { return null; }

    /// <summary>One-shot pattern: remove after first success if true (default true).</summary>
    default boolean firstTimeOnly() { return true; }

    /// <summary>Cooldown to apply after success (default zero = none).</summary>
    default Duration cooldown() { return Duration.ZERO; }
}