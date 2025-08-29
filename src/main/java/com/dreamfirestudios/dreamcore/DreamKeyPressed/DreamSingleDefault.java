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

import java.util.Objects;
import java.util.Set;

/// <summary>
/// Default immutable implementation of a SINGLE <see cref="IDreamKeyStepSpec"/>.
/// </summary>
/// <remarks>
/// Represents a single key press step, optionally bound to timing and conditions.
/// </remarks>
/// <example>
/// <para>
/// Minimal single-step pattern:
/// <code>
/// var single = DreamSingleDefault.of(DreamPressedKeys.SNEAK);
/// </code>
/// </para>
/// <para>
/// With timing:
/// <code>
/// var single = DreamSingleDefault.of(DreamPressedKeys.LEFT_CLICK,
///                                    new DreamTimingDefault(Duration.ofMillis(200)));
/// </code>
/// </para>
/// <para>
/// With timing + conditions:
/// <code>
/// var cond = DreamConditions.builder().requireSneaking(true).build();
/// var single = DreamSingleDefault.of(DreamPressedKeys.RIGHT_CLICK,
///                                    new DreamTimingDefault(Duration.ofMillis(150)),
///                                    cond);
/// </code>
/// </para>
/// </example>
public final class DreamSingleDefault implements IDreamKeyStepSpec {

    private final DreamPressedKeys key;
    private final IDreamTimingSpec timing;
    private final IDreamConditionsSpec conditions;

    /// <summary>Create a single-step with explicit fields.</summary>
    /// <param name="key">Key required for this step.</param>
    /// <param name="timing">Optional timing constraints (may be null).</param>
    /// <param name="conditions">Optional conditions (may be null).</param>
    public DreamSingleDefault(DreamPressedKeys key, IDreamTimingSpec timing, IDreamConditionsSpec conditions) {
        this.key = Objects.requireNonNull(key, "key");
        this.timing = timing;
        this.conditions = conditions;
    }

    /// <summary>Create a step with only a key.</summary>
    /// <param name="key">Key required for this step.</param>
    /// <returns>New step spec instance.</returns>
    public static IDreamKeyStepSpec of(DreamPressedKeys key) {
        return new DreamSingleDefault(key, null, null);
    }

    /// <summary>Create a step with a key and timing.</summary>
    /// <param name="key">Key required for this step.</param>
    /// <param name="timing">Timing constraint spec.</param>
    /// <returns>New step spec instance.</returns>
    public static IDreamKeyStepSpec of(DreamPressedKeys key, IDreamTimingSpec timing) {
        return new DreamSingleDefault(key, timing, null);
    }

    /// <summary>Create a step with key, timing, and conditions.</summary>
    /// <param name="key">Key required for this step.</param>
    /// <param name="timing">Timing constraint spec.</param>
    /// <param name="conditions">Conditions spec.</param>
    /// <returns>New step spec instance.</returns>
    public static IDreamKeyStepSpec of(DreamPressedKeys key, IDreamTimingSpec timing, IDreamConditionsSpec conditions) {
        return new DreamSingleDefault(key, timing, conditions);
    }

    /// <inheritdoc/>
    @Override public DreamStepKind kind() { return DreamStepKind.SINGLE; }

    /// <inheritdoc/>
    @Override public DreamPressedKeys singleKey() { return key; }

    /// <inheritdoc/>
    @Override public Set<DreamPressedKeys> chordKeys() { return null; }

    /// <inheritdoc/>
    @Override public IDreamTimingSpec timing() { return timing; }

    /// <inheritdoc/>
    @Override public IDreamConditionsSpec conditions() { return conditions; }
}