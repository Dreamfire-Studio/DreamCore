package com.dreamfirestudios.dreamCore.DreamfireSmartInvs;

import com.dreamfirestudios.dreamCore.DreamCore;
import com.dreamfirestudios.dreamCore.DreamfireSmartInvs.content.InventoryContents;
import com.dreamfirestudios.dreamCore.DreamfireSmartInvs.content.InventoryProvider;
import com.dreamfirestudios.dreamCore.DreamfireSmartInvs.opener.InventoryOpener;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class SmartInventory {
    private String id;
    private String title;
    private InventoryType type;
    private int rows, columns;
    private boolean closeable;
    private List<Integer> clickableTiles = new ArrayList<>();
    private boolean bottomClickable = false;

    private InventoryProvider provider;
    private SmartInventory parent;

    private List<InventoryListener<? extends Event>> listeners;
    private InventoryManager manager;

    private SmartInventory(InventoryManager manager) {
        this.manager = manager;
    }

    public CompletableFuture<Inventory> open(Player player) {
        return open(player, 0);
    }
    public CompletableFuture<Inventory> open(Player player, int page) {
        // Ensure the old inventory closure and cleanup runs first
        Optional<SmartInventory> oldInv = this.manager.getInventory(player);

        if (oldInv.isPresent()) {
            SmartInventory inv = oldInv.get();
            Bukkit.getScheduler().runTask(DreamCore.GetDreamfireCore(), () -> {
                inv.getListeners().stream()
                        .filter(listener -> listener.getType() == InventoryCloseEvent.class)
                        .forEach(listener -> ((InventoryListener<InventoryCloseEvent>) listener)
                                .accept(new InventoryCloseEvent(player.getOpenInventory())));

                this.manager.setInventory(player, null); // Clear the current inventory
            });
        }

        // Setup the new contents
        InventoryContents contents = new InventoryContents.Impl(this, player.getUniqueId());
        contents.pagination().page(page);

        this.manager.setContents(player, contents);

        // Continue with provider initialization and opening the inventory
        return this.provider.init(player, contents).thenApplyAsync(v -> {
            try {
                // Check if contents are consistent
                if (!this.manager.getContents(player).equals(Optional.of(contents))) {
                    return null;
                }

                // Open the new inventory
                InventoryOpener opener = this.manager.findOpener(type)
                        .orElseThrow(() -> new IllegalStateException("No opener found for the inventory type " + type.name()));
                Inventory handle = opener.open(this, player);

                this.manager.setInventory(player, this); // Set the new inventory
                return handle;
            } catch (Exception e) {
                this.manager.handleInventoryOpenError(this, player, e);
                return null;
            }
        });
    }

    @SuppressWarnings("unchecked")
    public void close(Player player) {
        Bukkit.getScheduler().runTask(DreamCore.GetDreamfireCore(), () -> {
            listeners.stream()
                    .filter(listener -> listener.getType() == InventoryCloseEvent.class)
                    .forEach(listener -> ((InventoryListener<InventoryCloseEvent>) listener)
                            .accept(new InventoryCloseEvent(player.getOpenInventory())));
            this.manager.setInventory(player, null);
            player.closeInventory();
            this.manager.setContents(player, null);
        });
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public InventoryType getType() { return type; }
    public int getRows() { return rows; }
    public int getColumns() { return columns; }
    public List<Integer> getClickableTiles(){return clickableTiles;}
    public boolean getBottomClickable(){return bottomClickable;}

    public boolean isCloseable() { return closeable; }
    public void setCloseable(boolean closeable) { this.closeable = closeable; }

    public InventoryProvider getProvider() { return provider; }
    public Optional<SmartInventory> getParent() { return Optional.ofNullable(parent); }

    public InventoryManager getManager() { return manager; }

    List<InventoryListener<? extends Event>> getListeners() { return listeners; }

    public static Builder builder() { return new Builder(); }

    public static final class Builder {

        private String id = "unknown";
        private String title = "";
        private InventoryType type = InventoryType.CHEST;
        private int rows = 6, columns = 9;
        private boolean closeable = true;
        private List<Integer> clickableTiles = new ArrayList<>();
        private boolean bottomClickable = false;

        private InventoryManager manager;
        private InventoryProvider provider;
        private SmartInventory parent;

        private List<InventoryListener<? extends Event>> listeners = new ArrayList<>();

        private Builder() {}

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder addClickable(int slot){
            clickableTiles.add(slot);
            return this;
        }

        public Builder addClickable(List<Integer> slot){
            clickableTiles.addAll(slot);
            return this;
        }

        public Builder bottomClickable(boolean clickable){
            bottomClickable = clickable;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder type(InventoryType type) {
            this.type = type;
            return this;
        }

        public Builder size(int rows, int columns) {
            this.rows = rows;
            this.columns = columns;
            return this;
        }

        public Builder closeable(boolean closeable) {
            this.closeable = closeable;
            return this;
        }

        public Builder provider(InventoryProvider provider) {
            this.provider = provider;
            return this;
        }

        public Builder parent(SmartInventory parent) {
            this.parent = parent;
            return this;
        }

        public Builder listener(InventoryListener<? extends Event> listener) {
            this.listeners.add(listener);
            return this;
        }

        public Builder manager(InventoryManager manager) {
            this.manager = manager;
            return this;
        }

        public SmartInventory build() {
            if(this.provider == null)
                throw new IllegalStateException("The provider of the SmartInventory.Builder must be set.");

            InventoryManager manager = this.manager != null ? this.manager : DreamCore.GetSmartInvsPlugin().manager();

            if(manager == null)
                throw new IllegalStateException("The manager of the SmartInventory.Builder must be set, "
                        + "or the SmartInvs should be loaded as a plugin.");

            SmartInventory inv = new SmartInventory(manager);
            inv.id = this.id;
            inv.title = this.title;
            inv.type = this.type;
            inv.rows = this.rows;
            inv.columns = this.columns;
            inv.closeable = this.closeable;
            inv.provider = this.provider;
            inv.parent = this.parent;
            inv.listeners = this.listeners;
            inv.clickableTiles = this.clickableTiles;
            inv.bottomClickable = this.bottomClickable;

            return inv;
        }
    }
}
