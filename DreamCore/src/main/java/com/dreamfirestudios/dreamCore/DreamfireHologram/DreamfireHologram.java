package com.dreamfirestudios.dreamCore.DreamfireHologram;

import com.dreamfirestudios.dreamCore.DreamCore;
import com.dreamfirestudios.dreamCore.DreamfireArmorStand.DreamfireArmorStand;
import com.dreamfirestudios.dreamCore.DreamfireChat.DreamfireMessage;
import com.dreamfirestudios.dreamCore.DreamfireHologram.Event.*;
import lombok.Getter;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

//TODO update to use text display
public class DreamfireHologram {
    private List<ArmorStand> armorStands = new ArrayList<>();
    @Getter private UUID hologramID;
    @Getter private String hologramName;
    @Getter private Location startLocation;
    @Getter private boolean isVisible = false;
    @Getter private boolean customNameVisible = true;
    @Getter private boolean useGravity = false;
    @Getter private float gapBetweenLines = -0.5f;
    private Function<Integer, String> lineGenerator;

    /**
     * Checks if a given armor stand is part of this hologram.
     *
     * @param armorStand The armor stand to check.
     * @return true if the armor stand is part of this hologram, false otherwise.
     */
    public boolean isArmorStand(ArmorStand armorStand) {
        return armorStands.contains(armorStand);
    }

    /**
     * Adds a new line to the hologram.
     *
     * @param index The index of the line to add.
     */
    public void addNewLine(int index) {
        if (index < 0) throw new IllegalArgumentException("Index cannot be negative");
        var currentLocation = startLocation.clone().add(0, armorStands.size() * gapBetweenLines, 0);
        if (currentLocation.getWorld() == null) return;
        var customLine = DreamfireMessage.formatMessage(lineGenerator.apply(index), null);
        new HologramAddLineEvent(this, PlainTextComponentSerializer.plainText().serialize(customLine));
        //armorStands.add(DreamfireArmorStand.spawnArmorStand(currentLocation, isVisible, customNameVisible, customLine, false, useGravity));
    }

    /**
     * Edits the line at a specific index.
     *
     * @param index The index of the line to edit.
     */
    @Deprecated
    public void editLine(int index) {
        if (index < 0 || index >= armorStands.size()) throw new IllegalArgumentException("Invalid line index");
        var customLine = DreamfireMessage.formatMessage(lineGenerator.apply(index), null);
        new HologramEditLineEvent(this, index, PlainTextComponentSerializer.plainText().serialize(customLine));
        armorStands.get(index).setCustomName(PlainTextComponentSerializer.plainText().serialize(customLine));
    }

    /**
     * Returns the custom name of the line at the specified index.
     *
     * @param index The index of the line.
     * @return The custom name of the line, or null if the index is invalid.
     */
    @Deprecated
    public String getLine(int index){
        if (index < 0 || index >= armorStands.size()) return null;
        return armorStands.get(index).getCustomName();
    }

    /**
     * Removes a line from the hologram at the specified index.
     *
     * @param index The index of the line to remove.
     */
    public void removeLine(int index) {
        if (index < 0 || index >= armorStands.size()) return;
        armorStands.get(index).remove();
        armorStands.remove(index);
        new HologramRemoveLineEvent(this, index);
        updateHologram();
    }

    /**
     * Deletes the entire hologram, removing all associated ArmorStands.
     */
    public void deleteHologram() {
        for (var i = 0; i < armorStands.size(); i++) {
            armorStands.get(i).remove();
        }
        armorStands.clear();
        new HologramDeleteEvent(this);
        DreamCore.GetDreamfireCore().DeleteDreamfireHologram(hologramID);
    }

    /**
     * Updates all lines in the hologram by teleporting ArmorStands to their new locations.
     */
    public void displayNextFrame(){
        for (int i = 0; i < armorStands.size(); i++) editLine(i);
    }

    /**
     * Updates the location of each armor stand in the hologram.
     */
    private void updateHologram() {
        for (int i = 0; i < armorStands.size(); i++) {
            armorStands.get(i).teleport(startLocation.clone().add(0, i * gapBetweenLines, 0));
        }
        new HologramUpdateEvent(this);
    }

