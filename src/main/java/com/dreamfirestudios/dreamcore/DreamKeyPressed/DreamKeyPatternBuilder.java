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
import java.util.*;

/// <summary>
/// Fluent builder for <see cref="IDreamKeyPatternSpec"/>.
/// </summary>
/// <remarks>
/// <para>
/// Supports <b>InOrder</b> (sequential steps) and <b>AllAtOnce</b> (collective chord) patterns,
/// optional global timeout, per-step timing, per-pattern/step conditions, inventory allowance,
/// <c>resetOnFailure</c>, and <c>firstTimeOnly</c> behaviors.
/// </para>
/// <para>
/// Calling <see cref="build()"/> returns an immutable spec and auto-registers it into
/// <c>DreamCore.DreamKeyPatternSpecs</c> for convenience, so your adapter can bind listeners on join.
/// </para>
/// </remarks>
/// <example>
/// <para>
/// Build a reusable in-order pattern that works only outside inventories and requires timing:
/// <code>
/// var spec = DreamKeyPatternBuilder.create()
///     .inOrder()
///     .worksInInventory(false)
///     .totalTimeout(Duration.ofSeconds(5))
///     .resetOnFailure(true)
///     .firstTimeOnly(false) // reusable
///     .stepWithTiming(DreamPressedKeys.SPRINT, new DreamTimingDefault(Duration.ofMillis(300)))
///     .chordWithTiming(Set.of(DreamPressedKeys.LEFT_CLICK, DreamPressedKeys.RIGHT_CLICK),
///                      new DreamTimingDefault(Duration.ofMillis(120)))
///     .build();
/// </code>
/// </para>
/// <para>
/// Build a one-shot all-at-once pattern with conditions:
/// <code>
/// var cond = DreamConditions.builder()
///     .requireSneaking(true)
///     .allowedLocations(Set.of(PlayerLocationState.ON_GROUND))
///     .build();
///
/// var spec = DreamKeyPatternBuilder.create()
///     .allAtOnce()
///     .withConditions(cond)
///     .firstTimeOnly(true)
///     .chord(DreamPressedKeys.SNEAK, DreamPressedKeys.SWAP_HANDS, DreamPressedKeys.HOTBAR_1)
///     .build();
/// </code>
/// </para>
/// </example>
public final class DreamKeyPatternBuilder {

    private DreamPressedType type = DreamPressedType.InOrder;
    private boolean worksInInventory = true;
    private Duration totalTimeout = null;
    private boolean resetOnFailure = true;
    private boolean firstTimeOnly = true; // NEW

    private final List<IDreamKeyStepSpec> steps = new ArrayList<>();
    private IDreamConditionsSpec conditions = null;

    private DreamKeyPatternBuilder() {}

    /// <summary>Create a new builder instance.</summary>
    public static DreamKeyPatternBuilder create() { return new DreamKeyPatternBuilder(); }

    /// <summary>Configure the pattern as <c>AllAtOnce</c> (collect all keys in a single spread window).</summary>
    public DreamKeyPatternBuilder allAtOnce() { this.type = DreamPressedType.AllAtOnce; return this; }

    /// <summary>Configure the pattern as <c>InOrder</c> (sequential steps).</summary>
    public DreamKeyPatternBuilder inOrder() { this.type = DreamPressedType.InOrder; return this; }

    /// <summary>Allow or deny processing inside inventory GUIs.</summary>
    /// <param name="allowed">True to allow (default true), false to disallow.</param>
    public DreamKeyPatternBuilder worksInInventory(boolean allowed) { this.worksInInventory = allowed; return this; }

    /// <summary>Set a global total timeout for the whole pattern (optional).</summary>
    /// <param name="timeout">Total time allowed from first step start to completion.</param>
    public DreamKeyPatternBuilder totalTimeout(Duration timeout) { this.totalTimeout = timeout; return this; }

    /// <summary>Reset progress to step 0 on any step failure (default true).</summary>
    /// <param name="reset">True to reset, false to keep partial progress.</param>
    public DreamKeyPatternBuilder resetOnFailure(boolean reset) { this.resetOnFailure = reset; return this; }

