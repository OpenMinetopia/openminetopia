package nl.openminetopia.modules.places.listeners;

import net.kyori.adventure.title.Title;
import nl.openminetopia.api.player.PlayerManager;
import nl.openminetopia.configuration.MessageConfiguration;
import nl.openminetopia.utils.ChatUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    @EventHandler
    public void playerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        PlayerManager.getInstance().getMinetopiaPlayer(player).whenComplete((minetopiaPlayer, throwable) -> {
            if (minetopiaPlayer == null) return;

            if (!minetopiaPlayer.isInPlace() || minetopiaPlayer.getPlace() == null) return;

            Title title = Title.title(
                    ChatUtils.format(minetopiaPlayer, MessageConfiguration.message("place_enter_title")),
                    ChatUtils.format(minetopiaPlayer, MessageConfiguration.message("place_enter_subtitle"))
            );
            player.showTitle(title);
        });
    }
}

