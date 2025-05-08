package com.dreamfirestudios.dreamCore.DreamfireEvents;

import com.dreamfirestudios.dreamCore.DreamCore;
import com.dreamfirestudios.dreamCore.DreamfireChat.DreamfireChat;
import com.dreamfirestudios.dreamCore.DreamfireEnchantment.DreamfireEnchantment;
import com.dreamfirestudios.dreamCore.DreamfireInventory.DreamfireInventory;
import com.dreamfirestudios.dreamCore.DreamfireItems.DreamfireItemStack;
import com.dreamfirestudios.dreamCore.DreamfirePersistentData.DreamfirePersistent;
import com.dreamfirestudios.dreamCore.DreamfirePlayer.DreamfirePlayerAction;
import com.dreamfirestudios.dreamCore.DreamfirePlayer.DreamfirePlayerActionAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.InvocationTargetException;
import java.util.UUID;

public class EventCheckUtils {

    public static boolean HandlePlayerAction(DreamfirePlayerAction dreamfirePlayerAction, UUID uuid, boolean isCancelled){
        var state = DreamfirePlayerActionAPI.CanPlayerAction(dreamfirePlayerAction, uuid);
        return isCancelled || !state;
    }

    public static <T extends Event> boolean HandleEntityCallbacks(T event, Entity entity, boolean isCancelled){
        for(var entityCallback : DreamCore.GetDreamfireCore().iDreamfirePersistentEntityCallbacks){
            if(DreamfirePersistent.CanCallback(entity, entityCallback)){
                try {
                    var method = entityCallback.getClass().getMethod(event.getClass().getSimpleName(), event.getClass(), Entity.class);
                    var state = (boolean) method.invoke(entityCallback, event, entity);
                    return isCancelled || state;
                } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                    var message = String.format("#ff2151No method (#ffffff%s#ff2151) in (#ffffff%s#ff2151)", event.getClass().getSimpleName(), entityCallback.getClass().getSimpleName());
                    DreamfireChat.SendMessageToConsole(message);
                }
            }
        }
        return isCancelled;
    }

    public static <T extends Event> boolean HandleBlockCallback(T event, Block block, boolean isCancelled){
        for(var blockCallback : DreamCore.GetDreamfireCore().iDreamfirePersistentBlockCallbacks){
            if(DreamfirePersistent.CanCallback(block, blockCallback)){
                try {
                    var method = blockCallback.getClass().getMethod(event.getClass().getSimpleName(), event.getClass(), Block.class);
                    var state = (boolean) method.invoke(blockCallback, event, block);
                    return isCancelled || state;
                } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                    var message = String.format("#ff2151No method (#ffffff%s#ff2151) in (#ffffff%s#ff2151)", event.getClass().getSimpleName(), blockCallback.getClass().getSimpleName());
                    DreamfireChat.SendMessageToConsole(message);
                }
            }
        }
        return isCancelled;
    }

    public static <T extends Event> boolean HandleItemStack(T event, LivingEntity livingEntity, boolean isCancelled){
        var playerInventoryItems = DreamfireInventory.returnAllItemsWithLocation(livingEntity);
        var selfCancel = false;
        for(var itemStack : playerInventoryItems.keySet()){
            if(itemStack == null || itemStack.getItemMeta() == null) continue;
            if(HandleItemStack(event, itemStack, isCancelled) && !selfCancel) selfCancel = true;
        }
        return isCancelled || selfCancel;
    }

    public static <T extends Event> boolean HandleItemStack(T event, ItemStack itemStack, boolean isCancelled){
        for(var itemStackCallback : DreamCore.GetDreamfireCore().iDreamfirePersistentItemStackCallbacks){
            if(DreamfirePersistent.CanCallback(itemStack, itemStackCallback)){
                try {
                    var method = itemStackCallback.getClass().getMethod(event.getClass().getSimpleName(), event.getClass(), ItemStack.class);
                    var state = (boolean) method.invoke(itemStackCallback, event, itemStack);
                    return isCancelled || state;
                } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                    var message = String.format("#ff2151No method (#ffffff%s#ff2151) in (#ffffff%s#ff2151)", event.getClass().getSimpleName(), itemStackCallback.getClass().getSimpleName());
                    DreamfireChat.SendMessageToConsole(message);
                }
            }
        }

        for(var dreamfireEnchantment : DreamfireEnchantment.ReturnAllCustomEnchantmentsFromItem(itemStack)){
            try {
                var method = dreamfireEnchantment.getClass().getMethod(event.getClass().getSimpleName(), event.getClass(), ItemStack.class);
                var state = (boolean) method.invoke(dreamfireEnchantment, event, itemStack);
                return isCancelled || state;
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                var message = String.format("#ff2151No method (#ffffff%s#ff2151) in (#ffffff%s#ff2151)", event.getClass().getSimpleName(), dreamfireEnchantment.getClass().getSimpleName());
                DreamfireChat.SendMessageToConsole(message);
            }
        }

        var pulseItemStack = DreamfireItemStack.ReturnPulseItem(itemStack);
        if(pulseItemStack != null){
            try {
                var method = pulseItemStack.getClass().getMethod(event.getClass().getSimpleName(), event.getClass(), ItemStack.class);
                var state = (boolean) method.invoke(pulseItemStack, event, itemStack);
                return isCancelled || state;
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                var message = String.format("#ff2151No method (#ffffff%s#ff2151) in (#ffffff%s#ff2151)", event.getClass().getSimpleName(), pulseItemStack.getClass().getSimpleName());
                DreamfireChat.SendMessageToConsole(message);
            }
        }

        return isCancelled;
    }

    public static <T extends Event> boolean HandleDreamfireEvents(T event, Entity entity, boolean isCancelled){
        for(var dreamfireEvent : DreamCore.GetDreamfireCore().iDreamfireEventsArrayList){
            if(DreamfireEvents.HandleDreamfireEvents(dreamfireEvent, entity)){
                try {
                    var method = dreamfireEvent.getClass().getMethod(event.getClass().getSimpleName(), event.getClass());
                    var state = (boolean) method.invoke(dreamfireEvent, event);
                    return isCancelled || state;
                } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                    var message = String.format("#ff2151No method (#ffffff%s#ff2151) in (#ffffff%s#ff2151)", event.getClass().getSimpleName(), dreamfireEvent.getClass().getSimpleName());
                    DreamfireChat.SendMessageToConsole(message);
                }
            }
        }
        return isCancelled;
    }

    public static <T extends Event> boolean HandleDreamfireEvents(T event, Location location, boolean isCancelled){
        for(var dreamfireEvent : DreamCore.GetDreamfireCore().iDreamfireEventsArrayList){
            if(DreamfireEvents.HandleDreamfireEvents(dreamfireEvent, location)){
                try {
                    var method = dreamfireEvent.getClass().getMethod(event.getClass().getSimpleName(), event.getClass());
                    var state = (boolean) method.invoke(dreamfireEvent, event);
                    return isCancelled || state;
                } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                    var message = String.format("#ff2151No method (#ffffff%s#ff2151) in (#ffffff%s#ff2151)", event.getClass().getSimpleName(), dreamfireEvent.getClass().getSimpleName());
                    DreamfireChat.SendMessageToConsole(message);
                }
            }
        }
        return isCancelled;
    }

    public static boolean HandleDreamfireWorldPlayerAction(DreamfirePlayerAction dreamfirePlayerAction, World world, boolean isCancelled){
        var dreamfireWorld = DreamCore.GetDreamfireCore().GetDreamfireWorld(world.getUID());
        if(dreamfireWorld == null) return isCancelled;
        return isCancelled || dreamfireWorld.IsActionLocked(dreamfirePlayerAction);
    }
}
