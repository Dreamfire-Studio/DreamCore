package com.dreamfirestudios.dreamCore.DreamfirePersistentData.Callbacks;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.*;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.BrewEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.*;

import java.util.ArrayList;
import java.util.List;

public interface IDreamfirePersistentEntityCallback {
    default List<String> BlockHasTags(){ return new ArrayList<>();}
    default EntityType EntityIsType(){return null;}

    default boolean BellResonateEvent(BellResonateEvent event, Entity entity){ return false; }
    default boolean BellRingEvent(BellRingEvent event, Entity entity){ return false; }
    default boolean BlockBreakEvent(BlockBreakEvent event, Entity entity){ return false; }
    default boolean BlockCanBuildEvent(BlockCanBuildEvent event, Entity entity){ return false; }
    default boolean BlockDamageEvent(BlockDamageEvent event, Entity entity){ return false; }
    default boolean BlockDamageAbortEvent(BlockDamageAbortEvent event, Entity entity){ return false; }
    default boolean BlockDispenseLootEvent(BlockDispenseLootEvent event, Entity entity){ return false; }
    default boolean BlockDropItemEvent(BlockDropItemEvent event, Entity entity){ return false; }
    default boolean BlockFertilizeEvent(BlockFertilizeEvent event, Entity entity){ return false; }
    default boolean BlockIgniteEvent(BlockIgniteEvent event, Entity entity){ return false; }
    default boolean BlockMultiPlaceEvent(BlockMultiPlaceEvent event, Entity entity){ return false; }
    default boolean BlockPlaceEvent(BlockPlaceEvent event, Entity entity){ return false; }
    default boolean BlockReceiveGameEvent(BlockReceiveGameEvent event, Entity entity){ return false; }
    default boolean BlockShearEntityEvent(BlockShearEntityEvent event, Entity entity){ return false; }
    default boolean EntityBlockFormEvent(EntityBlockFormEvent event, Entity entity){ return false; }
    default boolean SignChangeEvent(SignChangeEvent event, Entity entity){ return false; }
    default boolean TNTPrimeEvent(TNTPrimeEvent event, Entity entity){ return false; }
}
