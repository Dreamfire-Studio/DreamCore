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
import org.bukkit.Bukkit;

import java.util.UUID;

/**
 * <summary>
 * Contract for repeating loops driven by the Bukkit scheduler.
 * </summary>
 *
 * <remarks>
 * Implementations represent periodic tasks that DreamCore schedules on the main
 * server thread. Each loop runs until explicitly cancelled via {@link #CancelLoop()}.
 * <br/><br/>
 * <b>Lifecycle</b>:
 * <ul>
 *   <li>{@link #Start()} — invoked once before the first run.</li>
 *   <li>{@link #Loop()} — invoked every {@link #LoopInterval()} ticks after an optional {@link #StartDelay()}.</li>
 *   <li>{@link #End()} — invoked once after cancellation for cleanup.</li>
 * </ul>
 * </remarks>
 *
 * <example>
 * <code>
 * public final class BroadcastLoop implements IDreamLoop {
 *     private int taskId = -1;
 *     public UUID ReturnID() { return UUID.randomUUID(); }
 *     public long StartDelay() { return 0L; }
 *     public long LoopInterval() { return 100L; } // ~5 seconds
 *     public void Start() { Bukkit.getLogger().info("Broadcast loop starting"); }
 *     public void Loop()  { Bukkit.broadcastMessage("Hello every 5 seconds!"); }
 *     public void End()   { Bukkit.getLogger().info("Broadcast loop ended"); }
 *     public void PassID(int id) { this.taskId = id; }
 *     public int GetId() { return this.taskId; }
 * }
 * </code>
 * </example>
 */
public interface IDreamLoop {

    /**
     * <summary>
     * Unique identifier for this loop within the DreamCore registry.
     * </summary>
     *
     * <remarks>
     * Returned value is used as the key in {@code DreamCore.IDreamLoops}.
     * If you return a newly generated UUID each call, ensure the same instance
     * is used for registration and cancellation; otherwise removal may fail.
     * </remarks>
     *
     * <returns>
     * A {@link UUID} representing the loop identity.
     * </returns>
     */
    UUID ReturnID();

    /**
     * <summary>
     * Delay before the first {@link #Loop()} call.
     * </summary>
     *
     * <returns>
     * Number of ticks to wait before first execution. Default is {@code 0L}.
     * </returns>
     */
    default long StartDelay() { return 0L; }

    /**
     * <summary>
     * Interval between consecutive {@link #Loop()} calls.
     * </summary>
     *
     * <returns>
     * Number of ticks between executions. Default is {@code 20L} (~1 second).
     * </returns>
     */
    default long LoopInterval() { return 20L; }

    /**
     * <summary>
     * Called once before the scheduler begins invoking {@link #Loop()}.
     * </summary>
     *
     * <remarks>
     * Initialize any state here. Avoid heavy/blocking work to keep the main
     * thread responsive.
     * </remarks>
     */
    void Start();

    /**
     * <summary>
     * Called on each tick interval.
     * </summary>
     *
     * <remarks>
     * Runs on the main thread. Do not block. Offload long-running work to async
     * tasks if necessary.
     * </remarks>
     */
    void Loop();

    /**
     * <summary>
     * Called once after cancellation to release resources.
     * </summary>
     *
     * <remarks>
     * Unregister listeners, close handles, and clear references here.
     * </remarks>
     */
    void End();

    /**
     * <summary>
     * Cancels the scheduled task, calls {@link #End()}, and unregisters
     * this loop from {@code DreamCore.IDreamLoops}.
     * </summary>
     *
     * <remarks>
     * Steps performed:
     * <ol>
     *   <li>Cancel Bukkit task via {@link Bukkit#getScheduler()}.</li>
     *   <li>Invoke {@link #End()} in a finally-safe block.</li>
     *   <li>Attempt to remove {@link #ReturnID()} from the DreamCore registry.</li>
     * </ol>
     * Exceptions during registry cleanup are swallowed to avoid masking shutdown.
     * </remarks>
     */
    default void CancelLoop() {
        final int id = GetId();
        if (id >= 0) {
            Bukkit.getScheduler().cancelTask(id);
        }
        try {
            End();
        } finally {
            try {
                DreamCore.IDreamLoops.remove(ReturnID());
            } catch (Throwable ignored) {
                // Best-effort cleanup; do not propagate
            }
        }
    }

    /**
     * <summary>
     * Internal hook used by the scheduler to assign the Bukkit task ID.
     * </summary>
     *
     * <param name="id">Task ID returned by Bukkit scheduling.</param>
     */
    void PassID(int id);

    /**
     * <summary>
     * Retrieves the Bukkit scheduler task ID.
     * </summary>
     *
     * <returns>
     * The task ID, or {@code -1} if not currently scheduled.
     * </returns>
     */
    int GetId();
}