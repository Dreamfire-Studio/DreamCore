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
package com.dreamfirestudios.dreamcore.DreamWorld;

/// <summary>
/// Common time-lock presets for setting world time.
/// </summary>
/// <remarks>
/// Values are in Minecraft tick-time within a day (0..23999).
/// </remarks>
public enum TimeLock {
    /// <summary>Exact start of day (0).</summary>
    Daytime(0),
    /// <summary>Morning (1000).</summary>
    Day(1000),
    /// <summary>Noon (6000).</summary>
    Noon(6000),
    /// <summary>Sunset (12000).</summary>
    Sunset(12000),
    /// <summary>Early night (13000).</summary>
    Nighttime(13000),
    /// <summary>Midnight (18000).</summary>
    Midnight(18000),
    /// <summary>Sunrise (23000).</summary>
    Sunrise(23000);

    /// <summary>World time value.</summary>
    public final long time;
    TimeLock(long time){ this.time = time; }
}