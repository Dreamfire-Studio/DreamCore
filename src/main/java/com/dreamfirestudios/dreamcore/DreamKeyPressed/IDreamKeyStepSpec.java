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
/// A single step in a key pattern.
/// </summary>
/// <remarks>
/// <para>
/// A step is either <see cref="DreamStepKind.SINGLE"/> (one key) or <see cref="DreamStepKind.CHORD"/> (multiple keys).
/// Timing and conditions may be provided per-step; if absent, pattern-level settings apply.
/// </para>
/// </remarks>
/// <example>
/// <code>
/// IDreamKeyStepSpec step = DreamSingleDefault.of(DreamPressedKeys.SNEAK);
/// if (step.kind() == DreamStepKind.SINGLE) {
///     // ...
/// }
/// </code>
/// </example>
public interface IDreamKeyStepSpec {
    /// <summary>Whether this is a SINGLE or CHORD step.</summary>
    DreamStepKind kind();

    /// <summary>Per-step timing constraints (nullable).</summary>
    default IDreamTimingSpec timing() { return null; }

    /// <summary>The single key for SINGLE steps; otherwise <c>null</c>.</summary>
    default DreamPressedKeys singleKey() { return null; }

    /// <summary>The chord keys for CHORD steps; otherwise <c>null</c>.</summary>
    default Set<DreamPressedKeys> chordKeys() { return null; }

    /// <summary>Per-step conditions overriding pattern-level ones (nullable).</summary>
    default IDreamConditionsSpec conditions() { return null; }
}