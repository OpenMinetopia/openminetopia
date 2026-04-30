package nl.openminetopia.modules.backpack.listeners;

import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.modules.backpack.item.BackpackItem;
import nl.openminetopia.modules.backpack.BackpackModule;
import nl.openminetopia.modules.backpack.session.BackpackSession;
import nl.openminetopia.modules.backpack.session.BackpackSessionManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class BackpackSessionListener implements Listener {

    private BackpackSessionManager manager() {
        BackpackModule module = OpenMinetopia.getModuleManager().get(BackpackModule.class);
        return module == null ? null : module.getSessionManager();
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        BackpackSessionManager mgr = manager();
        if (mgr == null) return;

        BackpackSession session = mgr.getSessionByViewer(player.getUniqueId());
        if (session == null) return;

        UUID openId = session.getBackpackId();

        if (matchesOpenBackpack(event.getCurrentItem(), openId) || matchesOpenBackpack(event.getCursor(), openId)) {
            event.setCancelled(true);
            return;
        }

        if (event.getAction() == InventoryAction.HOTBAR_SWAP || event.getClick().isKeyboardClick()) {
            int hotbar = event.getHotbarButton();
            if (hotbar >= 0) {
                ItemStack swapping = player.getInventory().getItem(hotbar);
                if (matchesOpenBackpack(swapping, openId)) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onDrag(InventoryDragEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        BackpackSessionManager mgr = manager();
        if (mgr == null) return;

        BackpackSession session = mgr.getSessionByViewer(player.getUniqueId());
        if (session == null) return;

        if (matchesOpenBackpack(event.getOldCursor(), session.getBackpackId())) {
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

    private boolean matchesOpenBackpack(ItemStack stack, UUID openId) {
        if (stack == null) return false;
        UUID id = BackpackItem.getId(stack);
        return openId.equals(id);
    }
}