    public static class HologramBuilder {
        private UUID hologramID = UUID.randomUUID();
        private String hologramName = UUID.randomUUID().toString();
        private boolean isVisible = false;
        private boolean customNameVisible = true;
        private boolean useGravity = false;
        private float gapBetweenLines = -0.5f;
        private int linesToAdd;

        /**
         * Sets the name of the hologram.
         *
         * @param hologramName The name of the hologram.
         * @return The builder instance.
         */
        public HologramBuilder hologramName(String hologramName) {
            this.hologramName = hologramName;
            return this;
        }

        /**
         * Sets the number of lines to add to the hologram.
         *
         * @param linesToAdd The number of lines.
         * @return The builder instance.
         */
        public HologramBuilder line(int linesToAdd) {
            this.linesToAdd = linesToAdd;
            return this;
        }

        /**
         * Sets the visibility of the hologram.
         *
         * @param isVisible true if the hologram should be visible.
         * @return The builder instance.
         */
        public HologramBuilder isVisible(boolean isVisible) {
            this.isVisible = isVisible;
            return this;
        }

        /**
         * Sets the visibility of the custom name for armor stands.
         *
         * @param customNameVisible true if custom names are visible.
         * @return The builder instance.
         */
        public HologramBuilder customNameVisible(boolean customNameVisible) {
            this.customNameVisible = customNameVisible;
            return this;
        }

        /**
         * Sets the gravity for the hologram armor stands.
         *
         * @param useGravity true if gravity should be applied to armor stands.
         * @return The builder instance.
         */
        public HologramBuilder useGravity(boolean useGravity) {
            this.useGravity = useGravity;
            return this;
        }

        /**
         * Sets the gap between lines in the hologram.
         *
         * @param gapBetweenLines The gap between lines.
         * @return The builder instance.
         */
        public HologramBuilder gapBetweenLines(float gapBetweenLines) {
            this.gapBetweenLines = gapBetweenLines;
            return this;
        }

        /**
         * Creates and returns the hologram.
         *
         * @param location The location to spawn the hologram.
         * @param lineGenerator A function to generate the content of each line.
         * @return The created DreamfireHologram.
         */
        public DreamfireHologram create(Location location, Function<Integer, String> lineGenerator) {
            var storedHologram = DreamCore.GetDreamfireCore().GetDreamfireHologram(hologramID);
            if(storedHologram != null){
                storedHologram.lineGenerator = lineGenerator;
                for(var i = 0; i < linesToAdd; i++) storedHologram.editLine(i);
                return storedHologram;
            }

            DreamfireHologram hologram = new DreamfireHologram();
            hologram.hologramName = hologramName;
            hologram.startLocation = location;
            hologram.isVisible = isVisible;
            hologram.customNameVisible = customNameVisible;
            hologram.useGravity = useGravity;
            hologram.gapBetweenLines = gapBetweenLines;
            hologram.lineGenerator = lineGenerator;
            for(var i = 0; i < linesToAdd; i++) hologram.addNewLine(i);

            new HologramSpawnEvent(hologram);
            return DreamCore.GetDreamfireCore().AddDreamfireHologram(hologramID, hologram);
        }

        public DreamfireHologram create(IDreamfireHologram iDreamfireHologram) {
            var storedHologram = DreamCore.GetDreamfireCore().GetDreamfireHologram(iDreamfireHologram.hologramID());
            if(storedHologram != null){
                storedHologram.lineGenerator = iDreamfireHologram.lineGenerator();
                for(var i = 0; i < linesToAdd; i++) storedHologram.editLine(i);
                return storedHologram;
            }

            DreamfireHologram hologram = new DreamfireHologram();
            hologram.hologramName = iDreamfireHologram.hologramName();
            hologram.startLocation = iDreamfireHologram.location();
            hologram.isVisible = iDreamfireHologram.isVisible();
            hologram.customNameVisible = iDreamfireHologram.customNameVisible();
            hologram.useGravity = iDreamfireHologram.useGravity();
            hologram.gapBetweenLines = iDreamfireHologram.gapBetweenLines();
            hologram.lineGenerator = iDreamfireHologram.lineGenerator();
            for(var i = 0; i < iDreamfireHologram.linesToAdd(); i++) hologram.addNewLine(i);

            new HologramSpawnEvent(hologram);
            return DreamCore.GetDreamfireCore().AddDreamfireHologram(iDreamfireHologram.hologramID(), hologram);
        }
    }
}
