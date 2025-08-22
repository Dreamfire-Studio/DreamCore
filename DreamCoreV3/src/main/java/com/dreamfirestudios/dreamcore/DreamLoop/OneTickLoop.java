package com.dreamfirestudios.dreamcore.DreamLoop;

import com.dreamfirestudios.dreamcore.DreamCore;
import com.dreamfirestudios.dreamcore.DreamJava.PulseAutoRegister;
import lombok.Getter;

import java.util.UUID;

/**
 * Fires every tick (1L). Useful for short, lightweight dispatchers.
 */
@PulseAutoRegister
public class OneTickLoop implements IDreamLoop {

    @Getter private static final long startDelay = 0L;
    @Getter private static final long loopInterval = 1L;

    private int loopID = -1;

    @Override
    public UUID ReturnID() {
        return UUID.randomUUID();
    }

    @Override public long StartDelay() { return startDelay; }
    @Override public long LoopInterval() { return loopInterval; }

    @Override
    public void Start() {
        // Initialize fast-tick state if required
    }

    @Override
    public void Loop() {
        // Delegate to your plugin's 1-tick dispatcher
        DreamCore.DreamCore.OneTickClasses();
    }

    @Override
    public void End() {
        // Cleanup for fast-tick components if required
    }

    @Override public void PassID(int id) { this.loopID = id; }
    @Override public int GetId() { return this.loopID; }
}