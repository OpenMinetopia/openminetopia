package nl.openminetopia.modules.staff.admintool.listeners;

import nl.openminetopia.utils.PersistentDataUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerDropItemListener implements Listener {

    @EventHandler
    public void playerDropItem(final PlayerDropItemEvent event) {

        ItemStack item = event.getItemDrop().getItemStack();
        if (PersistentDataUtil.get(item, "openmt.admintool") != null) {
            event.getItemDrop().remove();
            event.getPlayer().getInventory().remove(item);
        }
    }
}
