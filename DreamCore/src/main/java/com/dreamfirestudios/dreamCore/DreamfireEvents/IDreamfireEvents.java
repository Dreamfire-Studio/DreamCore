package com.dreamfirestudios.dreamCore.DreamfireEvents;

import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.WeatherType;
import org.bukkit.entity.Player;
import org.bukkit.event.block.*;
import org.bukkit.inventory.ItemStack;

public interface IDreamfireEvents {
    default boolean op(){return false;}
    default String[] perms(){return new String[]{};}
    default GameMode gameMode(){return null;}
    default Difficulty difficulty(){return null;}
    default WeatherType weatherType(){return null;}
    default String[] worlds(){return new String[]{};}
}
