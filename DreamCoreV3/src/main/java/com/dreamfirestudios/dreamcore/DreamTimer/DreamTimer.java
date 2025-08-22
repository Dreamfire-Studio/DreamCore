package com.dreamfirestudios.dreamcore.DreamTimer;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * A countdown timer (counts down to zero).
 * All callbacks/events fire on the main thread.
 */
public final class DreamTimer {

    private final Plugin plugin;
    private final long periodTicks;
    private final Consumer<Integer> onTick;                 // remaining seconds
    private final BiConsumer<Integer, Boolean> onPause;     // (remaining, paused=true/false)
    private final Consumer<Integer> onFinish;               // final remaining (0 or below)

    private int taskId = -1;
    private int remainingSeconds;
    private boolean paused = false;
    private boolean finished = false;

    public DreamTimer(
            Plugin plugin,
            int startingSeconds,
            long periodTicks,
            Consumer<Integer> onTick,
            BiConsumer<Integer, Boolean> onPause,
            Consumer<Integer> onFinish
    ) {
        this.plugin = Objects.requireNonNull(plugin, "plugin");
        this.remainingSeconds = Math.max(0, startingSeconds);
        this.periodTicks = Math.max(1L, periodTicks);
        this.onTick = onTick == null ? s -> {} : onTick;
        this.onPause = onPause == null ? (s, p) -> {} : onPause;
        this.onFinish = onFinish == null ? s -> {} : onFinish;
    }

    /** Starts the countdown. No-op if already started. */
    public void start() {
        if (taskId != -1) return;
        finished = false;
        paused = false;
        taskId = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            if (finished || paused) return;

            // fire tick first (with current remaining)
            onTick.accept(remainingSeconds);
            Bukkit.getPluginManager().callEvent(new TimerTickEvent(this, remainingSeconds));

            // then decrement and test finish
            remainingSeconds--;
            if (remainingSeconds <= 0) {
                finish();
            }
        }, 0L, periodTicks).getTaskId();
        Bukkit.getPluginManager().callEvent(new TimerStartedEvent(this, remainingSeconds));
    }

    /** Pauses the timer. */
    public void pause() {
        if (taskId == -1 || paused || finished) return;
        paused = true;
        onPause.accept(remainingSeconds, true);
        Bukkit.getPluginManager().callEvent(new TimerPausedEvent(this, remainingSeconds));
    }

    /** Resumes a paused timer. */
    public void resume() {
        if (taskId == -1 || !paused || finished) return;
        paused = false;
        onPause.accept(remainingSeconds, false);
        Bukkit.getPluginManager().callEvent(new TimerResumedEvent(this, remainingSeconds));
    }

    /** Cancels the timer early (fires finish). */
    public void cancel() {
        if (taskId == -1 || finished) return;
        finish();
    }

    private void finish() {
        finished = true;
        cancelTask();
        onFinish.accept(Math.max(0, remainingSeconds));
        Bukkit.getPluginManager().callEvent(new TimerFinishedEvent(this, Math.max(0, remainingSeconds)));
    }

    private void cancelTask() {
        if (taskId != -1) {
            Bukkit.getScheduler().cancelTask(taskId);
            taskId = -1;
        }
    }

    // --- Accessors ---
    public int getRemainingSeconds() { return Math.max(0, remainingSeconds); }
    public boolean isRunning() { return taskId != -1 && !paused && !finished; }
    public boolean isPaused()  { return paused; }
    public boolean isFinished(){ return finished; }

    public static String formatHMS(int seconds) {
        int m = seconds / 60;
        int s = seconds % 60;
        return String.format("%02d:%02d", m, s);
    }
}