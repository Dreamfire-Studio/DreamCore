package com.dreamfirestudios.dreamCore.DreamfireLoop;

import com.dreamfirestudios.dreamCore.DreamCore;
import com.dreamfirestudios.dreamCore.DreamfireJava.PulseAutoRegister;
import lombok.Getter;

@PulseAutoRegister
public class OneTickLoop implements IDreamfireLoop {
    @Getter
    private static final Long startDelay = 0L;
    @Getter
    private static final Long loopInterval = 1L;
    private int loopID = 0;

    @Override
    public Long StartDelay() { return startDelay; }

    @Override
    public Long LoopInterval() { return loopInterval; }

    @Override
    public void Start() {

    }

    @Override
    public void Loop() {
        DreamCore.GetDreamfireCore().OneTickClasses();
    }

    @Override
    public void End() {

    }

    @Override
    public void PassID(int id) {
        loopID = id;
    }

    @Override
    public int GetId() {
        return loopID;
    }
}
