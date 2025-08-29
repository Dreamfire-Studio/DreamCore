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
import java.util.Objects;

/// <summary>
/// Default immutable implementation of <see cref="IDreamTimingSpec"/>.
/// </summary>
/// <remarks>
/// <para>
/// Use this to constrain timing between steps (min/max delay), require a hold duration,
/// or set a chord spread window for multi-key presses.
/// Any field may be <c>null</c> meaning "no constraint".
/// </para>
/// </remarks>
/// <example>
/// <para>
/// No constraints:
/// <code>
/// IDreamTimingSpec t = DreamTimingDefault.none();
/// </code>
/// </para>
/// <para>
/// Only chord spread (all keys within 120ms):
/// <code>
/// IDreamTimingSpec t = DreamTimingDefault.chordSpread(Duration.ofMillis(120));
/// </code>
/// </para>
/// <para>
/// Windowed step (between 100ms and 400ms after previous step):
/// <code>
/// IDreamTimingSpec t = DreamTimingDefault.window(Duration.ofMillis(100), Duration.ofMillis(400));
/// </code>
/// </para>
/// <para>
/// Full explicit construction (rarely needed):
/// <code>
/// IDreamTimingSpec t = new DreamTimingDefault(
///     Duration.ofMillis(50),     // min delay after previous
///     Duration.ofMillis(200),    // max delay after previous
///     Duration.ofMillis(250),    // required hold of key
///     Duration.ofMillis(120));   // chord spread
/// </code>
/// </para>
/// </example>
public final class DreamTimingDefault implements IDreamTimingSpec {

    private final Duration minDelayFromPrevious;
    private final Duration maxDelayFromPrevious;
    private final Duration requiredHold;
    private final Duration maxChordSpread;

    /// <summary>Create a timing spec with explicit fields (nulls allowed).</summary>
    /// <param name="minDelayFromPrevious">Minimum allowed delay since previous step (nullable).</param>
    /// <param name="maxDelayFromPrevious">Maximum allowed delay since previous step (nullable).</param>
    /// <param name="requiredHold">Required hold duration for the current key (nullable).</param>
    /// <param name="maxChordSpread">Max time window for chord keys to register together (nullable).</param>
    public DreamTimingDefault(Duration minDelayFromPrevious,
                              Duration maxDelayFromPrevious,
                              Duration requiredHold,
                              Duration maxChordSpread) {
        this.minDelayFromPrevious = minDelayFromPrevious;
        this.maxDelayFromPrevious = maxDelayFromPrevious;
        this.requiredHold = requiredHold;
        this.maxChordSpread = maxChordSpread;
    }

    /// <summary>No constraints.</summary>
    /// <returns>An <see cref="IDreamTimingSpec"/> with all fields unset.</returns>
    public static IDreamTimingSpec none() {
        return new DreamTimingDefault(null, null, null, null);
    }

    /// <summary>Only chord spread constraint.</summary>
    /// <param name="spread">Max interval in which all chord keys must be detected.</param>
    /// <returns>Timing spec with chord spread set.</returns>
    public static IDreamTimingSpec chordSpread(Duration spread) {
        Objects.requireNonNull(spread, "spread");
        return new DreamTimingDefault(null, null, null, spread);
    }

    /// <summary>Only step window constraint (min/max delay since previous step).</summary>
    /// <param name="min">Minimum allowed delay since previous step (nullable for no-min).</param>
    /// <param name="max">Maximum allowed delay since previous step (nullable for no-max).</param>
    /// <returns>Timing spec with a step window.</returns>
    public static IDreamTimingSpec window(Duration min, Duration max) {
        return new DreamTimingDefault(min, max, null, null);
    }

    /// <inheritdoc/>
    @Override public Duration minDelayFromPrevious() { return minDelayFromPrevious; }

    /// <inheritdoc/>
    @Override public Duration maxDelayFromPrevious() { return maxDelayFromPrevious; }

    /// <inheritdoc/>
    @Override public Duration requiredHold() { return requiredHold; }

    /// <inheritdoc/>
    @Override public Duration maxChordSpread() { return maxChordSpread; }
}