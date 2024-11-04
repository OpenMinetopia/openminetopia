package nl.openminetopia.modules.staff.admintool.listeners;

import nl.openminetopia.api.player.PlayerManager;
import nl.openminetopia.modules.staff.admintool.menus.AdminToolMenu;
import nl.openminetopia.utils.PersistentDataUtil;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerInteractListener implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        if (item == null) return;

        if (item.getType() != Material.NETHER_STAR) return;
        if (event.getAction().isRightClick()) return;
        if (PersistentDataUtil.get(item, "openmt.admintool") == null) return;

        PlayerManager.getInstance().getMinetopiaPlayerAsync(event.getPlayer(), minetopiaPlayer -> {
            if (minetopiaPlayer == null) return;
            new AdminToolMenu(event.getPlayer(), event.getPlayer(), minetopiaPlayer).open(event.getPlayer());
        }, Throwable::printStackTrace);

    }
}
