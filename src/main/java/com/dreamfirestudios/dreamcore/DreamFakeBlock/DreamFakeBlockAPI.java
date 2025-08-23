/*
 * MIT License
 *
 * Copyright (c) 2025 Dreamfire Studio
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.dreamfirestudios.dreamcore.DreamFakeBlock;

import com.dreamfirestudios.dreamcore.DreamCore;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

/**
 * <summary>
 * Static utility methods for creating, managing and querying fake blocks.
 * Relies on {@link DreamCore} as the central storage.
 * </summary>
 */
public class DreamFakeBlockAPI {

    /**
     * <summary>Create and register a new fake block with observers.</summary>
     *
     * <param name="id">Unique ID for storing the fake block.</param>
     * <param name="location">Location of the fake block.</param>
     * <param name="material">Material to render.</param>
     * <param name="players">Initial observers.</param>
     */
    public static void createFakeBlock(String id, Location location, Material material, Player... players){
        var fakeBlock = new DreamFakeBlock(location, material);
        for (var player : players) {
            fakeBlock.addObserver(player);
        }
        DreamCore.DreamFakeBlocks.put(id, fakeBlock);
        new FakeBlockCreatedEvent(fakeBlock, id);
    }

    /**
     * <summary>Remove a player from observing the fake block at a specific location.</summary>
     */
    public static void removePlayerFromFakeBlock(Player player, Location location){
        for (var fakeBlock : DreamCore.DreamFakeBlocks.values()){
            if (fakeBlock.isPlayerObservingAtLocation(player, location)) {
                fakeBlock.removeObserver(player);
            }
        }
    }

    /**
     * <summary>Remove a player from observing a fake block by ID.</summary>
     */
    public static void removePlayerFromFakeBlock(Player player, String id){
        var fakeBlock = DreamCore.DreamFakeBlocks.getOrDefault(id, null);
        if (fakeBlock != null) fakeBlock.removeObserver(player);
    }

    /**
     * <summary>Remove and clear a fake block by location.</summary>
     */
    public static void removeFakeBlock(Location location){
        for (var fakeBlock : DreamCore.DreamFakeBlocks.values()){
            if (fakeBlock.isLocation(location)) {
                fakeBlock.removeAllObservers();
            }
        }
    }

    /**
     * <summary>Remove and clear a fake block by ID.</summary>
     */
    public static void removeFakeBlock(String id){
        var fakeBlock = DreamCore.DreamFakeBlocks.getOrDefault(id, null);
        if (fakeBlock != null) fakeBlock.removeAllObservers();
    }

    /**
     * <summary>Returns the fake material a player sees at a location, or null if none.</summary>
     */
    public static Material returnMaterialForPlayer(Player player, Location location){
        for (var fakeBlock : DreamCore.DreamFakeBlocks.values()){
            if (fakeBlock.isPlayerObservingAtLocation(player, location)) {
                return fakeBlock.getMaterial();
            }
        }
        return null;
    }

    /**
     * <summary>Returns the fake material for a block ID, or null if not present.</summary>
     */
    public static Material returnMaterialForID(String id){
        var fakeBlock = DreamCore.DreamFakeBlocks.getOrDefault(id, null);
        return fakeBlock != null ? fakeBlock.getMaterial() : null;
    }
}