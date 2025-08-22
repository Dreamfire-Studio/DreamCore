/* ============================================================================
 * [COPYRIGHT HEADER PLACEHOLDER]
 * Replace this block with the ChaosGalaxyTCG/Dreamfire standard header.
 * ============================================================================
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
