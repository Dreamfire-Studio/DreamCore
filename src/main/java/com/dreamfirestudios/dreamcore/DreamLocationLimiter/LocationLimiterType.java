package com.dreamfirestudios.dreamcore.DreamLocationLimiter;

/// <summary>
/// Behavior of <see cref="DreamLocationLimiter"/> when players exceed the boundary.
/// </summary>
/// <remarks>
/// Determines whether players are teleported back to origin
/// or pushed back with velocity.
/// </remarks>
public enum LocationLimiterType {
    /// <summary>
    /// Push player back toward origin with velocity.
    /// </summary>
    PUSH_BACK,

    /// <summary>
    /// Instantly teleport the player back to origin.
    /// </summary>
    SNAP_TO_ORIGIN
}
