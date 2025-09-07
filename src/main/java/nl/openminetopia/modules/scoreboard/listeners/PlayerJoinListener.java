package nl.openminetopia.modules.scoreboard.listeners;

import nl.openminetopia.api.player.PlayerManager;
import nl.openminetopia.api.player.ScoreboardManager;
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
            if (minetopiaPlayer == null) {
                player.kick(ChatUtils.color("<red>Er is een fout opgetreden bij het laden van je gegevens! Probeer het later opnieuw."));
                return;
            }

            minetopiaPlayer.setScoreboardVisible(true);
            ScoreboardManager.getInstance().addScoreboard(player);
        });
    }
}