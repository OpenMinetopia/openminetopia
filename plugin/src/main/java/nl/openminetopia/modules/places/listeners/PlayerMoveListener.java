package nl.openminetopia.modules.places.listeners;

import net.kyori.adventure.title.Title;
import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.api.places.MTPlaceManager;
import nl.openminetopia.api.places.objects.MTPlace;
import nl.openminetopia.api.player.PlayerManager;
import nl.openminetopia.api.player.objects.MinetopiaPlayer;
import nl.openminetopia.configuration.MessageConfiguration;
import nl.openminetopia.utils.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerMoveListener implements Listener {

    @EventHandler
    public void playerMove(PlayerMoveEvent event) {
        Bukkit.getScheduler().runTaskAsynchronously(OpenMinetopia.getInstance(), () -> {
            Player player = event.getPlayer();
            MinetopiaPlayer minetopiaPlayer = PlayerManager.getInstance().getOnlineMinetopiaPlayer(player);

            if (minetopiaPlayer == null) return;

            if (!minetopiaPlayer.isInPlace() || minetopiaPlayer.getPlace() == null) return;

            MTPlace from = MTPlaceManager.getInstance().getPlace(event.getFrom());
            if (from == null) return;

            MTPlace to = minetopiaPlayer.getPlace();
            if (to == null) return;

            if (from.equals(to)) return;

            Title title = Title.title(
                    ChatUtils.format(minetopiaPlayer, MessageConfiguration.message("place_enter_title")),
                    ChatUtils.format(minetopiaPlayer, MessageConfiguration.message("place_enter_subtitle"))
            );
            player.showTitle(title);
        });
    }
}
