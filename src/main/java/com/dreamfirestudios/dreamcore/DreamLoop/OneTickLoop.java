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
 * Loop that fires every server tick (~20 times per second).
 * </summary>
 *
 * <remarks>
 * Use for extremely lightweight, high-frequency operations (e.g., fine-grained
 * animation ticks). Heavy logic here can harm server performance.
 * </remarks>
 *
 * <example>
 * <code>
 * @PulseAutoRegister
 * public final class HudTickLoop extends OneTickLoop {
 *     public void Loop() { updateAllPlayerHUDs(); }
 * }
 * </code>
 * </example>
 */
@PulseAutoRegister
public class OneTickLoop implements IDreamLoop {

    /**
     * <summary>
     * Constant start delay (ticks) for this loop flavor.
     * </summary>
     *
     * <remarks>
     * Exposed via Lombok-generated static getter {@code getStartDelay()}.
     * </remarks>
     */
    @Getter private static final long startDelay = 0L;

    /**
     * <summary>
     * Constant loop interval (ticks) — every tick.
     * </summary>
     *
     * <remarks>
     * Exposed via Lombok-generated static getter {@code getLoopInterval()}.
     * </remarks>
     */
    @Getter private static final long loopInterval = 1L;

    /**
     * <summary>
     * The Bukkit scheduler task ID associated with this loop instance.
     * </summary>
     *
     * <remarks>
     * {@code -1} indicates not scheduled yet.
     * </remarks>
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
     * <summary>Delay before the first tick.</summary>
     * <returns>{@code 0L}.</returns>
     @Override public long StartDelay() { return startDelay; }

     /**
      * <summary>Interval between ticks.</summary>
      * <returns>{@code 1L}.</returns>
     @Override public long LoopInterval() { return loopInterval; }

     /**
      * <summary>Initialize per-loop state.</summary>
      * <remarks>Keep it lightweight.</remarks>
     */
    @Override
    public void Start() {
        // Initialize fast-tick state if required
    }

    /**
     * <summary>
     * Executes the 1-tick dispatcher in DreamCore.
     * </summary>
     * <remarks>Runs on the main thread; do not block.</remarks>
     */
    @Override
    public void Loop() {
        DreamCore.DreamCore.OneTickClasses();
    }

    /**
     * <summary>Cleanup logic for loop shutdown.</summary>
     */
    @Override
    public void End() {
        // Cleanup for fast-tick components if required
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