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
package com.dreamfirestudios.dreamcore.DreamStopwatch;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/// <summary>
/// A simple tick-driven stopwatch (counts up) with pause/resume and events.
/// </summary>
/// <remarks>
/// Thread-safety: all state changes are expected to occur on the main thread.  
/// Period is in ticks; use 20 ticks for second-precision.
/// </remarks>
/// <example>
/// <code>
/// DreamStopwatch watch = new DreamStopwatch(
///     plugin,
///     20L,
///     secs -&gt; player.sendActionBar(Component.text("Time: " + DreamStopwatch.formatHMS(secs))),
///     (secs, paused) -&gt; {},
///     secs -&gt; player.sendMessage("Final: " + DreamStopwatch.formatHMS(secs))
/// );
/// watch.start();
/// </code>
/// </example>
public final class DreamStopwatch {

    private final Plugin plugin;
    private final long periodTicks;
    private final Consumer<Integer> onTick;                 // elapsed seconds
    private final BiConsumer<Integer, Boolean> onPause;     // (elapsed, paused=true/false)
    private final Consumer<Integer> onStop;                 // final elapsed seconds

    private int taskId = -1;
    private int elapsedSeconds = 0;
    private boolean paused = false;
    private boolean stopped = false;

    /// <summary>
    /// Creates a new stopwatch.
    /// </summary>
    /// <param name="plugin">Owning plugin.</param>
    /// <param name="periodTicks">Scheduler period in ticks (20 = 1s).</param>
    /// <param name="onTick">Callback fired each period with elapsed seconds.</param>
    /// <param name="onPause">Callback fired on pause/resume with (elapsed, paused).</param>
    /// <param name="onStop">Callback fired when stopped (final elapsed).</param>
    public DreamStopwatch(
            Plugin plugin,
            long periodTicks,
            Consumer<Integer> onTick,
            BiConsumer<Integer, Boolean> onPause,
            Consumer<Integer> onStop
    ) {
        this.plugin = Objects.requireNonNull(plugin, "plugin");
        this.periodTicks = Math.max(1L, periodTicks);
        this.onTick = onTick == null ? s -> {} : onTick;
        this.onPause = onPause == null ? (s, p) -> {} : onPause;
        this.onStop = onStop == null ? s -> {} : onStop;
    }

    /// <summary>Starts the stopwatch. No-op if already started.</summary>
    public void start() {
        if (taskId != -1) return;
        stopped = false;
        paused = false;
        taskId = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            if (stopped || paused) return;
            elapsedSeconds++;
            // callback
            onTick.accept(elapsedSeconds);
            // event
            Bukkit.getPluginManager().callEvent(new StopwatchTickEvent(this, elapsedSeconds));
        }, 0L, periodTicks).getTaskId();
        Bukkit.getPluginManager().callEvent(new StopwatchStartedEvent(this));
    }

    /// <summary>Pauses the stopwatch (no tick increments while paused).</summary>
    public void pause() {
        if (taskId == -1 || paused || stopped) return;
        paused = true;
        onPause.accept(elapsedSeconds, true);
        Bukkit.getPluginManager().callEvent(new StopwatchPausedEvent(this, elapsedSeconds));
    }

    /// <summary>Resumes a paused stopwatch.</summary>
    public void resume() {
        if (taskId == -1 || !paused || stopped) return;
        paused = false;
        onPause.accept(elapsedSeconds, false);
        Bukkit.getPluginManager().callEvent(new StopwatchResumedEvent(this, elapsedSeconds));
    }

    /// <summary>Stops the stopwatch and cancels the scheduled task.</summary>
    public void stop() {
        if (taskId == -1 || stopped) return;
        stopped = true;
        cancelTask();
        onStop.accept(elapsedSeconds);
        Bukkit.getPluginManager().callEvent(new StopwatchStoppedEvent(this, elapsedSeconds));
    }

    /// <summary>Resets elapsed time to zero. Does not start the task.</summary>
    public void reset() {
        elapsedSeconds = 0;
        paused = false;
        stopped = false;
    }

    private void cancelTask() {
        if (taskId != -1) {
            Bukkit.getScheduler().cancelTask(taskId);
            taskId = -1;
        }
    }

    // --- Accessors ---

    /// <summary>Gets elapsed whole seconds.</summary>
    public int getElapsedSeconds() { return elapsedSeconds; }
    /// <summary>True if running (started, not paused, not stopped).</summary>
    public boolean isRunning() { return taskId != -1 && !paused && !stopped; }
    /// <summary>True if paused.</summary>
    public boolean isPaused()  { return paused; }
    /// <summary>True if stopped.</summary>
    public boolean isStopped() { return stopped; }

    /// <summary>
    /// Utility formatter: mm:ss (e.g., 03:07).
    /// </summary>
    /// <param name="seconds">Total seconds.</param>
    /// <returns>Formatted string.</returns>
    public static String formatHMS(int seconds) {
        int m = seconds / 60;
        int s = seconds % 60;
        return String.format("%02d:%02d", m, s);
    }
}