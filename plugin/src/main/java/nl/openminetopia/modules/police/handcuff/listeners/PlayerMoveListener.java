package nl.openminetopia.modules.police.handcuff.listeners;

import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.modules.police.handcuff.HandcuffManager;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerMoveListener implements Listener {

    @EventHandler
    public void playerMove(final PlayerMoveEvent event) {
        if (!HandcuffManager.getInstance().isHandcuffed(event.getPlayer())) return;
        if (OpenMinetopia.getDefaultConfiguration().isHandcuffCanRunAway()) return;

        Location from = event.getFrom();
        Location to = event.getTo();

        if (from.y() > to.y()) return;
        if (from.x() != to.x() || from.z() != to.z()) {
            event.setCancelled(true);
        }
    }
}
