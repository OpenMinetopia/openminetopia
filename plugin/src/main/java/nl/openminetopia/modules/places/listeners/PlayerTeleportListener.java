package nl.openminetopia.modules.places.listeners;

import net.kyori.adventure.title.Title;
import nl.openminetopia.api.places.MTPlaceManager;
import nl.openminetopia.api.places.objects.MTPlace;
import nl.openminetopia.api.player.PlayerManager;
import nl.openminetopia.api.player.objects.MinetopiaPlayer;
import nl.openminetopia.configuration.MessageConfiguration;
import nl.openminetopia.utils.ChatUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

public class PlayerTeleportListener implements Listener {

    @EventHandler
    public void playerTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        MinetopiaPlayer minetopiaPlayer = PlayerManager.getInstance().getOnlineMinetopiaPlayer(player);
        if (minetopiaPlayer == null) return;

        if (!minetopiaPlayer.isInPlace() || minetopiaPlayer.getPlace() == null) return;

        MTPlace from = MTPlaceManager.getInstance().getPlace(event.getFrom());
        MTPlace to = minetopiaPlayer.getPlace();

        if (from.equals(to)) return;

        Title title = Title.title(
                ChatUtils.format(minetopiaPlayer, MessageConfiguration.message("place_enter_title")),
                ChatUtils.format(minetopiaPlayer, MessageConfiguration.message("place_enter_subtitle"))
        );
        player.showTitle(title);
    }
}
