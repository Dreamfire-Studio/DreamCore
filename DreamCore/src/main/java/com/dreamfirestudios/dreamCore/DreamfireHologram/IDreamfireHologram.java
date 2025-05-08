package com.dreamfirestudios.dreamCore.DreamfireHologram;

import org.bukkit.Location;

import java.util.UUID;
import java.util.function.Function;

public interface IDreamfireHologram {
    default UUID hologramID(){return UUID.randomUUID();}
    default String hologramName(){return getClass().getSimpleName();}
    default boolean isVisible(){return false;}
    default boolean customNameVisible(){return false;}
    default boolean useGravity(){return false;}
    default float gapBetweenLines(){return -0.5f;}
    int linesToAdd();
    Location location();
    Function<Integer, String> lineGenerator();
}
