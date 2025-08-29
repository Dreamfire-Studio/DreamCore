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
/// Location-based constraints that may be applied to a key step or pattern.
/// </summary>
/// <remarks>
/// <list type="bullet">
///   <item><description><b>ON_GROUND</b> – Player must be standing on solid ground.</description></item>
///   <item><description><b>IN_AIR</b> – Player must be in the air (not on ground and not in water).</description></item>
///   <item><description><b>IN_WATER</b> – Player must be swimming or submerged in water.</description></item>
/// </list>
/// </remarks>
/// <example>
/// Usage in a conditions spec:
/// <code>
/// var cond = DreamConditions.builder()
///     .allowedLocations(Set.of(DreamLocationConstraint.ON_GROUND))
///     .build();
/// </code>
/// </example>
public enum DreamLocationConstraint {
    ON_GROUND,
    IN_AIR,
    IN_WATER
}
