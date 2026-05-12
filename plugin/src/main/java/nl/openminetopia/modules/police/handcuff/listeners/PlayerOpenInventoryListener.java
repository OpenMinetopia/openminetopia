package nl.openminetopia.modules.police.handcuff.listeners;

import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.modules.police.handcuff.HandcuffManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;

public class PlayerOpenInventoryListener implements Listener {

    @EventHandler
    public void inventoryOpen(final InventoryOpenEvent event) {
        if (!(event.getPlayer() instanceof Player player)) return;

        if (!HandcuffManager.getInstance().isHandcuffed(player)) return;

        if (!OpenMinetopia.getDefaultConfiguration().isHandcuffCanOpenInventory()) event.setCancelled(true);
    }
}
