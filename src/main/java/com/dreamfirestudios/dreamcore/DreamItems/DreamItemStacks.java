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
package com.dreamfirestudios.dreamcore.DreamItems;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import java.util.*;
import java.util.function.Function;

/// <summary>
/// Utilities for building, resolving, comparing, and counting custom items.
/// </summary>
/// <remarks>
/// This helper writes a stable ID (when provided by <see cref="IDreamItemStack.id()"/>)
/// into the item's PDC so you can reliably match items across sessions and restarts.
/// When no ID is present, it falls back to Bukkit's <c>isSimilar</c>.
/// </remarks>
/// <example>
/// <code>
/// // Define a custom item
/// class MySword implements IDreamItemStack {
///     public Optional&lt;String&gt; id() { return Optional.of("my_sword_v1"); }
///     public Material type() { return Material.DIAMOND_SWORD; }
///     public Component displayName() { return Component.text("§bAzure Edge"); }
/// }
///
/// // Build the item
/// ItemStack stack = DreamItemStacks.build(plugin, new MySword());
///
/// // Resolve by ID later
/// Optional&lt;IDreamItemStack&gt; def = DreamItemStacks.resolveById(
///     plugin, stack, id -&gt; id.equals("my_sword_v1") ? new MySword() : null
/// );
/// </code>
/// </example>
public final class DreamItemStacks {

    private DreamItemStacks() {}

    /// <summary>
    /// Generates the PDC key used to store the optional custom item ID.
    /// </summary>
    /// <param name="plugin">Owning plugin (for namespacing).</param>
    /// <returns>A <see cref="NamespacedKey"/> under <c>dream_item_id</c>.</returns>
    public static NamespacedKey keyId(Plugin plugin) {
        return new NamespacedKey(plugin, "dream_item_id");
    }

    /* --------------------------------------------------------------------- */
    /* Build                                                                  */
    /* --------------------------------------------------------------------- */

    /// <summary>
    /// Builds an <see cref="ItemStack"/> from a definition and writes its ID (if any) to PDC.
    /// </summary>
    /// <param name="plugin">Owning plugin used for PDC keys.</param>
    /// <param name="def">Item definition to materialize.</param>
    /// <returns>Newly built <see cref="ItemStack"/>.</returns>
    /// <remarks>
    /// Order of operations:
    /// name → lore → custom model data → unbreakable/flags → attributes → PDC → meta → enchantments.
    /// </remarks>
    /// <example>
    /// <code>
    /// ItemStack wand = DreamItemStacks.build(plugin, new MagicWandDef());
    /// </code>
    /// </example>
    public static ItemStack build(Plugin plugin, IDreamItemStack def) {
        Objects.requireNonNull(plugin, "plugin");
        Objects.requireNonNull(def, "definition");

        ItemStack stack = new ItemStack(def.type(), Math.max(1, def.amount()));
        ItemMeta meta = stack.getItemMeta();

        // Name
        Component name = def.displayName();
        if (name != null) meta.displayName(name);

        // Lore
        List<Component> lore = def.lore();
        if (lore != null && !lore.isEmpty()) meta.lore(lore);

        // Custom model data
        def.customModelData().ifPresent(meta::setCustomModelData);

        // Unbreakable, flags
        meta.setUnbreakable(def.unbreakable());
        Set<?> flags = def.flags();
        if (flags != null && !flags.isEmpty()) {
            meta.addItemFlags(def.flags().toArray(new org.bukkit.inventory.ItemFlag[0]));
        }

        // Attributes
        Map<?, ?> attrs = def.attributeModifiers();
        if (attrs != null && !attrs.isEmpty()) {
            def.attributeModifiers().forEach((attr, mods) -> {
                if (attr == null || mods == null) return;
                for (var mod : mods) if (mod != null) meta.addAttributeModifier(attr, mod);
            });
        }

        // PDC
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        String id = def.id();
        if (id != null && !id.isBlank()) {
            pdc.set(keyId(plugin), PersistentDataType.STRING, id);
        }
        def.writePdc(plugin, pdc);

        stack.setItemMeta(meta);

        // Enchantments last
        Map<Enchantment, Integer> ench = def.enchantments();
        if (ench != null && !ench.isEmpty()) {
            ench.forEach((e, lvl) -> {
                if (e != null && lvl != null && lvl > 0) stack.addUnsafeEnchantment(e, lvl);
            });
        }

        return stack;
    }

    /* --------------------------------------------------------------------- */
    /* Resolve                                                                */
    /* --------------------------------------------------------------------- */

    /// <summary>
    /// Reads the stored item ID from PDC, if present.
    /// </summary>
    /// <param name="plugin">Owning plugin used to construct the key.</param>
    /// <param name="stack">Item to inspect (AIR/null returns empty).</param>
    /// <returns>Optional ID string.</returns>
    /// <example>
    /// <code>
    /// Optional&lt;String&gt; id = DreamItemStacks.readId(plugin, stack);
    /// </code>
    /// </example>
    public static Optional<String> readId(Plugin plugin, ItemStack stack) {
        if (stack == null || stack.getType() == Material.AIR) return Optional.empty();
        ItemMeta meta = stack.getItemMeta();
        if (meta == null) return Optional.empty();
        String id = meta.getPersistentDataContainer().get(keyId(plugin), PersistentDataType.STRING);
        return Optional.ofNullable(id);
    }

