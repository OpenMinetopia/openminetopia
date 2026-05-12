package nl.openminetopia.modules.police.handcuff.listeners;

import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.modules.police.handcuff.HandcuffManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;

public class PlayerDropItemListener implements Listener {

    @EventHandler
    public void dropItem(final PlayerDropItemEvent event) {
        if (!HandcuffManager.getInstance().isHandcuffed(event.getPlayer())) return;

        if (!OpenMinetopia.getDefaultConfiguration().isHandcuffCanDropItems()) event.setCancelled(true);
    }
}
