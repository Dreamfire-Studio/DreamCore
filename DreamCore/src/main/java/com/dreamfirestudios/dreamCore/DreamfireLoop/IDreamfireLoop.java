package com.dreamfirestudios.dreamCore.DreamfireLoop;

import com.dreamfirestudios.dreamCore.DreamCore;
import org.bukkit.Bukkit;

public interface IDreamfireLoop {
    default String ReturnID(){ return getClass().getSimpleName();}
    default Long StartDelay(){return 0L;}
    default Long LoopInterval(){return 20L;}
    void Start();
    void Loop();
    void End();
    default void CancelLoop(){
        Bukkit.getScheduler().cancelTask(GetId());
        End();
        DreamCore.GetDreamfireCore().DeleteDreamfireLoop(ReturnID());
    }
    void PassID(int id);
    int GetId();
}
