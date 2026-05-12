package nl.openminetopia.modules.backpack.listeners;

import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.modules.backpack.item.BackpackItem;
import nl.openminetopia.modules.backpack.BackpackModule;
import nl.openminetopia.modules.backpack.session.BackpackSession;
import nl.openminetopia.modules.backpack.session.BackpackSessionManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.UUID;

public class BackpackSessionListener implements Listener {

    private BackpackSessionManager manager() {
        BackpackModule module = OpenMinetopia.getModuleManager().get(BackpackModule.class);
        return module == null ? null : module.getSessionManager();
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onClick(InventoryClickEvent event) {
        BackpackSessionManager mgr = manager();
        if (mgr == null) return;

        // Don't let anyone move a bag that's currently open.
        if (matchesAnyOpen(event.getCurrentItem(), mgr) || matchesAnyOpen(event.getCursor(), mgr)) {
            event.setCancelled(true);
            return;
        }

        if (!(event.getWhoClicked() instanceof Player player)) return;

        // Block 1-9 hotbar swaps that would yoink an open bag out of its slot.
        if (event.getAction() == InventoryAction.HOTBAR_SWAP || event.getClick().isKeyboardClick()) {
            int hotbar = event.getHotbarButton();
            if (hotbar >= 0) {
                ItemStack swapping = player.getInventory().getItem(hotbar);
                if (matchesAnyOpen(swapping, mgr)) {
                    event.setCancelled(true);
                }
            }
        }
    }

    // Right-click a backpack inside another player's inventory (e.g. during /bodysearch) to open it.
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onSearchClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player viewer)) return;

        Inventory clicked = event.getClickedInventory();
        if (!(clicked instanceof PlayerInventory pi)) return;

        HumanEntity holder = pi.getHolder();
        if (holder == null || holder.getUniqueId().equals(viewer.getUniqueId())) return;

        ClickType click = event.getClick();
        if (click != ClickType.RIGHT && click != ClickType.SHIFT_RIGHT) return;

        ItemStack item = event.getCurrentItem();
        if (!BackpackItem.isBackpack(item)) return;

        event.setCancelled(true);

        BackpackSessionManager mgr = manager();
        if (mgr == null) return;

        Bukkit.getScheduler().runTask(OpenMinetopia.getInstance(), () -> {
            viewer.closeInventory();
            mgr.open(viewer, item);
        });
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onDrag(InventoryDragEvent event) {
        BackpackSessionManager mgr = manager();
        if (mgr == null) return;

        if (matchesAnyOpen(event.getOldCursor(), mgr)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onDrop(PlayerDropItemEvent event) {
        BackpackSessionManager mgr = manager();
        if (mgr == null) return;

        ItemStack stack = event.getItemDrop().getItemStack();
        UUID id = BackpackItem.getId(stack);
        if (id == null) return;

        if (mgr.isOpen(id)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPickup(EntityPickupItemEvent event) {
        BackpackSessionManager mgr = manager();
        if (mgr == null) return;

        UUID id = BackpackItem.getId(event.getItem().getItemStack());
        if (id == null) return;

        if (mgr.isOpen(id)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        BackpackSessionManager mgr = manager();
        if (mgr == null) return;

        BackpackSession session = mgr.getSessionByViewer(event.getPlayer().getUniqueId());
        if (session == null) return;

        mgr.saveSnapshot(session);
        mgr.close(session);
    }

    private boolean matchesAnyOpen(ItemStack stack, BackpackSessionManager mgr) {
        if (stack == null) return false;
        UUID id = BackpackItem.getId(stack);
        return id != null && mgr.isOpen(id);
    }
}