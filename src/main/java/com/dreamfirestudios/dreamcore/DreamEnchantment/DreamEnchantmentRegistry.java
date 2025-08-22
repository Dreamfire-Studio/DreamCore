package com.dreamfirestudios.dreamcore.DreamEnchantment;

import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 /// <summary>
 /// Plugin-side registry for Dreamfire enchantments.
 /// </summary>
 /// <remarks>
 /// This does <b>not</b> inject into Minecraft’s global registry (modern versions are data-pack driven).
 /// It guarantees singletons per key (wrapper-level) and provides lookup/utilities for plugin code.
 /// </remarks>
 */
public final class DreamEnchantmentRegistry {

    private DreamEnchantmentRegistry() {}

    /// <summary>
    /// Backing store for raw implementations keyed by <see cref="NamespacedKey"/>.
    /// </summary>
    private static final Map<NamespacedKey, IDreamEnchantment> ENCHANTS = new LinkedHashMap<>();

    /// <summary>
    /// Backing store for Bukkit/Paper <see cref="Enchantment"/> wrappers keyed by <see cref="NamespacedKey"/>.
    /// </summary>
    private static final Map<NamespacedKey, Enchantment> WRAPPERS = new LinkedHashMap<>();

    /**
     /// <summary>
     /// Registers (or fetches) an enchantment implementation and returns the Bukkit/Paper wrapper.
     /// </summary>
     /// <param name="impl">Implementation to register.</param>
     /// <returns>The canonical <see cref="Enchantment"/> wrapper for the given key.</returns>
     /// <remarks>
     /// If an enchantment with the same key already exists, that existing wrapper is returned.
     /// </remarks>
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

    /**
     /// <summary>
     /// Returns the registered Bukkit/Paper wrapper by key, or <c>null</c> if not present.
     /// </summary>
     /// <param name="key">Enchantment key.</param>
     /// <returns>The wrapper or <c>null</c>.</returns>
     */
    public static @Nullable Enchantment getWrapper(@NotNull NamespacedKey key) {
        return WRAPPERS.get(key);
    }

    /**
     /// <summary>
     /// Returns the registered implementation by key, or <c>null</c> if not present.
     /// </summary>
     /// <param name="key">Enchantment key.</param>
     /// <returns>The implementation or <c>null</c>.</returns>
     */
    public static @Nullable IDreamEnchantment get(@NotNull NamespacedKey key) {
        return ENCHANTS.get(key);
    }

    /**
     /// <summary>
     /// Immutable view of all registered wrappers.
     /// </summary>
     /// <returns>Unmodifiable collection of wrappers.</returns>
     */
    public static @NotNull Collection<Enchantment> allWrappers() {
        return Collections.unmodifiableCollection(WRAPPERS.values());
    }

    /**
     /// <summary>
     /// Immutable view of all registered implementations.
     /// </summary>
     /// <returns>Unmodifiable collection of implementations.</returns>
     */
    public static @NotNull Collection<IDreamEnchantment> all() {
        return Collections.unmodifiableCollection(ENCHANTS.values());
    }

    // ---------- item helpers ----------

    /**
     /// <summary>
     /// Finds all Dreamfire enchantments currently present on an item.
     /// </summary>
     /// <param name="stack">The item to inspect (may be null).</param>
     /// <returns>Unmodifiable list of matching enchantment implementations.</returns>
     /// <remarks>
     /// This inspects the item meta’s enchant map against each registered enchantment wrapper.
     /// </remarks>
     */
    public static @NotNull List<IDreamEnchantment> findOn(@Nullable ItemStack stack) {
        if (stack == null || stack.getItemMeta() == null) return List.of();
        var meta = stack.getItemMeta();
        var out = new ArrayList<IDreamEnchantment>();
        for (var impl : ENCHANTS.values()) {
            var ench = impl.returnEnchantment(); // NOTE: creates a new wrapper per call (see suggestion).
            if (meta.hasEnchant(ench)) out.add(impl);
        }
        return Collections.unmodifiableList(out);
    }

    /**
     /// <summary>
     /// Convenience helper: adds an enchantment by key at level 1, ignoring caps.
     /// </summary>
     /// <param name="stack">Target item stack.</param>
     /// <param name="key">Enchantment key.</param>
     /// <returns><c>true</c> if the item now has a non-zero level of this enchantment.</returns>
     */
    public static boolean add(@NotNull ItemStack stack, @NotNull NamespacedKey key) {
        var impl = ENCHANTS.get(key);
        return impl != null && impl.addToItem(stack);
    }
}