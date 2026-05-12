package nl.openminetopia.modules.police.handcuff.listeners;

import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.modules.police.handcuff.HandcuffManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class PlayerEntityDamageListener implements Listener {

    @EventHandler
    public void entityDamage(final EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player player)) return;

        if (!HandcuffManager.getInstance().isHandcuffed(player)) return;

        if (OpenMinetopia.getDefaultConfiguration().isHandcuffCanPvP()) event.setCancelled(true);
    }
}
