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
package com.dreamfirestudios.dreamcore.DreamHologram;

import com.dreamfirestudios.dreamcore.DreamChat.DreamMessageFormatter;
import com.dreamfirestudios.dreamcore.DreamChat.DreamMessageSettings;
import com.dreamfirestudios.dreamcore.DreamCore;
import com.dreamfirestudios.dreamcore.DreamJava.DreamClassID;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * A multi-line Adventure Component hologram backed by stacked ArmorStands.
 * <p>
 * Paper best practices:
 * <ul>
 *   <li>All entity spawn/mutation must occur on the server main thread.</li>
 *   <li>Custom names use Kyori Adventure {@link Component}.</li>
 *   <li>Hologram lines are configured as marker ArmorStands (no hitbox) and invisible.</li>
 * </ul>
 */
public class DreamHologram extends DreamClassID {

    /** Backing stands, index 0 is the top line. */
    private final List<ArmorStand> armorStands = new ArrayList<>();

    @Getter private String hologramName;
    @Getter private Location startLocation;

    @Getter private boolean visible = false;
    @Getter private boolean customNameVisible = true;
    @Getter private boolean useGravity = false;

    /**
     * Vertical gap (Y offset) between lines. Negative values place subsequent lines below the first.
     * Typical hologram spacing is around -0.25F to -0.5F.
     */
    @Getter private float gapBetweenLines = -0.5f;

    /**
     * Supplies the formatted Adventure text for a given line index.
     * The generator should be deterministic for a given index.
     */
    private Function<Integer, Component> lineGenerator;

    // -----------------------------------------------------------------------
    // Query
    // -----------------------------------------------------------------------

    /**
     * @param armorStand armor stand to test
     * @return true if the armor stand is owned by this hologram
     */
    public boolean isArmorStand(@NotNull ArmorStand armorStand) {
        return armorStands.contains(armorStand);
    }

    /**
     * @return current number of lines in this hologram
     */
    public int size() {
        return armorStands.size();
    }

    /**
     * Returns the Component name of a line.
     *
     * @param index zero-based index
     * @return line Component, or null if index out of bounds
     */
    @Nullable
    public Component line(int index) {
        if (index < 0 || index >= armorStands.size()) return null;
        return armorStands.get(index).customName();
    }

    // -----------------------------------------------------------------------
    // Mutations (MAIN THREAD)
    // -----------------------------------------------------------------------

    /**
     * Inserts a new line at the given index and spawns a configured ArmorStand.
     * Existing lines at or after the index are shifted down.
     *
     * @param index insertion index (0..size)
     * @throws IllegalArgumentException if index is negative or greater than size
     */
    public void addNewLine(int index) {
        ensureMainThread();
        if (index < 0 || index > armorStands.size())
            throw new IllegalArgumentException("Index out of bounds: " + index);
        if (startLocation == null) return;
        final World world = startLocation.getWorld();
        if (world == null) return;

        // Compute name via generator (null-safe)
        final Component name = formatLine(index);

        // Spawn and configure a new marker ArmorStand
        final Location spawnLoc = lineLocation(index);
        ArmorStand stand = world.spawn(spawnLoc, ArmorStand.class, configureArmorStand(name));

        // Insert into list and re-stack positions beneath it
        armorStands.add(index, stand);
        restackFrom(index);

        HologramAddLineEvent.fire(this, name);
        HologramUpdateEvent.fire(this);
    }

    /**
     * Updates the content (custom name) of an existing line.
     *
     * @param index line index to edit
     * @throws IllegalArgumentException if index invalid
     */
    public void editLine(int index) {
        ensureMainThread();
        if (index < 0 || index >= armorStands.size())
            throw new IllegalArgumentException("Invalid line index: " + index);

        final Component name = formatLine(index);
        ArmorStand stand = armorStands.get(index);
        stand.customName(name);
        stand.setCustomNameVisible(customNameVisible);

        HologramEditLineEvent.fire(this, index, name);
    }

    /**
     * Removes a line at the specified index and deletes its ArmorStand.
     * Re-stacks all lines below to close the gap.
     *
     * @param index index to remove
     */
    public void removeLine(int index) {
        ensureMainThread();
        if (index < 0 || index >= armorStands.size()) return;

        ArmorStand stand = armorStands.remove(index);
        stand.remove();

        restackFrom(index);
        HologramRemoveLineEvent.fire(this, index);
        HologramUpdateEvent.fire(this);
    }

    /**
     * Teleports all line ArmorStands to their correct stacked positions from top to bottom.
     * Call after moving {@link #startLocation} or changing {@link #gapBetweenLines}.
     */
    public void updateHologram() {
        ensureMainThread();
        restackFrom(0);
        HologramUpdateEvent.fire(this);
    }

