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
import java.util.UUID;

/// <summary>
/// Listener for pattern progress and completion callbacks.
/// </summary>
/// <remarks>
/// <para>
/// Implementers should keep logic lightweight; heavy work should be deferred
/// to async tasks when appropriate.
/// </para>
/// </remarks>
/// <example>
/// <code>
/// class BlinkListener implements IDreamKeyPressed {
///     @Override public boolean worksInInventory() { return false; }
///     @Override public void PartialComplete(int index) { /* show progress HUD */ }
///     @Override public void FailedAction() { /* play fail sound */ }
///     @Override public void ActionComplete() { /* perform blink */ }
///     @Override public void TimeWindowUpdate(UUID pid, Duration remaining, Duration total, int stepIdx) { /* tick UI */ }
///     @Override public void OnCooldown(UUID pid, Duration remaining) { /* show cooldown */ }
/// }
/// </code>
/// </example>
public interface IDreamKeyPressed {
    /// <summary>True if the listener allows activation while in inventory.</summary>
    default boolean worksInInventory() { return true; }

    /// <summary>(Deprecated) Optional static hint; prefer using <see cref="IDreamKeyPatternSpec#steps()"/>.</summary>
    default IDreamKeyStepSpec[] keyOrder() { return new IDreamKeyStepSpec[0]; }

    /// <summary>(Deprecated) Optional static hint; prefer using <see cref="IDreamKeyPatternSpec#pressedType()"/>.</summary>
    default DreamPressedType dreamPressedType() { return DreamPressedType.InOrder; }

    /// <summary>Called when a step completes (index of the step just completed).</summary>
    default void PartialComplete(int index) {}

    /// <summary>Called when a pattern fails (timing/conditions/sequence mismatch).</summary>
    default void FailedAction() {}

    /// <summary>Called when a pattern successfully completes.</summary>
    default void ActionComplete() {}

    /// <summary>Called periodically to update remaining window for current step or spread.</summary>
    default void TimeWindowUpdate(UUID playerId, Duration remaining, Duration total, int stepIndex) {}

    /// <summary>Called when input is received but the pattern is on cooldown.</summary>
    default void OnCooldown(UUID playerId, Duration remaining) {}
}