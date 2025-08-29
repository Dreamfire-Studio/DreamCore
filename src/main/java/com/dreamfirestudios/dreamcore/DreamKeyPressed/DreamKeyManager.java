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
package com.dreamfirestudios.dreamcore.DreamKeyPressed;

import com.dreamfirestudios.dreamcore.DreamConcurrent.DreamCooldownMap;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/// <summary>
/// Evaluates key patterns per player and fires listener callbacks.
/// </summary>
/// <remarks>
/// <para>
/// The manager consumes normalized inputs via <see cref="handleInput(Player, DreamPressedKeys, Instant)"/> and
/// matches them against active <see cref="IDreamKeyPatternSpec"/> instances registered for each player.
/// It supports both <c>InOrder</c> sequences and <c>AllAtOnce</c> multi-key activations, timing windows,
/// chord spreads, per-step and global conditions, and per-pattern cooldowns.
/// </para>
/// <para>
/// Patterns may be configured as <c>firstTimeOnly</c> (one-shot) or reusable. When a pattern succeeds or fails,
/// one-shot patterns are removed while reusable patterns are re-armed to their initial state.
/// </para>
/// <para>
/// Thread-safety: Uses concurrent collections for per-player attempt tracking. The typical usage
/// is to drive inputs from the Bukkit main thread. If called from other threads, ensure your listeners are thread-safe.
/// </para>
/// </remarks>
/// <example>
/// <para>
/// Basic setup:
/// <code>
/// // Build a pattern: SNEAK then within 250ms LEFT_CLICK, then a chord (RIGHT_CLICK + SWAP_HANDS) within 150ms.
/// var pattern = DreamKeyPatternBuilder.create()
///     .inOrder()
///     .totalTimeout(Duration.ofSeconds(4))
///     .resetOnFailure(true)
///     .firstTimeOnly(false)
///     .stepWithTiming(DreamPressedKeys.SNEAK, new DreamTimingDefault(Duration.ofMillis(250)))
///     .stepWithTiming(DreamPressedKeys.LEFT_CLICK, new DreamTimingDefault(Duration.ofMillis(250)))
///     .chordWithTiming(Set.of(DreamPressedKeys.RIGHT_CLICK, DreamPressedKeys.SWAP_HANDS),
///                      new DreamTimingDefault(Duration.ofMillis(150)))
///     .build();
///
/// // Example listener
/// class DashListener implements IDreamKeyPressed {
///     @Override public void ActionComplete() { /* grant short dash */ }
///     @Override public void PartialComplete(int stepIdx) { /* show progress */ }
///     @Override public void FailedAction() { /* optional feedback */ }
///     @Override public void TimeWindowUpdate(UUID playerId, Duration remaining, Duration total, int activeStepIdx) { /* HUD */ }
///     @Override public void OnCooldown(UUID playerId, Duration left) { /* show cooldown */ }
///     @Override public boolean worksInInventory() { return false; } // disable inside inventories
/// }
///
/// // Registration when a player joins (e.g., from DreamKeyBukkitAdapter#onJoin):
/// manager.register(player.getUniqueId(), pattern, new DashListener());
///
/// // Feeding inputs (typically handled by DreamKeyBukkitAdapter):
/// manager.handleInput(player, DreamPressedKeys.SNEAK, Instant.now());
/// manager.handleInput(player, DreamPressedKeys.LEFT_CLICK, Instant.now());
/// // ... later:
/// manager.handleInput(player, DreamPressedKeys.RIGHT_CLICK, Instant.now());
/// manager.handleInput(player, DreamPressedKeys.SWAP_HANDS, Instant.now());
/// </code>
/// </para>
/// <para>
/// Game loop maintenance:
/// <code>
/// // Optionally call each tick (20x/s) to expire windows and emit TimeWindowUpdate at ~10Hz
/// manager.tick();
/// </code>
/// </para>
/// </example>
public final class DreamKeyManager implements IDreamKeyManager {

    private final Map<UUID, List<Attempt>> attempts = new ConcurrentHashMap<>();
    private final java.util.function.Supplier<Instant> now;

    /// <summary>Per-player, per-pattern cooldowns.</summary>
    private final DreamCooldownMap<CooldownKey> cooldowns = new DreamCooldownMap<>();

    /// <summary>Create a manager using <see cref="Instant#now()"/> as the time source.</summary>
    public DreamKeyManager() { this(Instant::now); }

