package com.dreamfirestudios.dreamcore.DreamLoop;

import com.dreamfirestudios.dreamcore.DreamCore;
import com.dreamfirestudios.dreamcore.DreamJava.PulseAutoRegister;
import lombok.Getter;

import java.util.UUID;

/**
 * Fires every 20 ticks (~1s). Good for heavier periodic work.
 */
@PulseAutoRegister
public class TwentyTickLoop implements IDreamLoop {

    @Getter private static final long startDelay = 0L;
    @Getter private static final long loopInterval = 20L; // FIXED: was 1L

    private int loopID = -1;

    @Override
    public UUID ReturnID() {
        return UUID.randomUUID();
    }

    @Override public long StartDelay() { return startDelay; }
    @Override public long LoopInterval() { return loopInterval; }

    @Override
    public void Start() {
        // Initialize second-based state if required
    }

    @Override
    public void Loop() {
        // Delegate to your plugin's 20-tick dispatcher
        DreamCore.DreamCore.TwentyTickClasses(); // FIXED: call the 20-tick hook
    }

    @Override
    public void End() {
        // Cleanup for second-based tasks if required
    }

    @Override public void PassID(int id) { this.loopID = id; }
    @Override public int GetId() { return this.loopID; }
}