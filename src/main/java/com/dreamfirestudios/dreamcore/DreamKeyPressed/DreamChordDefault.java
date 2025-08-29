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

import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;

/// <summary>
/// Default immutable implementation of a CHORD <see cref="IDreamKeyStepSpec"/>.
/// Represents a simultaneous key press combination, optionally bound
/// to timing and conditions.
/// </summary>
/// <remarks>
/// <para>
/// Chords differ from single key steps by requiring multiple keys to be pressed
/// together (e.g. SPRINT + SNEAK + HOTBAR_1).
/// </para>
/// <para>
/// Designed as a compact, immutable representation. Keys are stored as an
/// <see cref="EnumSet"/> for memory efficiency.
/// </para>
/// </remarks>
/// <example>
/// Creating a simple chord:
/// <code>
/// var chord = DreamChordDefault.of(Set.of(DreamPressedKeys.SNEAK, DreamPressedKeys.SPRINT));
/// </code>
/// <para>
/// Creating a chord with timing constraints:
/// <code>
/// var chord = DreamChordDefault.of(
///     Set.of(DreamPressedKeys.LEFT_CLICK, DreamPressedKeys.RIGHT_CLICK),
///     new DreamTimingDefault(Duration.ofMillis(200))
/// );
/// </code>
/// </para>
/// </example>
public final class DreamChordDefault implements IDreamKeyStepSpec {

    private final Set<DreamPressedKeys> keys;
    private final IDreamTimingSpec timing;
    private final IDreamConditionsSpec conditions;

    /// <summary>Create a chord step with explicit fields.</summary>
    /// <param name="keys">Set of keys that must be pressed simultaneously.</param>
    /// <param name="timing">Optional timing constraint (may be null).</param>
    /// <param name="conditions">Optional conditions that must be satisfied (may be null).</param>
    /// <exception cref="IllegalArgumentException">Thrown if keys is empty.</exception>
    public DreamChordDefault(Set<DreamPressedKeys> keys,
                             IDreamTimingSpec timing,
                             IDreamConditionsSpec conditions) {
        Objects.requireNonNull(keys, "keys");
        if (keys.isEmpty()) throw new IllegalArgumentException("Chord must contain at least one key");
        // defensive copy & compact storage
        this.keys = EnumSet.copyOf(keys);
        this.timing = timing;
        this.conditions = conditions;
    }

    /// <summary>Create a chord without timing/conditions.</summary>
    /// <param name="keys">Set of keys that must be pressed simultaneously.</param>
    /// <returns>New chord spec instance.</returns>
    public static IDreamKeyStepSpec of(Set<DreamPressedKeys> keys) {
        return new DreamChordDefault(keys, null, null);
    }

    /// <summary>Create a chord with timing.</summary>
    /// <param name="keys">Set of keys that must be pressed simultaneously.</param>
    /// <param name="timing">Timing constraint spec.</param>
    /// <returns>New chord spec instance.</returns>
    public static IDreamKeyStepSpec of(Set<DreamPressedKeys> keys, IDreamTimingSpec timing) {
        return new DreamChordDefault(keys, timing, null);
    }

    /// <summary>Create a chord with timing and conditions.</summary>
    /// <param name="keys">Set of keys that must be pressed simultaneously.</param>
    /// <param name="timing">Timing constraint spec.</param>
    /// <param name="conditions">Conditions spec.</param>
    /// <returns>New chord spec instance.</returns>
    public static IDreamKeyStepSpec of(Set<DreamPressedKeys> keys, IDreamTimingSpec timing, IDreamConditionsSpec conditions) {
        return new DreamChordDefault(keys, timing, conditions);
    }

    /// <inheritdoc/>
    @Override public DreamStepKind kind() { return DreamStepKind.CHORD; }

    /// <inheritdoc/>
    @Override public Set<DreamPressedKeys> chordKeys() { return keys; }

    /// <inheritdoc/>
    @Override public DreamPressedKeys singleKey() { return null; }

    /// <inheritdoc/>
    @Override public IDreamTimingSpec timing() { return timing; }

    /// <inheritdoc/>
    @Override public IDreamConditionsSpec conditions() { return conditions; }
}