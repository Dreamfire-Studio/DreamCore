package com.dreamfirestudios.dreamcore.DreamStopwatch;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * A simple tick-driven stopwatch (counts up).
 * Thread-safety: all state changes are expected to occur on the main thread.
 */
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

    /**
     * @param plugin       owning plugin
     * @param periodTicks  scheduler period in ticks (20 = 1s). Use 20 for second-precision.
     * @param onTick       callback fired each period (elapsed seconds)
     * @param onPause      callback fired on pause/resume (elapsed, true/false)
     * @param onStop       callback fired when stopped (final elapsed)
     */
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

    /** Starts the stopwatch. No-op if already started. */
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

    /** Pauses the stopwatch (no tick increments while paused). */
    public void pause() {
        if (taskId == -1 || paused || stopped) return;
        paused = true;
        onPause.accept(elapsedSeconds, true);
        Bukkit.getPluginManager().callEvent(new StopwatchPausedEvent(this, elapsedSeconds));
    }

    /** Resumes a paused stopwatch. */
    public void resume() {
        if (taskId == -1 || !paused || stopped) return;
        paused = false;
        onPause.accept(elapsedSeconds, false);
        Bukkit.getPluginManager().callEvent(new StopwatchResumedEvent(this, elapsedSeconds));
    }

    /** Stops the stopwatch and cancels the task. */
    public void stop() {
        if (taskId == -1 || stopped) return;
        stopped = true;
        cancelTask();
        onStop.accept(elapsedSeconds);
        Bukkit.getPluginManager().callEvent(new StopwatchStoppedEvent(this, elapsedSeconds));
    }

    /** Resets elapsed time to zero. Does not start the task. */
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
    public int getElapsedSeconds() { return elapsedSeconds; }
    public boolean isRunning() { return taskId != -1 && !paused && !stopped; }
    public boolean isPaused()  { return paused; }
    public boolean isStopped() { return stopped; }

    /** Utility: mm:ss formatting. */
    public static String formatHMS(int seconds) {
        int m = seconds / 60;
        int s = seconds % 60;
        return String.format("%02d:%02d", m, s);
    }
}