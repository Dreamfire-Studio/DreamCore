package com.dreamfirestudios.dreamCore.DreamfireFakeBlock;

import com.dreamfirestudios.dreamCore.DreamCore;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class DreamfireFakeBlockAPI {
    public static void CreateFakeBlock(String id, Location location, Material material, Player... players){
        var fakeBlock = new DreamfireFakeBlock(location, material);
        for(var player : players) fakeBlock.addObserver(player);
        DreamCore.GetDreamfireCore().AddDreamfireFakeBlock(id, fakeBlock);
    }

    public static void RemovePlayerFromFakeBlock(Player player, Location location){
        for(var fakeBlock : DreamCore.GetDreamfireCore().GetDreamfireFakeBlocks()){
            if(!fakeBlock.isPlayerObservingAtLocation(player, location)) continue;
            fakeBlock.removeObserver(player);
        }
    }

    public static void RemovePlayerFromFakeBlock(Player player, String id){
        var fakeBlock = DreamCore.GetDreamfireCore().GetDreamfireFakeBlock(id);
        if(fakeBlock != null) fakeBlock.removeObserver(player);
    }

    public static void RemoveFakeBlock(Location location){
        for(var fakeBlock : DreamCore.GetDreamfireCore().GetDreamfireFakeBlocks()){
            if(!fakeBlock.isLocation(location)) continue;
            fakeBlock.removeAllObservers();
        }
    }

    public static void RemoveFakeBlock(String id){
        var fakeBlock = DreamCore.GetDreamfireCore().GetDreamfireFakeBlock(id);
        if(fakeBlock != null) fakeBlock.removeAllObservers();
    }

    public static Material ReturnMaterialForPlayer(Player player, Location location){
        for(var fakeBlock : DreamCore.GetDreamfireCore().GetDreamfireFakeBlocks()){
            if(!fakeBlock.isPlayerObservingAtLocation(player, location)) continue;
            return fakeBlock.getMaterial();
        }
        return null;
    }

    public static Material ReturnMaterialForID(String id){
        var fakeBlock = DreamCore.GetDreamfireCore().GetDreamfireFakeBlock(id);
        return fakeBlock != null ? fakeBlock.getMaterial() : null;
    }
}