    /**
     * Re-applies the line generator to all lines (useful for animated text).
     */
    public void displayNextFrame() {
        ensureMainThread();
        for (int i = 0; i < armorStands.size(); i++) {
            editLine(i);
        }
    }

    /**
     * Deletes the entire hologram and removes all ArmorStands.
     * Also unregisters this hologram from {@link DreamCore#DreamHolograms}.
     */
    public void deleteHologram() {
        ensureMainThread();
        for (ArmorStand stand : armorStands) {
            stand.remove();
        }
        armorStands.clear();
        HologramDeleteEvent.fire(this);
        DreamCore.DreamHolograms.remove(getClassID());
    }

    // -----------------------------------------------------------------------
    // Helpers
    // -----------------------------------------------------------------------

    private void restackFrom(int startIndex) {
        if (startLocation == null) return;
        for (int i = startIndex; i < armorStands.size(); i++) {
            ArmorStand stand = armorStands.get(i);
            stand.teleport(lineLocation(i));
            stand.setCustomNameVisible(customNameVisible);
            stand.setGravity(useGravity);
            stand.setInvisible(!visible);
        }
    }

    @NotNull
    private Location lineLocation(int index) {
        return startLocation.clone().add(0.0, index * gapBetweenLines, 0.0);
    }

    @NotNull
    private Component formatLine(int index) {
        Component generated = lineGenerator != null
                ? lineGenerator.apply(index)
                : Component.empty();
        return DreamMessageFormatter.format(generated, DreamMessageSettings.all());
    }

    @NotNull
    private Consumer<ArmorStand> configureArmorStand(Component name) {
        return stand -> {
            // "Marker" stands have no hitbox; pure visual line
            stand.setMarker(true);
            stand.setInvisible(!visible);
            stand.setGravity(useGravity);
            stand.setCustomNameVisible(customNameVisible);
            stand.customName(name);
            stand.setSmall(true);
            stand.setPersistent(true);
            // Optional polish:
            stand.setBasePlate(false);
            stand.setArms(false);
            stand.setCanMove(false); // Paper API
        };
    }

    private static void ensureMainThread() {
        if (!Bukkit.isPrimaryThread()) {
            throw new IllegalStateException("Hologram mutations must run on the server main thread.");
        }
    }

    // -----------------------------------------------------------------------
    // Builder
    // -----------------------------------------------------------------------

    /**
     * Fluent builder for {@link DreamHologram}. Use {@link #create(Location, Function)} to spawn.
     */
    public static class HologramBuilder {
        private String hologramName = UUID.randomUUID().toString();
        private boolean visible = false;
        private boolean customNameVisible = true;
        private boolean useGravity = false;
        private float gapBetweenLines = -0.5f;
        private int linesToAdd = 0;

        public HologramBuilder hologramName(@NotNull String hologramName) {
            this.hologramName = hologramName;
            return this;
        }

        public HologramBuilder lines(int linesToAdd) {
            if (linesToAdd < 0) throw new IllegalArgumentException("linesToAdd cannot be negative");
            this.linesToAdd = linesToAdd;
            return this;
        }

        public HologramBuilder visible(boolean visible) {
            this.visible = visible;
            return this;
        }

        public HologramBuilder customNameVisible(boolean customNameVisible) {
            this.customNameVisible = customNameVisible;
            return this;
        }

        public HologramBuilder useGravity(boolean useGravity) {
            this.useGravity = useGravity;
            return this;
        }

        public HologramBuilder gapBetweenLines(float gapBetweenLines) {
            this.gapBetweenLines = gapBetweenLines;
            return this;
        }

        /**
         * Creates and spawns a new hologram at the given location.
         *
         * @param location     top line location (world must be non-null)
         * @param lineGenerator supplies content for each line index as an Adventure Component
         * @return the created & registered {@link DreamHologram}
         */
        public DreamHologram create(@NotNull Location location,
                                    @NotNull Function<Integer, Component> lineGenerator) {
            ensureMainThread();
            World world = location.getWorld();
            if (world == null) throw new IllegalArgumentException("Location must have a world");

            DreamHologram hologram = new DreamHologram();
            hologram.hologramName = hologramName;
            hologram.startLocation = location.clone();
            hologram.visible = visible;
            hologram.customNameVisible = customNameVisible;
            hologram.useGravity = useGravity;
            hologram.gapBetweenLines = gapBetweenLines;
            hologram.lineGenerator = lineGenerator;

            for (int i = 0; i < linesToAdd; i++) {
                hologram.addNewLine(i);
            }

            DreamCore.DreamHolograms.put(hologram.getClassID(), hologram);
            HologramSpawnEvent.fire(hologram);
            return hologram;
        }
    }
}