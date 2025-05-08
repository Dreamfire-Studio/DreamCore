package com.dreamfirestudios.dreamCore.DreamfirePlaceholder;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DreamfirePlaceholderManager extends PlaceholderExpansion {
    public ArrayList<IDreamfirePlaceholder> placeHolders = new ArrayList<>();

    public void RegisterInterface(IDreamfirePlaceholder pulsePlaceholder){
        placeHolders.add(pulsePlaceholder);
    }

    @Override
    public @NotNull String getIdentifier() { return "pulse_core"; }

    @Override
    public @NotNull String getAuthor() { return "PulseCore"; }

    @Override
    public @NotNull String getVersion() { return "1.0"; }

    @Override
    public boolean canRegister() { return true; }

    @Override
    public boolean persist() { return true; }

    @Override
    public @Nullable String onPlaceholderRequest(Player player, @NotNull String params) {
        if(player == null){ return ""; }
        for(var pulsePlaceholder : placeHolders){
            if(params.equals(pulsePlaceholder.ReturnKey())) return pulsePlaceholder.ReturnData(player);
        }
        return null;
    }
}
