package com.dreamfirestudios.dreamcore.DreamLoop;

public abstract class DreamAbstractLoop implements IDreamLoop {
    private int loopID = -1;

    @Override
    public void PassID(int id) {
        loopID = id;
    }

    @Override
    public int GetId() {
        return loopID;
    }
}