    /// <summary>
    /// Resolves a definition by PDC ID using a registry lookup function.
    /// </summary>
    /// <param name="plugin">Owning plugin.</param>
    /// <param name="stack">Item to resolve.</param>
    /// <param name="registryLookup">Lookup function mapping ID -&gt; definition (nullable result allowed).</param>
    /// <returns>
    /// Optional <see cref="IDreamItemStack"/> if an ID exists and lookup returns non-null; otherwise empty.
    /// </returns>
    /// <typeparam name="T">Conceptually <see cref="IDreamItemStack"/>; included for doc parity.</typeparam>
    /// <example>
    /// <code>
    /// DreamItemStacks.resolveById(plugin, stack, id -&gt; REGISTRY.get(id));
    /// </code>
    /// </example>
    public static Optional<IDreamItemStack> resolveById(
            Plugin plugin,
            ItemStack stack,
            Function<String, IDreamItemStack> registryLookup
    ) {
        Objects.requireNonNull(registryLookup, "registryLookup");
        return readId(plugin, stack).map(registryLookup);
    }

    /// <summary>
    /// Resolves a definition by scanning a collection for a matching ID.
    /// </summary>
    /// <param name="plugin">Owning plugin.</param>
    /// <param name="stack">Item to resolve.</param>
    /// <param name="registry">Collection of candidate definitions.</param>
    /// <returns>Optional matching definition, or empty if not found / no ID.</returns>
    /// <example>
    /// <code>
    /// Optional&lt;IDreamItemStack&gt; def = DreamItemStacks.resolveById(plugin, stack, ALL_DEFS);
    /// </code>
    /// </example>
    public static Optional<IDreamItemStack> resolveById(Plugin plugin, ItemStack stack, Collection<IDreamItemStack> registry) {
        Optional<String> id = readId(plugin, stack);
        if (id.isEmpty()) return Optional.empty();
        String key = id.get();
        for (IDreamItemStack def : registry) {
            if (def == null) continue;
            String defId = def.id();
            if (defId != null && defId.equals(key)) {
                return Optional.of(def);
            }
        }
        return Optional.empty();
    }

    /* --------------------------------------------------------------------- */
    /* Equality / counting                                                    */
    /* --------------------------------------------------------------------- */

    /// <summary>
    /// Safer equality check for items:
    /// </summary>
    /// <remarks>
    /// (1) If both items have a PDC ID, compares the IDs.
    /// (2) Otherwise falls back to <c>ItemStack#isSimilar</c>.
    /// </remarks>
    /// <param name="plugin">Owning plugin.</param>
    /// <param name="a">First item.</param>
    /// <param name="b">Second item.</param>
    /// <returns>True if considered the same; otherwise false.</returns>
    /// <example>
    /// <code>
    /// boolean same = DreamItemStacks.isSame(plugin, a, b);
    /// </code>
    /// </example>
    public static boolean isSame(Plugin plugin, ItemStack a, ItemStack b) {
        if (a == b) return true;
        if (a == null || b == null) return false;

        Optional<String> idA = readId(plugin, a);
        Optional<String> idB = readId(plugin, b);
        if (idA.isPresent() && idB.isPresent()) {
            return idA.get().equals(idB.get());
        }
        return a.isSimilar(b);
    }

    /// <summary>
    /// Counts how many items equivalent to <paramref name="probe"/> exist in an inventory.
    /// </summary>
    /// <param name="plugin">Owning plugin.</param>
    /// <param name="inv">Inventory to scan.</param>
    /// <param name="probe">Probe item (by ID or similarity).</param>
    /// <returns>Total amount across all stacks.</returns>
    /// <example>
    /// <code>
    /// int apples = DreamItemStacks.count(plugin, player.getInventory(), new ItemStack(Material.APPLE));
    /// </code>
    /// </example>
    public static int count(Plugin plugin, Inventory inv, ItemStack probe) {
        Objects.requireNonNull(inv, "inventory");
        Objects.requireNonNull(probe, "probe");
        int total = 0;
        for (ItemStack s : inv.getContents()) {
            if (s == null) continue;
            if (isSame(plugin, s, probe)) total += s.getAmount();
        }
        return total;
    }

    /// <summary>
    /// Counts items by <see cref="Material"/> (null-safe).
    /// </summary>
    /// <param name="inv">Inventory to scan.</param>
    /// <param name="material">Material to match.</param>
    /// <returns>Total amount of the given material.</returns>
    /// <example>
    /// <code>
    /// int cobble = DreamItemStacks.count(player.getInventory(), Material.COBBLESTONE);
    /// </code>
    /// </example>
    public static int count(Inventory inv, Material material) {
        Objects.requireNonNull(inv, "inventory");
        Objects.requireNonNull(material, "material");
        int total = 0;
        for (ItemStack s : inv.getContents()) {
            if (s == null) continue;
            if (s.getType() == material) total += s.getAmount();
        }
        return total;
    }
}