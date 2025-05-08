package com.dreamfirestudios.dreamCore.DreamfirePersistentData.Callbacks;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.block.*;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public interface IDreamfirePersistentItemStackCallback {
    default List<String> ItemStackHasTags(){ return new ArrayList<>();}
    default Material ItemStackIsType(){return null;}

    default boolean BlockCookEvent(BlockCookEvent event, ItemStack itemStack){ return false; }
    default boolean BlockDamageEvent(BlockDamageEvent event, ItemStack itemStack){ return false; }
    default boolean BlockDamageAbortEvent(BlockDamageAbortEvent event, ItemStack itemStack){ return false; }
    default boolean BlockDispenseEvent(BlockDispenseEvent event, ItemStack itemStack){ return false; }
    default boolean BlockDispenseArmorEvent(BlockDispenseArmorEvent event, ItemStack itemStack){ return false; }
    default boolean BlockDispenseLootEvent(BlockDispenseLootEvent event, ItemStack itemStack){ return false; }
    default boolean BlockDropItemEvent(BlockDropItemEvent event, ItemStack itemStack){ return false; }
    default boolean BlockMultiPlaceEvent(BlockMultiPlaceEvent event, ItemStack itemStack){ return false; }
    default boolean BlockPlaceEvent(BlockPlaceEvent event, ItemStack itemStack){ return false; }
    default boolean BrewingStartEvent(BrewingStartEvent event, ItemStack itemStack){ return false; }
    default boolean CampfireStartEvent(CampfireStartEvent event, ItemStack itemStack){ return false; }
}
