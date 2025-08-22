package com.dreamfirestudios.dreamcore.DreamWorld;

public enum TimeLock {
    Daytime(0),
    Day(1000),
    Noon(6000),
    Sunset(12000),
    Nighttime(13000),
    Midnight(18000),
    Sunrise(23000);

    public final long time;
    TimeLock(long time){ this.time = time; }
}