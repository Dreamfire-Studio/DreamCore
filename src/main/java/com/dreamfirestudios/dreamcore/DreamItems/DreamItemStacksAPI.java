package com.dreamfirestudios.dreamcore.DreamItems;

import com.dreamfirestudios.dreamcore.DreamCore;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

public final class DreamItemStacksAPI {

    public static ItemStack GetItemStackBYID(JavaPlugin javaPlugin, String id) {
        var def = DreamCore.IDreamItemStacks.getOrDefault(id, null);
        return def == null ? null : DreamItemStacks.build(javaPlugin, def);
    }

    public static ItemStack GetITemStackByName(JavaPlugin javaPlugin, String itemName) {
        if (itemName == null || itemName.isBlank()) return null;
        Component probe = LegacyComponentSerializer.legacySection().deserialize(itemName);
        String probePlain = PlainTextComponentSerializer.plainText().serialize(probe);
        for (var def : DreamCore.IDreamItemStacks.values()) {
            if (def == null) continue;
            Component name = def.displayName();
            if (name == null) continue;
            String defPlain = PlainTextComponentSerializer.plainText().serialize(name);
            if (defPlain.equalsIgnoreCase(probePlain)) {
                return DreamItemStacks.build(javaPlugin, def);
            }
        }
        return null;
    }

    public static IDreamItemStack GetIDreamItemStackBYID(JavaPlugin javaPlugin, String id) {
        return DreamCore.IDreamItemStacks.getOrDefault(id, null);
    }

    public static IDreamItemStack GetIDreamItemStackByName(JavaPlugin javaPlugin, String itemName) {
        if (itemName == null || itemName.isBlank()) return null;
        Component probe = LegacyComponentSerializer.legacySection().deserialize(itemName);
        String probePlain = PlainTextComponentSerializer.plainText().serialize(probe);
        for (var def : DreamCore.IDreamItemStacks.values()) {
            if (def == null) continue;
            Component name = def.displayName();
            if (name == null) continue;
            String defPlain = PlainTextComponentSerializer.plainText().serialize(name);
            if (defPlain.equalsIgnoreCase(probePlain)) {
                return def;
            }
        }
        return null;
    }
}