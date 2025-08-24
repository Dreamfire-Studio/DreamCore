# DreamLoop — Developer Guide

The **DreamLoop** system gives you an easy way to define **repeating tasks** in your plugin. Instead of manually scheduling Bukkit tasks everywhere, you can implement `IDreamLoop` or extend built-in loop flavors (`OneTickLoop`, `TwentyTickLoop`) and let DreamCore handle registration.

---

## 🔁 What is a DreamLoop?

A DreamLoop is a **scheduled task** that runs at a fixed interval on the **main server thread**. It follows a simple lifecycle:

1. **Start()** → runs once before the loop begins.
2. **Loop()** → runs every interval.
3. **End()** → runs once when cancelled.

The loop is automatically registered when annotated with `@PulseAutoRegister` and discovered by `DreamClassAPI`.

---

## 📝 Defining a Custom Loop

You can implement `IDreamLoop` directly:

```java
@PulseAutoRegister
public final class BroadcastLoop implements IDreamLoop {
    private int taskId = -1;
    private final UUID loopID = UUID.randomUUID();

    @Override
    public UUID ReturnID() { return loopID; }
    @Override public long StartDelay() { return 0L; }
    @Override public long LoopInterval() { return 100L; } // 5 seconds

    @Override public void Start() {
        Bukkit.getLogger().info("Broadcast loop starting");
    }

    @Override public void Loop() {
        Bukkit.broadcastMessage("Hello every 5 seconds!");
    }

    @Override public void End() {
        Bukkit.getLogger().info("Broadcast loop ended");
    }

    @Override public void PassID(int id) { this.taskId = id; }
    @Override public int GetId() { return this.taskId; }
}
```

Once registered, DreamCore schedules and runs it automatically.

---

## ⚡ Built-in Loop Types

### `OneTickLoop`

* Fires **every server tick** (\~20 times per second).
* Useful for animations, HUD updates, or time-sensitive effects.

```java
@PulseAutoRegister
public final class HudTickLoop extends OneTickLoop {
    public void Loop() { updateAllHUDs(); }
}
```

### `TwentyTickLoop`

* Fires **every 20 ticks** (\~1 second).
* Perfect for periodic work like saving, polling, or simple heartbeats.

```java
@PulseAutoRegister
public final class HeartbeatLoop extends TwentyTickLoop {
    public void Loop() { Bukkit.getLogger().info("Heartbeat"); }
}
```

---

## 🛑 Cancelling Loops

Every loop can be stopped cleanly via `CancelLoop()`:

```java
myLoop.CancelLoop();
```

This will:

1. Cancel the Bukkit task.
2. Call `End()` for cleanup.
3. Remove the loop from `DreamCore.IDreamLoops`.

---

## ⚙️ IDreamLoop Interface

When implementing manually, you must define:

* `UUID ReturnID()` → unique identifier for registry.
* `long StartDelay()` → ticks before first execution.
* `long LoopInterval()` → ticks between executions.
* `void Start()` → one-time init.
* `void Loop()` → repeated logic.
* `void End()` → cleanup.
* `void PassID(int)` / `int GetId()` → track Bukkit task ID.

---

## ✅ Summary

As a plugin developer:

* Use **@PulseAutoRegister** to let DreamCore discover your loops.
* Extend **OneTickLoop** or **TwentyTickLoop** for common intervals.
* Implement **IDreamLoop** directly for custom intervals.
* Always keep `Loop()` lightweight to avoid lag.
* Use `CancelLoop()` to stop safely.

---

## 💡 Suggestions for Future Changes

* Add **async loop support** for background work.
* Provide **loop priority levels** (before/after core tasks).
* Allow **dynamic interval adjustment** at runtime.
* Add **metrics collection** for loop runtimes to catch slow tasks.
* Support **pausing/resuming** without full cancellation.