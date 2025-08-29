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
/// The kind of step represented in a key pattern.
/// </summary>
/// <remarks>
/// <list type="bullet">
///   <item><description><b>SINGLE</b> – A single key press (see <see cref="DreamSingleDefault"/>).</description></item>
///   <item><description><b>CHORD</b> – Multiple keys pressed together (see <see cref="DreamChordDefault"/>).</description></item>
/// </list>
/// </remarks>
/// <example>
/// <code>
/// for (IDreamKeyStepSpec step : pattern.steps()) {
///     if (step.kind() == DreamStepKind.SINGLE) {
///         System.out.println("Single key: " + step.singleKey());
///     } else if (step.kind() == DreamStepKind.CHORD) {
///         System.out.println("Chord keys: " + step.chordKeys());
///     }
/// }
/// </code>
/// </example>
public enum DreamStepKind {
    SINGLE,
    CHORD
}