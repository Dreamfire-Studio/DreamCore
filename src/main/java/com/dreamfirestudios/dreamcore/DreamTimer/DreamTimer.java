// src/main/java/com/dreamfirestudios/dreamcore/DreamTimer/DreamTimer.java
package com.dreamfirestudios.dreamcore.DreamTimer;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/// <summary>
/// A countdown timer (counts down to zero) with pause/resume and Bukkit events.
/// </summary>
/// <remarks>
/// All callbacks and events fire on the main thread.
/// Use <see cref="start()"/> to begin, <see cref="pause()"/>, <see cref="resume()"/>, and <see cref="cancel()"/> to control.
/// Listen to <c>Timer*</c> events for lifecycle hooks.
/// </remarks>
/// <example>
/// <code>
/// DreamTimer timer = new DreamTimer(plugin, 10, 20L,
///     secs -&gt; player.sendActionBar(Component.text("Remaining: " + DreamTimer.formatHMS(secs))),
///     (secs, paused) -&gt; {},
///     secs -&gt; player.sendMessage("Done!")
/// );
/// timer.start();
/// </code>
/// </example>
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

    /// <summary>
    /// Constructs a countdown timer.
    /// </summary>
    /// <param name="plugin">Owning plugin.</param>
    /// <param name="startingSeconds">Starting seconds (negative values are clamped to 0).</param>
    /// <param name="periodTicks">Scheduler period in ticks (e.g., 20 = 1s updates).</param>
    /// <param name="onTick">Callback fired every period with remaining seconds (before decrement).</param>
    /// <param name="onPause">Callback fired on pause/resume with (remaining, paused=true/false).</param>
    /// <param name="onFinish">Callback fired when finished or cancelled (final remaining, clamped to 0).</param>
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

    /// <summary>
    /// Starts the countdown. No-op if already started.
    /// </summary>
    /// <remarks>
    /// Fires <see cref="TimerStartedEvent"/> immediately, then <see cref="TimerTickEvent"/> each period,
    /// and finally <see cref="TimerFinishedEvent"/> when it reaches zero.
    /// </remarks>
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

    /// <summary>Pauses the timer.</summary>
    /// <remarks>Fires <see cref="TimerPausedEvent"/> once; subsequent calls are ignored while paused.</remarks>
    public void pause() {
        if (taskId == -1 || paused || finished) return;
        paused = true;
        onPause.accept(remainingSeconds, true);
        Bukkit.getPluginManager().callEvent(new TimerPausedEvent(this, remainingSeconds));
    }

    /// <summary>Resumes a paused timer.</summary>
    /// <remarks>Fires <see cref="TimerResumedEvent"/>.</remarks>
    public void resume() {
        if (taskId == -1 || !paused || finished) return;
        paused = false;
        onPause.accept(remainingSeconds, false);
        Bukkit.getPluginManager().callEvent(new TimerResumedEvent(this, remainingSeconds));
    }

    /// <summary>
    /// Cancels the timer early (fires finish events and callbacks).
    /// </summary>
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

    /// <summary>Remaining seconds (never negative).</summary>
    /// <returns>Remaining whole seconds.</returns>
    public int getRemainingSeconds() { return Math.max(0, remainingSeconds); }

    /// <summary>True if the timer is currently running (started, not paused, not finished).</summary>
    public boolean isRunning() { return taskId != -1 && !paused && !finished; }

    /// <summary>True if the timer is paused.</summary>
    public boolean isPaused()  { return paused; }

    /// <summary>True if the timer has finished or was cancelled.</summary>
    public boolean isFinished(){ return finished; }

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