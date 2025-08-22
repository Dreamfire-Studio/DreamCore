package com.dreamfirestudios.dreamcore.DreamHead;

import com.dreamfirestudios.dreamcore.DreamCore;
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

/// <summary>
/// Utility class for creating and manipulating custom player heads in Minecraft.
/// </summary>
/// <remarks>
/// Provides helper methods to generate player heads from UUIDs, names, custom textures,
/// and apply display names. These methods are commonly used for GUIs, rewards, and cosmetics.
/// </remarks>
public class DreamHead {

    /// <summary>
    /// Checks if a string is a valid UUID.
    /// </summary>
    /// <param name="s">The string to validate.</param>
    /// <returns>True if the string is a valid UUID; otherwise false.</returns>
    /// <remarks>
    /// This method attempts to parse the string into a <see cref="UUID"/> object.
    /// </remarks>
    /// <example>
    /// <code>
    /// boolean valid = DreamHead.isUUID("550e8400-e29b-41d4-a716-446655440000");
    /// </code>
    /// </example>
    private static boolean isUUID(String s) {
        try {
            UUID.fromString(s);
            return true;
        } catch (IllegalArgumentException ex) {
            return false;
        }
    }

    /// <summary>
    /// Creates a player head item for the given offline player.
    /// </summary>
    /// <param name="player">The player whose head should be created.</param>
    /// <returns>
    /// An <see cref="ItemStack"/> representing the player's head, or null if the player is null.
    /// </returns>
    /// <remarks>
    /// The returned head will have the correct skin for the player.
    /// </remarks>
    /// <example>
    /// <code>
    /// ItemStack head = DreamHead.returnPlayerHead(Bukkit.getOfflinePlayer("Notch"));
    /// </code>
    /// </example>
    public static ItemStack returnPlayerHead(OfflinePlayer player) {
        if (player == null) return null;
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) skull.getItemMeta();
        if (meta == null) return skull;
        meta.setOwningPlayer(player);
        skull.setItemMeta(meta);
        return skull;
    }

    /// <summary>
    /// Creates a player head item from a UUID string.
    /// </summary>
    /// <param name="uuid">The UUID string of the player.</param>
    /// <returns>
    /// An <see cref="ItemStack"/> representing the player's head, or null if the UUID is invalid.
    /// </returns>
    /// <example>
    /// <code>
    /// ItemStack head = DreamHead.returnPlayerHead("550e8400-e29b-41d4-a716-446655440000");
    /// </code>
    /// </example>
    public static ItemStack returnPlayerHead(String uuid) {
        if (!isUUID(uuid)) return null;
        OfflinePlayer player = Bukkit.getOfflinePlayer(UUID.fromString(uuid));
        return returnPlayerHead(player);
    }

    /// <summary>
    /// Creates a custom player head with a given display name, amount, and texture URL.
    /// </summary>
    /// <param name="name">The display name of the head.</param>
    /// <param name="amount">The number of items in the stack.</param>
    /// <param name="url">
    /// The short texture hash (from Minecraft's skin servers) or a player name.
    /// If shorter than 16 characters, it is treated as a player name.
    /// </param>
    /// <returns>
    /// An <see cref="ItemStack"/> representing the custom head, or null if input is invalid.
    /// </returns>
    /// <remarks>
    /// If the <paramref name="url"/> is a short string, it will attempt to fetch
    /// a player head by name. Otherwise, it assumes a custom skin texture.
    /// </remarks>
    /// <example>
    /// <code>
    /// ItemStack head = DreamHead.returnPlayerHead("Custom Head", 1, "a1b2c3d4e5f6...");
    /// </code>
    /// </example>
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
                JavaPlugin.getPlugin(DreamCore.class).getLogger()
                        .log(Level.SEVERE, "Failed to apply custom profile to skull meta", e);
            }
        }

        meta.displayName(Component.text(name));
        skull.setItemMeta(meta);
        return skull;
    }

    /// <summary>
    /// Creates a player head with a custom texture from a URL.
    /// </summary>
    /// <param name="url">The texture URL or hash for the head skin.</param>
    /// <returns>
    /// An <see cref="ItemStack"/> with the custom texture applied, or null if invalid.
    /// </returns>
    /// <example>
    /// <code>
    /// ItemStack head = DreamHead.returnCustomTextureHead("a1b2c3d4e5f6...");
    /// </code>
    /// </example>
    public static ItemStack returnCustomTextureHead(String url) {
        if (url == null || url.isEmpty()) return null;
        return returnPlayerHead("", 1, url);
    }

    /// <summary>
    /// Applies a custom display name to an existing skull item.
    /// </summary>
    /// <param name="skull">The skull item to rename.</param>
    /// <param name="name">The new display name.</param>
    /// <returns>The same skull with the updated name.</returns>
    /// <example>
    /// <code>
    /// ItemStack head = DreamHead.applyCustomName(existingHead, "Legendary Head");
    /// </code>
    /// </example>
    public static ItemStack applyCustomName(ItemStack skull, String name) {
        if (skull == null || name == null) return skull;
        SkullMeta meta = skull.getItemMeta() instanceof SkullMeta sm ? sm : null;
        if (meta == null) return skull;
        meta.displayName(Component.text(name));
        skull.setItemMeta(meta);
        return skull;
    }

    /// <summary>
    /// Checks if the given item is a player head.
    /// </summary>
    /// <param name="item">The item to check.</param>
    /// <returns>True if the item is a player head; otherwise false.</returns>
    /// <example>
    /// <code>
    /// boolean isHead = DreamHead.isPlayerHead(item);
    /// </code>
    /// </example>
    public static boolean isPlayerHead(ItemStack item) {
        return item != null && item.getType() == Material.PLAYER_HEAD;
    }
}