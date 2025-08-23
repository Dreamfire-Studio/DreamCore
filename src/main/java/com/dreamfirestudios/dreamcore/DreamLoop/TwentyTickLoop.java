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
package com.dreamfirestudios.dreamcore.DreamLoop;

import com.dreamfirestudios.dreamcore.DreamCore;
import com.dreamfirestudios.dreamcore.DreamJava.PulseAutoRegister;
import lombok.Getter;

import java.util.UUID;

/**
 * <summary>
 * Loop that fires every 20 ticks (~1 second).
 * </summary>
 *
 * <remarks>
 * Appropriate for periodic work like saving small state, health checks, or
 * coarse polling. Heavier than per-tick loops, but still keep work modest.
 * </remarks>
 *
 * <example>
 * <code>
 * @PulseAutoRegister
 * public final class HeartbeatLoop extends TwentyTickLoop {
 *     public void Loop() { Bukkit.getLogger().info("Heartbeat"); }
 * }
 * </code>
 * </example>
 */
@PulseAutoRegister
public class TwentyTickLoop implements IDreamLoop {

    /**
     * <summary>
     * Constant start delay (ticks) for this loop flavor.
     * </summary>
     */
    @Getter private static final long startDelay = 0L;

    /**
     * <summary>
     * Constant loop interval (ticks) — every second (20 ticks).
     * </summary>
     */
    @Getter private static final long loopInterval = 20L;

    /**
     * <summary>
     * The Bukkit scheduler task ID associated with this loop instance.
     * </summary>
     */
    private int loopID = -1;

    /**
     * <summary>
     * Unique identifier for this loop in the DreamCore registry.
     * </summary>
     *
     * <returns>A new {@link UUID} identifier.</returns>
     */
    @Override
    public UUID ReturnID() {
        return UUID.randomUUID();
    }

    /**
     * <summary>Delay before first execution.</summary>
     * <returns>{@code 0L}.</returns>
     @Override public long StartDelay() { return startDelay; }

     /**
      * <summary>Interval between executions.</summary>
      * <returns>{@code 20L}.</returns>
     @Override public long LoopInterval() { return loopInterval; }

     /**
      * <summary>Initialization hook before the first run.</summary>
     */
    @Override
    public void Start() {
        // Initialize second-based state if required
    }

    /**
     * <summary>
     * Executes the 20-tick dispatcher in DreamCore.
     * </summary>
     */
    @Override
    public void Loop() {
        DreamCore.DreamCore.TwentyTickClasses();
    }

    /**
     * <summary>Cleanup hook after cancellation.</summary>
     */
    @Override
    public void End() {
        // Cleanup for second-based tasks if required
    }

    /**
     * <summary>Assigns the Bukkit task ID.</summary>
     * <param name="id">Task ID provided by scheduler.</param>
     */
    @Override public void PassID(int id) { this.loopID = id; }

    /**
     * <summary>Returns the Bukkit task ID or {@code -1} if unscheduled.</summary>
     * <returns>Task ID or {@code -1}.</returns>
     */
     @Override public int GetId() { return this.loopID; }
 }
