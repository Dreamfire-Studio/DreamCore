package com.dreamfirestudios.dreamcore.DreamWorld;

/// <summary>
/// Common time-lock presets for setting world time.
/// </summary>
/// <remarks>
/// Values are in Minecraft tick-time within a day (0..23999).
/// </remarks>
public enum TimeLock {
    /// <summary>Exact start of day (0).</summary>
    Daytime(0),
    /// <summary>Morning (1000).</summary>
    Day(1000),
    /// <summary>Noon (6000).</summary>
    Noon(6000),
    /// <summary>Sunset (12000).</summary>
    Sunset(12000),
    /// <summary>Early night (13000).</summary>
    Nighttime(13000),
    /// <summary>Midnight (18000).</summary>
    Midnight(18000),
    /// <summary>Sunrise (23000).</summary>
    Sunrise(23000);

    /// <summary>World time value.</summary>
    public final long time;
    TimeLock(long time){ this.time = time; }
}