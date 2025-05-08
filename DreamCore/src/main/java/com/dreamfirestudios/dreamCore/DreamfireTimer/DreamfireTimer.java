package com.dreamfirestudios.dreamCore.DreamfireTimer;

import com.dreamfirestudios.dreamCore.DreamCore;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class DreamfireTimer {
    private int totalTime;
    private final Consumer<Integer> runningTimer;
    private final BiConsumer<Integer, Boolean> pausedTimer;
    private final Consumer<Integer> endedTimer;
    private boolean isPaused = false;
    private boolean isStopped = false;

    public DreamfireTimer(int totalTime, Consumer<Integer> runningTimer, BiConsumer<Integer, Boolean> pausedTimer, Consumer<Integer> endedTimer){
        this.totalTime = totalTime;
        this.runningTimer = runningTimer;
        this.pausedTimer = pausedTimer;
        this.endedTimer = endedTimer;
        DreamCore.GetDreamfireCore().dreamfireTimerArrayList.add(this);
    }

    public boolean HandleOnLoop(){
        if(isPaused) return false;
        totalTime -= 1;
        if(totalTime == 0 || isStopped){
            endedTimer.accept(totalTime);
            return true;
        }
        runningTimer.accept(totalTime);
        return false;
    }

    public void pauseTimer(){
        isPaused = true;
        pausedTimer.accept(totalTime, true);
    }

    public void playTimer(){
        isPaused = false;
        pausedTimer.accept(totalTime, false);
    }

    public void StopTimer(){
        isStopped = true;
    }
}
