package com.dreamfirestudios.dreamCore.DreamfireHead;

import com.dreamfirestudios.dreamCore.DreamCore;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;
import java.util.logging.Level;

public class DreamfireHead {
    private static boolean isUUID(String s) {
        try {
            UUID.fromString(s);
            return true;
        } catch (IllegalArgumentException ex) {
            return false;
        }
    }

    public static ItemStack returnPlayerHead(OfflinePlayer player) {
        if (player == null) return null;
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) skull.getItemMeta();
        if (meta == null) return skull;
        meta.setOwningPlayer(player);
        skull.setItemMeta(meta);
        return skull;
    }

    public static ItemStack returnPlayerHead(String uuid) {
        if (!isUUID(uuid)) return null;
        OfflinePlayer player = Bukkit.getOfflinePlayer(UUID.fromString(uuid));
        return returnPlayerHead(player);
    }

    public static ItemStack returnPlayerHead(String name, int amount, String url) {
        if (name == null || url == null || amount <= 0) return null;
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD, amount);
        SkullMeta meta = (SkullMeta) skull.getItemMeta();
        if (meta == null) return skull;

        if (url.length() < 16) {
            OfflinePlayer owner = Bukkit.getOfflinePlayer(url);
            meta.setOwningPlayer(owner);
        } else {
            String fullUrl = "https://textures.minecraft.net/texture/" + url;
            GameProfile profile = new GameProfile(UUID.randomUUID(), null);
            String json = String.format("{\"textures\":{\"SKIN\":{\"url\":\"%s\"}}}", fullUrl);
            String encoded = Base64.getEncoder().encodeToString(json.getBytes(StandardCharsets.UTF_8));
            profile.getProperties().put("textures", new Property("textures", encoded));
            try {
                Field profileField = meta.getClass().getDeclaredField("profile");
                profileField.setAccessible(true);
                profileField.set(meta, profile);
            } catch (Exception e) {
                JavaPlugin.getPlugin(DreamCore.class).getLogger().log(Level.SEVERE, "Failed to apply custom profile to skull meta", e);
            }
        }

        meta.displayName(Component.text(name));
        skull.setItemMeta(meta);
        return skull;
    }

    public static ItemStack returnCustomTextureHead(String url) {
        if (url == null || url.isEmpty()) return null;
        return returnPlayerHead("", 1, url);
    }

    public static ItemStack applyCustomName(ItemStack skull, String name) {
        if (skull == null || name == null) return skull;
        SkullMeta meta = skull.getItemMeta() instanceof SkullMeta sm ? sm : null;
        if (meta == null) return skull;
        meta.displayName(Component.text(name));
        skull.setItemMeta(meta);
        return skull;
    }

    public static boolean isPlayerHead(ItemStack item) {
        return item != null && item.getType() == Material.PLAYER_HEAD;
    }
}