    /// <summary>Create a manager with a custom time supplier (useful for tests).</summary>
    /// <param name="clock">Supplier returning monotonic-ish instants.</param>
    public DreamKeyManager(java.util.function.Supplier<Instant> clock) {
        this.now = Objects.requireNonNull(clock, "clock");
    }

    /// <inheritdoc/>
    @Override
    public void register(UUID playerId, IDreamKeyPatternSpec pattern, IDreamKeyPressed listener) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(pattern, "pattern");
        Objects.requireNonNull(listener, "listener");
        attempts.computeIfAbsent(playerId, k -> new ArrayList<>())
                .add(new Attempt(playerId, pattern, listener, now.get()));
    }

    /// <inheritdoc/>
    @Override public void unregisterAll(UUID playerId) { attempts.remove(playerId); }

    /// <inheritdoc/>
    @Override
    public boolean unregister(UUID playerId, IDreamKeyPatternSpec pattern) {
        var list = attempts.get(playerId);
        if (list == null) return false;
        return list.removeIf(a -> a.pattern == pattern);
    }

    /// <inheritdoc/>
    @Override public void clear() { attempts.clear(); }

    /// <inheritdoc/>
    @Override
    public void handleInput(org.bukkit.entity.Player player, DreamPressedKeys key, java.time.Instant at) {
        var list = attempts.get(player.getUniqueId());
        if (list == null || list.isEmpty()) return;

        var snapshot = new java.util.ArrayList<>(list);
        for (var attempt : snapshot) {
            var cdLeft = cooldowns.remaining(new CooldownKey(attempt.playerId, attempt.pattern));
            if (!cdLeft.isZero() && !cdLeft.isNegative()) {
                attempt.listener.OnCooldown(attempt.playerId, cdLeft);
                continue;
            }

            if (!isInventoryAllowed(player, attempt)) continue;

            if (isTimedOut(attempt, at)) {
                fail(attempt);
                continue;
            }

            switch (attempt.pattern.pressedType()) {
                case InOrder   -> processInOrder(player, attempt, key, at);
                case AllAtOnce -> processAllAtOnce(player, attempt, key, at);
            }

            emitWindowUpdate(attempt, at);
        }
    }

    /// <inheritdoc/>
    @Override
    public void tick() {
        var nowTs = now.get();
        for (var entry : attempts.entrySet()) {
            var list = entry.getValue();
            var it = list.iterator();
            while (it.hasNext()) {
                var attempt = it.next();
                if (isTimedOut(attempt, nowTs)) {
                    fail(attempt);
                    continue;
                }
                // (Optional) cooldowns.clearExpired(); // lightweight maintenance if you want
                emitWindowUpdate(attempt, nowTs);
            }
        }
    }

    // ========================= Evaluation =========================

    /// <summary>Process an in-order step progression for a given key at time <paramref name="at"/>.</summary>
    private void processInOrder(Player player, Attempt a, DreamPressedKeys key, Instant at) {
        var idx = a.progressIndex;
        if (idx >= a.steps.size()) return;

        var step = a.steps.get(idx);
        if (!conditionsOk(player, a, step)) { onFailMaybeReset(a); return; }
        if (!timingOk(a, step, at))        { onFailMaybeReset(a); return; }

        if (step.kind() == DreamStepKind.SINGLE) {
            if (key != step.singleKey()) {
                if (a.pattern.resetOnFailure()) onFailMaybeReset(a);
                return;
            }
            stepCompleted(a, at);
        } else {
            a.chordBuffer.add(new TimedKey(key, at));
            if (isChordSatisfied(step, a.chordBuffer)) {
                stepCompleted(a, at);
                a.chordBuffer.clear();
            } else {
                pruneChordBuffer(a, step, at);
            }
        }
    }

    /// <summary>Process an all-at-once activation, collecting all keys inside a spread window.</summary>
    private void processAllAtOnce(Player player, Attempt a, DreamPressedKeys key, Instant at) {
        if (!a.allAtOnceInitialized) {
            a.allAtOnceInitialized = true;
            a.allAtOnceRequired = collectAllKeys(a.steps);
        }

        if (!conditionsOk(player, a, null)) { onFailMaybeReset(a); return; }

        a.chordBuffer.add(new TimedKey(key, at));

        if (isAllAtOnceSatisfied(a)) {
            complete(a);
        } else {
            pruneAllAtOnceBuffer(a, at);
        }
    }

    // ========================= Time / Conditions =========================

    /// <summary>Returns true if the attempt exceeded its pattern’s total timeout.</summary>
    private boolean isTimedOut(Attempt a, Instant at) {
        var total = a.pattern.totalTimeout();
        if (total == null) return false;
        return at.isAfter(a.started.plus(total));
    }

    /// <summary>Checks per-step timing constraints relative to the previous step time.</summary>
    private boolean timingOk(Attempt a, IDreamKeyStepSpec step, Instant at) {
        var t = step.timing();
        if (t == null) return true;
        if (a.progressIndex == 0) return true;

        var sincePrev = Duration.between(a.lastStepTime != null ? a.lastStepTime : a.started, at);
        var min = t.minDelayFromPrevious();
        var max = t.maxDelayFromPrevious();
        if (min != null && sincePrev.compareTo(min) < 0) return false;
        if (max != null && sincePrev.compareTo(max) > 0) return false;
        return true;
    }

    /// <summary>Heuristically determines whether a non-player inventory GUI is open.</summary>
    private boolean isInventoryGuiOpen(Player p) {
        var view = p.getOpenInventory();
        if (view == null || view.getTopInventory() == null) return false;
        var type = view.getTopInventory().getType();
        return type != org.bukkit.event.inventory.InventoryType.CRAFTING
                && type != org.bukkit.event.inventory.InventoryType.CREATIVE;
    }

    /// <summary>Checks if both pattern and listener allow processing when an inventory GUI is open.</summary>
    private boolean isInventoryAllowed(Player p, Attempt a) {
        boolean invGuiOpen = isInventoryGuiOpen(p);
        if (!a.pattern.worksInInventory() && invGuiOpen) return false;
        if (!a.listener.worksInInventory() && invGuiOpen) return false;
        return true;
    }

    /// <summary>Evaluates pattern/step conditions (location, posture, and hand constraints).</summary>
    private boolean conditionsOk(Player p, Attempt a, IDreamKeyStepSpec step) {
        var cond = (step != null && step.conditions() != null) ? step.conditions() : a.pattern.conditions();
        if (cond == null) return true;

        var locOk = cond.allowedLocations().isEmpty() || cond.allowedLocations().stream().anyMatch(loc -> switch (loc) {
            case ON_GROUND -> p.isOnGround();
            case IN_AIR    -> !p.isOnGround() && !p.isInWater();
            case IN_WATER  -> p.isInWater();
        });
        if (!locOk) return false;

        if (cond.requireSneaking()  && !p.isSneaking())  return false;
        if (cond.requireSprinting() && !p.isSprinting()) return false;

        if (cond.mainHand() != null && !handOk(p.getInventory().getItemInMainHand(), cond.mainHand())) return false;
        if (cond.offHand()  != null && !handOk(p.getInventory().getItemInOffHand(),  cond.offHand()))  return false;

        return true;
    }

    /// <summary>Validates a single hand against an <see cref="IDreamHandCondition"/>.</summary>
    private boolean handOk(ItemStack stack, IDreamHandCondition hc) {
        var present = stack != null && stack.getType() != Material.AIR;
        if (hc.requireItemPresent() && !present) return false;

        if (!hc.allowOnlyMaterials().isEmpty() && (stack == null || !hc.allowOnlyMaterials().contains(stack.getType())))
            return false;

        if (!hc.bannedMaterials().isEmpty() && stack != null && hc.bannedMaterials().contains(stack.getType()))
            return false;

        if (!hc.requireAnyOfMaterials().isEmpty() && (stack == null || !hc.requireAnyOfMaterials().contains(stack.getType())))
            return false;

        return true;
    }

    // ========================= Chords =========================

    /// <summary>
    /// Returns true if <paramref name="buffer"/> contains all keys from the step’s chord
    /// within its configured <c>maxChordSpread</c> window.
    /// </summary>
    private boolean isChordSatisfied(IDreamKeyStepSpec step, List<TimedKey> buffer) {
        var keysNeeded = step.chordKeys();
        if (keysNeeded == null || keysNeeded.isEmpty()) return false;
        var spread = step.timing() != null ? step.timing().maxChordSpread() : null;
        if (spread == null) spread = Duration.ofMillis(100);

        buffer.sort(Comparator.comparing(tk -> tk.at));
        for (int i = 0; i < buffer.size(); i++) {
            var start = buffer.get(i).at;
            var windowEnd = start.plus(spread);
            var seen = EnumSet.noneOf(DreamPressedKeys.class);
            for (int j = i; j < buffer.size(); j++) {
                var tk = buffer.get(j);
                if (tk.at.isAfter(windowEnd)) break;
                seen.add(tk.key);
                if (seen.containsAll(keysNeeded)) return true;
            }
        }
        return false;
    }

    /// <summary>Removes stale entries that can no longer form a valid chord window.</summary>
    private void pruneChordBuffer(Attempt a, IDreamKeyStepSpec step, Instant nowTs) {
        var spread = step.timing() != null ? step.timing().maxChordSpread() : Duration.ofMillis(100);
        var cutoff = nowTs.minus(spread.multipliedBy(2));
        a.chordBuffer.removeIf(tk -> tk.at.isBefore(cutoff));
    }

    /// <summary>Returns true if all required keys across all steps are seen within a derived spread window.</summary>
    private boolean isAllAtOnceSatisfied(Attempt a) {
        var spread = inferAllAtOnceSpread(a);
        var buf = a.chordBuffer;
        if (buf.isEmpty()) return false;

        buf.sort(Comparator.comparing(tk -> tk.at));
        for (int i = 0; i < buf.size(); i++) {
            var start = buf.get(i).at;
            var windowEnd = start.plus(spread);
            var seen = EnumSet.noneOf(DreamPressedKeys.class);
            for (int j = i; j < buf.size(); j++) {
                var tk = buf.get(j);
                if (tk.at.isAfter(windowEnd)) break;
                seen.add(tk.key);
                if (seen.containsAll(a.allAtOnceRequired)) return true;
            }
        }
        return false;
    }

    /// <summary>Prunes the global collection buffer for all-at-once activations.</summary>
    private void pruneAllAtOnceBuffer(Attempt a, Instant nowTs) {
        var spread = inferAllAtOnceSpread(a);
        var cutoff = nowTs.minus(spread.multipliedBy(2));
        a.chordBuffer.removeIf(tk -> tk.at.isBefore(cutoff));
    }

    /// <summary>
    /// Infers the all-at-once chord spread as the maximum of all step spreads (minimum 120ms),
    /// allowing patterns with mixed step definitions to work predictably.
    /// </summary>
    private Duration inferAllAtOnceSpread(Attempt a) {
        Duration max = Duration.ofMillis(120);
        for (var s : a.steps) {
            var t = s.timing();
            if (t != null && t.maxChordSpread() != null) {
                if (t.maxChordSpread().compareTo(max) > 0) max = t.maxChordSpread();
            }
        }
        return max;
    }

    /// <summary>Collects a flat set of all keys referenced by the pattern’s steps.</summary>
    private Set<DreamPressedKeys> collectAllKeys(List<IDreamKeyStepSpec> steps) {
        var set = EnumSet.noneOf(DreamPressedKeys.class);
        for (var s : steps) {
            if (s.kind() == DreamStepKind.SINGLE && s.singleKey() != null) set.add(s.singleKey());
            if (s.kind() == DreamStepKind.CHORD  && s.chordKeys()  != null) set.addAll(s.chordKeys());
        }
        return set;
    }

    // ========================= Progress / Callbacks =========================

    /// <summary>Marks the current step as complete and notifies the listener.</summary>
    private void stepCompleted(Attempt a, Instant at) {
        a.lastStepTime = at;
        a.progressIndex++;
        a.listener.PartialComplete(a.progressIndex - 1);

        if (a.progressIndex >= a.steps.size()) {
            complete(a);
        }
    }

    /// <summary>On success: set cooldown; if one-shot remove, else re-arm.</summary>
    private void complete(Attempt a) {
        try {
            a.listener.ActionComplete();

            var cd = a.pattern.cooldown();
            if (cd != null && !cd.isZero() && !cd.isNegative()) {
                cooldowns.set(new CooldownKey(a.playerId, a.pattern), cd);
            }
        } finally {
            if (a.pattern.firstTimeOnly()) {
                var list = attempts.get(a.playerId);
                if (list != null) list.remove(a);
            } else {
                rearm(a, now.get());
            }
        }
    }

    /// <summary>If the pattern is configured to reset on failure, fail and re-arm / remove.</summary>
    private void onFailMaybeReset(Attempt a) {
        if (a.pattern.resetOnFailure()) {
            fail(a);
        }
    }

    /// <summary>On failure: one-shot removes, reusable re-arms.</summary>
    private void fail(Attempt a) {
        try {
            a.listener.FailedAction();
        } finally {
            if (a.pattern.firstTimeOnly()) {
                var list = attempts.get(a.playerId);
                if (list != null) list.remove(a);
            } else {
                rearm(a, now.get());
            }
        }
    }

    /// <summary>Reset attempt internals for the next activation.</summary>
    private void rearm(Attempt a, Instant startAt) {
        a.progressIndex = 0;
        a.lastStepTime = null;
        a.chordBuffer.clear();
        a.lastWindowNotify = null;
        a.allAtOnceInitialized = false;
        a.allAtOnceRequired.clear();
        a.started = startAt;
    }

    /// <summary>
    /// Computes and emits the current time-left window to the listener at ~10Hz,
    /// unless the pattern is currently on cooldown.
    /// </summary>
    private void emitWindowUpdate(Attempt a, Instant nowTs) {
        if (a.lastWindowNotify != null && Duration.between(a.lastWindowNotify, nowTs).toMillis() < 100) return;

        // If on cooldown, don’t spam step windows.
        if (isOnCooldown(a)) return;

        Duration total, remaining;

        if (a.pattern.pressedType() == DreamPressedType.InOrder) {
            if (a.progressIndex >= a.steps.size()) return;
            var step = a.steps.get(a.progressIndex);
            var t = step.timing();
            if (t == null || t.maxDelayFromPrevious() == null) return;

            total = t.maxDelayFromPrevious();
            var anchor = (a.lastStepTime != null) ? a.lastStepTime : a.started;
            var elapsed = Duration.between(anchor, nowTs);
            remaining = total.minus(elapsed);
        } else {
            total = inferAllAtOnceSpread(a);
            if (a.chordBuffer.isEmpty()) return;
            var first = a.chordBuffer.stream().map(tk -> tk.at).min(Instant::compareTo).orElse(nowTs);
            var elapsed = Duration.between(first, nowTs);
            remaining = total.minus(elapsed);
        }

        if (remaining.isNegative()) remaining = Duration.ZERO;

        a.lastWindowNotify = nowTs;
        a.listener.TimeWindowUpdate(a.playerId, remaining, total,
                Math.min(a.progressIndex, Math.max(0, a.steps.size() - 1)));
    }

    // ========================= Data =========================

    /// <summary>Key used for cooldown map: per player + pattern instance.</summary>
    private record CooldownKey(UUID playerId, IDreamKeyPatternSpec pattern) {}

    /// <summary>Returns true if the attempt’s pattern currently has cooldown remaining.</summary>
    private boolean isOnCooldown(Attempt a) {
        return cooldowns.remaining(new CooldownKey(a.playerId, a.pattern)).isPositive();
    }

    /// <summary>Mutable attempt state for a player+pattern pair.</summary>
    private static final class Attempt {
        final UUID playerId;
        final IDreamKeyPatternSpec pattern;
        final IDreamKeyPressed listener;
        final List<IDreamKeyStepSpec> steps;

        /// <summary>Mutable so we can re-arm and restart the total window.</summary>
        Instant started;
        Instant lastStepTime;
        int progressIndex;

        final List<TimedKey> chordBuffer = new ArrayList<>();
        boolean allAtOnceInitialized = false;
        Set<DreamPressedKeys> allAtOnceRequired = EnumSet.noneOf(DreamPressedKeys.class);

        Instant lastWindowNotify;

        Attempt(UUID playerId, IDreamKeyPatternSpec pattern, IDreamKeyPressed listener, Instant started) {
            this.playerId = Objects.requireNonNull(playerId);
            this.pattern = Objects.requireNonNull(pattern);
            this.listener = Objects.requireNonNull(listener);
            this.steps   = List.copyOf(pattern.steps());
            this.started = Objects.requireNonNull(started);
        }
    }

    /// <summary>A single key observed at a given instant.</summary>
    private record TimedKey(DreamPressedKeys key, Instant at) {}
}