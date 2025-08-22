package com.dreamfirestudios.dreamcore.DreamBlock;

/// <summary>
/// Enumeration of geometric region shapes used in block operations.
/// </summary>
/// <remarks>
/// <para>
/// The selected shape determines how positions within a given radius around a center location are
/// considered for filtering, counting, replacing, or clearing blocks.
/// </para>
/// </remarks>
public enum RegionShape {
    /// <summary>
    /// A cubic region defined by extending equally in the X, Y, and Z axes.
    /// </summary>
    CUBE,

    /// <summary>
    /// A spherical region defined by all positions within a given radius
    /// from the center point, using squared-distance checks.
    /// </summary>
    SPHERE
}