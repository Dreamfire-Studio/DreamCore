package com.dreamfirestudios.dreamcore.DreamLoop;

import com.dreamfirestudios.dreamcore.DreamCore;
import org.bukkit.Bukkit;

import java.util.UUID;

/**
 * Contract for repeating loops driven by the Bukkit scheduler.
 * <p>
 * Lifecycle:
 * <ul>
 *   <li>{@link #Start()} is invoked once before the first run.</li>
 *   <li>{@link #Loop()} is invoked every {@link #LoopInterval()} ticks after the optional {@link #StartDelay()}.</li>
 *   <li>{@link #End()} is invoked once when the loop is cancelled.</li>
 * </ul>
 */
public interface IDreamLoop {

    /**
     * Identifier for this loop in the DreamCore registry. By default uses the simple class name.
     */
    UUID ReturnID();

    /**
     * Delay before the first {@link #Loop()} call, in ticks.
     */
    default long StartDelay() { return 0L; }

    /**
     * Interval between {@link #Loop()} calls, in ticks.
     */
    default long LoopInterval() { return 20L; }

    /** Called once before the scheduler begins invoking {@link #Loop()}. */
    void Start();

    /** Called on each tick interval. Do not block. Runs on the main thread. */
    void Loop();

    /** Called once after cancellation. Use to release resources. */
    void End();

    /**
     * Cancels this loop's scheduled task, calls {@link #End()}, and
     * unregisters it from {@code DreamCore.IDreamLoops}.
     */
    default void CancelLoop() {
        final int id = GetId();
        if (id >= 0) {
            Bukkit.getScheduler().cancelTask(id);
        }
        try {
            End();
        } finally {
            // Best-effort cleanup; guard against NPE if registry not initialized.
            try {
                DreamCore.IDreamLoops.remove(ReturnID());
            } catch (Throwable ignored) {
                // ignore registry cleanup errors to avoid masking real shutdown issues
            }
        }
    }

    /** Internal: set by the scheduler when registered. */
    void PassID(int id);

    /** Returns the Bukkit scheduler task id, or -1 if not scheduled. */
    int GetId();
}