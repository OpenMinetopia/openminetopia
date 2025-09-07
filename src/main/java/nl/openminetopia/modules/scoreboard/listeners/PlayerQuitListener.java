package nl.openminetopia.modules.scoreboard.listeners;


import nl.openminetopia.api.player.ScoreboardManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener {


    @EventHandler
    public void playerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        ScoreboardManager.getInstance().removeScoreboard(player);
    }
}