package com.dreamfirestudios.dreamcore.DreamEnchantment;

import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Plugin-side registry for Dreamfire enchantments.
 * This does NOT inject into the global Minecraft registry (which is data-pack driven in modern versions).
 * It guarantees singletons per key and provides lookup/utilities for your plugin code.
 */
public final class DreamEnchantmentRegistry {

    private DreamEnchantmentRegistry() {}

    private static final Map<NamespacedKey, IDreamEnchantment> ENCHANTS = new LinkedHashMap<>();
    private static final Map<NamespacedKey, Enchantment>       WRAPPERS = new LinkedHashMap<>();

    /**
     * Register or fetch an enchantment implementation.
     * If the key already exists, the existing instance is returned.
     */
    public static synchronized @NotNull Enchantment register(@NotNull IDreamEnchantment impl) {
        var key = impl.getKey();
        var existing = WRAPPERS.get(key);
        if (existing != null) return existing;

        ENCHANTS.put(key, impl);
        var wrapper = impl.returnEnchantment();
        WRAPPERS.put(key, wrapper);
        return wrapper;
    }

    /** Return the plugin wrapper by key, or null. */
    public static @Nullable Enchantment getWrapper(@NotNull NamespacedKey key) {
        return WRAPPERS.get(key);
    }

    /** Return the implementation by key, or null. */
    public static @Nullable IDreamEnchantment get(@NotNull NamespacedKey key) {
        return ENCHANTS.get(key);
    }

    /** Immutable view of registered wrappers. */
    public static @NotNull Collection<Enchantment> allWrappers() {
        return Collections.unmodifiableCollection(WRAPPERS.values());
    }

    /** Immutable view of implementations. */
    public static @NotNull Collection<IDreamEnchantment> all() {
        return Collections.unmodifiableCollection(ENCHANTS.values());
    }

    // ---------- item helpers ----------

    /** Returns all Dreamfire enchantments present on the item. */
    public static @NotNull List<IDreamEnchantment> findOn(@Nullable ItemStack stack) {
        if (stack == null || stack.getItemMeta() == null) return List.of();
        var meta = stack.getItemMeta();
        var out = new ArrayList<IDreamEnchantment>();
        for (var impl : ENCHANTS.values()) {
            var ench = impl.returnEnchantment();
            if (meta.hasEnchant(ench)) out.add(impl);
        }
        return Collections.unmodifiableList(out);
    }

    /** Convenience: add an enchantment by key (level 1, ignoring caps). */
    public static boolean add(@NotNull ItemStack stack, @NotNull NamespacedKey key) {
        var impl = ENCHANTS.get(key);
        return impl != null && impl.addToItem(stack);
    }
}