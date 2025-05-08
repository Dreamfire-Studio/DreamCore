package com.dreamfirestudios.dreamCore.DreamfirePersistentData.Callbacks;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.*;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.BrewEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.server.ServerCommandEvent;

import java.util.ArrayList;
import java.util.List;

public interface IDreamfirePersistentBlockCallback {
    default List<String> BlockHasTags(){ return new ArrayList<>();}
    default Material BlockIsMaterial(){return null;}

    default boolean BellResonateEvent(BellResonateEvent event, Block block){ return false; }
    default boolean BellRingEvent(BellRingEvent event, Block block){ return false; }
    default boolean BlockBreakEvent(BlockBreakEvent event, Block block){ return false; }
    default boolean BlockBurnEvent(BlockBurnEvent event, Block block){ return false; }
    default boolean BlockCanBuildEvent(BlockCanBuildEvent event, Block block){ return false; }
    default boolean BlockCookEvent(BlockCookEvent event, Block block){ return false; }
    default boolean BlockDamageEvent(BlockDamageEvent event, Block block){ return false; }
    default boolean BlockDamageAbortEvent(BlockDamageAbortEvent event, Block block){ return false; }
    default boolean BlockDispenseEvent(BlockDamageAbortEvent event, Block block){ return false; }
    default boolean BlockDispenseArmorEvent(BlockDispenseArmorEvent event, Block block){ return false; }
    default boolean BlockDispenseLootEvent(BlockDispenseLootEvent event, Block block){ return false; }
    default boolean BlockDropItemEvent(BlockDropItemEvent event, Block block){ return false; }
    default boolean BlockExpEvent(BlockExpEvent event, Block block){ return false; }
    default boolean BlockExplodeEvent(BlockExplodeEvent event, Block block){ return false; }
    default boolean BlockFadeEvent(BlockFadeEvent event, Block block){ return false; }
    default boolean BlockFertilizeEvent(BlockFertilizeEvent event, Block block){ return false; }
    default boolean BlockFormEvent(BlockFormEvent event, Block block){ return false; }
    default boolean BlockGrowEvent(BlockGrowEvent event, Block block){ return false; }
    default boolean BlockIgniteEvent(BlockIgniteEvent event, Block block){ return false; }
    default boolean BlockMultiPlaceEvent(BlockMultiPlaceEvent event, Block block){ return false; }
    default boolean BlockPhysicsEvent(BlockPhysicsEvent event, Block block){ return false; }
    default boolean BlockPistonEvent(BlockPistonEvent event, Block block){ return false; }
    default boolean BlockPistonExtendEvent(BlockPistonExtendEvent event, Block block){ return false; }
    default boolean BlockPistonRetractEvent(BlockPistonRetractEvent event, Block block){ return false; }
    default boolean BlockPlaceEvent(BlockPlaceEvent event, Block block){ return false; }
    default boolean BlockReceiveGameEvent(BlockReceiveGameEvent event, Block block){ return false; }
    default boolean BlockRedstoneEvent(BlockRedstoneEvent event, Block block){ return false; }
    default boolean BlockShearEntityEvent(BlockShearEntityEvent event, Block block){ return false; }
    default boolean BlockSpreadEvent(BlockSpreadEvent event, Block block){ return false; }
    default boolean BrewingStartEvent(BrewingStartEvent event, Block block){ return false; }
    default boolean CampfireStartEvent(CampfireStartEvent event, Block block){ return false; }
    default boolean CrafterCraftEvent(CrafterCraftEvent event, Block block){ return false; }
    default boolean EntityBlockFormEvent(EntityBlockFormEvent event, Block block){ return false; }
    default boolean FluidLevelChangeEvent(FluidLevelChangeEvent event, Block block){ return false; }
    default boolean InventoryBlockStartEvent(InventoryBlockStartEvent event, Block block){ return false; }
    default boolean LeavesDecayEvent(LeavesDecayEvent event, Block block){ return false; }
    default boolean MoistureChangeEvent(MoistureChangeEvent event, Block block){ return false; }
    default boolean NotePlayEvent(NotePlayEvent event, Block block){ return false; }
    default boolean SculkBloomEvent(SculkBloomEvent event, Block block){ return false; }
    default boolean SignChangeEvent(SignChangeEvent event, Block block){ return false; }
    default boolean SpongeAbsorbEvent(SpongeAbsorbEvent event, Block block){ return false; }
    default boolean TNTPrimeEvent(TNTPrimeEvent event, Block block){ return false; }
}
