package com.dreamfirestudios.dreamcore.DreamCam;

/// <summary>
/// Defines how the camera should orient itself during path playback.
/// </summary>
public enum LookAtType {
    /// <summary>Always looks at a fixed location.</summary>
    FixedFocus,

    /// <summary>Continuously looks at a moving entity.</summary>
    MovingFocus,

    /// <summary>No special focus; looks forward along the path.</summary>
    NoFocus
}