    /// <summary>Make this pattern one-shot (true) or reusable (false). Default true.</summary>
    /// <param name="firstTimeOnly">True to remove after first success; false to re-arm.</param>
    public DreamKeyPatternBuilder firstTimeOnly(boolean firstTimeOnly) {
        this.firstTimeOnly = firstTimeOnly;
        return this;
    }

    /// <summary>Add a single-key step without timing/conditions.</summary>
    /// <param name="key">The key that must be pressed.</param>
    public DreamKeyPatternBuilder key(DreamPressedKeys key) {
        steps.add(new SingleStep(key, null, null));
        return this;
    }

    /// <summary>Add a chord step without timing/conditions.</summary>
    /// <param name="keys">Keys that must be pressed within the chord spread window.</param>
    public DreamKeyPatternBuilder chord(DreamPressedKeys... keys) {
        steps.add(new ChordStep(Set.of(keys), null, null));
        return this;
    }

    /// <summary>Add a single-key step with timing.</summary>
    /// <param name="key">The key that must be pressed.</param>
    /// <param name="timing">Timing window relative to previous step.</param>
    public DreamKeyPatternBuilder stepWithTiming(DreamPressedKeys key, IDreamTimingSpec timing) {
        steps.add(new SingleStep(key, timing, null));
        return this;
    }

    /// <summary>Add a chord step with timing.</summary>
    /// <param name="keys">Keys that must be pressed within <paramref name="timing"/> spread.</param>
    /// <param name="timing">Chord spread + step timing relative to previous step.</param>
    public DreamKeyPatternBuilder chordWithTiming(Set<DreamPressedKeys> keys, IDreamTimingSpec timing) {
        steps.add(new ChordStep(keys, timing, null));
        return this;
    }

    /// <summary>Attach global conditions that apply unless a step overrides them.</summary>
    /// <param name="conditions">Pattern-level conditions.</param>
    public DreamKeyPatternBuilder withConditions(IDreamConditionsSpec conditions) {
        this.conditions = conditions;
        return this;
    }

    /// <summary>
    /// Builds the immutable <see cref="IDreamKeyPatternSpec"/> and auto-registers it into
    /// <c>DreamCore.DreamKeyPatternSpecs</c> for convenience.
    /// </summary>
    /// <returns>Immutable pattern spec.</returns>
    public IDreamKeyPatternSpec build() {
        IDreamKeyPatternSpec spec = new BuiltPattern(
                worksInInventory,
                type,
                List.copyOf(steps),
                totalTimeout,
                resetOnFailure,
                conditions,
                firstTimeOnly // NEW
        );

        // optional: auto-register in core
        com.dreamfirestudios.dreamcore.DreamCore.DreamKeyPatternSpecs.add(spec);
        return spec;
    }

    // ---------------- private inner impls ----------------

    /// <summary>Immutable built pattern.</summary>
    private record BuiltPattern(
            boolean worksInInventory,
            DreamPressedType pressedType,
            List<IDreamKeyStepSpec> steps,
            Duration totalTimeout,
            boolean resetOnFailure,
            IDreamConditionsSpec conditions,
            boolean firstTimeOnly // NEW
    ) implements IDreamKeyPatternSpec {}

    /// <summary>Single-key step implementation.</summary>
    private record SingleStep(
            DreamPressedKeys key,
            IDreamTimingSpec timing,
            IDreamConditionsSpec conditions
    ) implements IDreamKeyStepSpec {
        @Override public DreamStepKind kind() { return DreamStepKind.SINGLE; }
        @Override public DreamPressedKeys singleKey() { return key; }
        @Override public Set<DreamPressedKeys> chordKeys() { return null; }
        @Override public IDreamTimingSpec timing() { return timing; }
        @Override public IDreamConditionsSpec conditions() { return conditions; }
    }

    /// <summary>Chord step implementation.</summary>
    private record ChordStep(
            Set<DreamPressedKeys> keys,
            IDreamTimingSpec timing,
            IDreamConditionsSpec conditions
    ) implements IDreamKeyStepSpec {
        @Override public DreamStepKind kind() { return DreamStepKind.CHORD; }
        @Override public DreamPressedKeys singleKey() { return null; }
        @Override public Set<DreamPressedKeys> chordKeys() { return keys; }
        @Override public IDreamTimingSpec timing() { return timing; }
        @Override public IDreamConditionsSpec conditions() { return conditions; }
    }
}