package com.dreamfirestudios.dreamcore.DreamLoop;

import com.dreamfirestudios.dreamcore.DreamCore;
import org.bukkit.Bukkit;

public interface IDreamLoop {
    default String ReturnID(){ return getClass().getSimpleName();}
    default Long StartDelay(){return 0L;}
    default Long LoopInterval(){return 20L;}
    void Start();
    void Loop();
    void End();
    default void CancelLoop(){
        Bukkit.getScheduler().cancelTask(GetId());
        End();
        DreamCore.IDreamLoops.remove(ReturnID());
    }
    void PassID(int id);
    int GetId();
}
