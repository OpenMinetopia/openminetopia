package nl.openminetopia.modules.scoreboard.listeners;

import nl.openminetopia.api.player.PlayerManager;
import nl.openminetopia.api.player.ScoreboardManager;
import nl.openminetopia.configuration.MessageConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    @EventHandler
    public void playerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        PlayerManager.getInstance().getMinetopiaPlayer(player).whenComplete((minetopiaPlayer, throwable) -> {
            if (throwable != null) {
                player.kick(MessageConfiguration.component("player_data_not_loaded"));
                return;
            }
            if (minetopiaPlayer == null) {
                player.kick(MessageConfiguration.component("player_data_not_loaded"));
                return;
            }

            minetopiaPlayer.setScoreboardVisible(true);
            ScoreboardManager.getInstance().addScoreboard(player);
        });
    }
}