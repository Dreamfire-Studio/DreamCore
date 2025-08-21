package com.dreamfirestudios.dreamcore.DreamJava;

import java.util.UUID;

public abstract class DreamClassID {
    public final UUID ClassID;

    protected DreamClassID(){
        ClassID = UUID.randomUUID();
    }

    public UUID getClassID() {
        return ClassID;
    }
}
