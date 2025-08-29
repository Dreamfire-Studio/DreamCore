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

/**
 * Timing constraints for a key step and/or chord.
 *
 * <ul>
 *   <li><b>minDelayFromPrevious</b>: Step cannot happen before this delay since previous step.</li>
 *   <li><b>maxDelayFromPrevious</b>: Step must happen before this delay since previous step.</li>
 *   <li><b>requiredHold</b>: Key must be held for at least this duration (if supported).</li>
 *   <li><b>maxChordSpread</b>: All chord keys must occur within this window.</li>
 * </ul>
 *
 * <p>Use {@link DreamTimingDefault} helpers to construct typical cases.</p>
 *
 * <pre>
 * IDreamTimingSpec t = DreamTimingDefault.window(
 *     Duration.ofMillis(100),
 *     Duration.ofMillis(300)
 * );
 * </pre>
 */
public interface IDreamTimingSpec {
    /// <summary>Minimum delay since previous step (nullable).</summary>
    default Duration minDelayFromPrevious() { return null; }

    /// <summary>Maximum delay since previous step (nullable).</summary>
    default Duration maxDelayFromPrevious() { return null; }

    /// <summary>Required hold duration for the current key (nullable).</summary>
    default Duration requiredHold() { return null; }

    /// <summary>Maximum spread window for chord detection (nullable).</summary>
    default Duration maxChordSpread() { return null; }
}