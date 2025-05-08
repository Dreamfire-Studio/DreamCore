package com.dreamfirestudios.dreamCore.DreamfireTimer;

import com.dreamfirestudios.dreamCore.DreamCore;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class DreamfireStopWatch {
    private int totalTime = 0;
    private final Consumer<Integer> runningStopwatch;
    private final BiConsumer<Integer, Boolean> pausedStopwatch;
    private final Consumer<Integer> stoppedStopwatch;
    private boolean isPaused = false;
    private boolean isStopped = false;

    public DreamfireStopWatch(Consumer<Integer> runningStopwatch,BiConsumer<Integer, Boolean> pausedStopwatch, Consumer<Integer> stoppedStopwatch){
        this.runningStopwatch = runningStopwatch;
        this.pausedStopwatch = pausedStopwatch;
        this.stoppedStopwatch = stoppedStopwatch;
        DreamCore.GetDreamfireCore().dreamfireStopWatchArrayList.add(this);
    }

    public boolean HandleOnLoop(){
        if(isPaused) return false;
        totalTime += 1;
        if(isStopped){
            stoppedStopwatch.accept(totalTime);
            return true;
        }
        runningStopwatch.accept(totalTime);
        return false;
    }

    public void pauseTimer(){
        isPaused = true;
        pausedStopwatch.accept(totalTime, true);
    }

    public void playTimer(){
        isPaused = false;
        pausedStopwatch.accept(totalTime, false);
    }

    public void StopTimer(){
        isStopped = true;
    }
}
