package nl.openminetopia.modules.police.handcuff.listeners;

import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.modules.police.handcuff.HandcuffManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;

public class PlayerPickupItemListener implements Listener {

    @EventHandler
    public void pickupItem(final EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        if (!HandcuffManager.getInstance().isHandcuffed(player)) return;

        if (!OpenMinetopia.getDefaultConfiguration().isHandcuffCanPickupItems()) event.setCancelled(true);
    }
}