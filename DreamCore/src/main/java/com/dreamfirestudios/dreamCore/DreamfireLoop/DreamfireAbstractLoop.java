package com.dreamfirestudios.dreamCore.DreamfireLoop;

public abstract class DreamfireAbstractLoop implements IDreamfireLoop{
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
