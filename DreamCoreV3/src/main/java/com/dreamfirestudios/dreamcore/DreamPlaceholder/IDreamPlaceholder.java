package com.dreamfirestudios.dreamcore.DreamPlaceholder;

import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Implementations provide a single top-level key and a resolver that accepts optional args.
 *
 * <p>Examples of how PlaceholderAPI tokens map to this interface:</p>
 * <ul>
 *   <li>%dreamcore_balance%           -> key() = "balance", args = []</li>
 *   <li>%dreamcore_balance:gold%      -> key() = "balance", args = ["gold"]</li>
 *   <li>%dreamcore_balance_gold_bank% -> key() = "balance", args = ["gold","bank"]</li>
 * </ul>
 */
public interface IDreamPlaceholder {

    /**
     * The root key (case-insensitive). Must be unique within the manager.
     * e.g. "balance", "rank", "server_time".
     */
    @NotNull String key();

    /**
     * Resolve a placeholder value.
     *
     * @param player the associated player (may be null if context has no player)
     * @param args   optional arguments derived from the token (see examples above)
     * @return resolved string (never null). Return empty string if you have nothing to show.
     */
    @NotNull String resolve(@Nullable OfflinePlayer player, @NotNull String[] args);
}