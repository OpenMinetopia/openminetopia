package nl.openminetopia.modules.police.handcuff.listeners;

import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.configuration.DefaultConfiguration;
import nl.openminetopia.modules.police.handcuff.HandcuffManager;
import nl.openminetopia.modules.police.handcuff.objects.HandcuffedPlayer;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.List;

public class PlayerQuitListener implements Listener {

    @EventHandler
    public void onQuit(final PlayerQuitEvent event) {
        Player player = event.getPlayer();
        HandcuffManager manager = HandcuffManager.getInstance();
        DefaultConfiguration configuration = OpenMinetopia.getDefaultConfiguration();

        if (configuration.isHandcuffTeleportOnLogout() && manager.isHandcuffed(player)) {
            Location location = configuration.getHandcuffLogoutLocation();
            if (location != null) player.teleport(location);
        }

        List<HandcuffedPlayer> affected = manager.getHandcuffedPlayers().stream()
                .filter(handcuffedPlayer -> handcuffedPlayer.getSource().equals(player)
                        || handcuffedPlayer.getPlayer().equals(player))
                .toList();

        affected.forEach(manager::release);
    }
}
