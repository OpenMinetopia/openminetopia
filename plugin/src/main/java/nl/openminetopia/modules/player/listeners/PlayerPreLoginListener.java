package nl.openminetopia.modules.player.listeners;

import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.api.player.PlayerManager;
import nl.openminetopia.api.player.objects.MinetopiaPlayer;
import nl.openminetopia.configuration.MessageConfiguration;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import java.util.concurrent.TimeUnit;

public class PlayerPreLoginListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void playerPreLogin(final AsyncPlayerPreLoginEvent event) {
        PlayerManager.getInstance().getOnlinePlayers().remove(event.getUniqueId());

        if (Bukkit.isPrimaryThread()) {
            OpenMinetopia.getInstance().getLogger().severe("PlayerPreLoginEvent is called on the main thread! This is not allowed!");
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, MessageConfiguration.component("player_data_not_loaded"));
            return;
        }

        long startTime = System.currentTimeMillis();
        OfflinePlayer player = Bukkit.getOfflinePlayer(event.getUniqueId());
        PlayerManager.getInstance().getMinetopiaPlayer(player).whenComplete((minetopiaPlayer, throwable) -> {
            if (throwable != null) {
                event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, MessageConfiguration.component("player_data_not_loaded"));
                OpenMinetopia.getInstance().getLogger().warning("Error loading player model: " + throwable.getMessage());
                throwable.printStackTrace();
                return;
            }
            if (minetopiaPlayer == null) {
                event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, MessageConfiguration.component("player_data_not_loaded"));
                OpenMinetopia.getInstance().getLogger().warning("Error loading player model: MinetopiaPlayer is null");
            }

            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            OpenMinetopia.getInstance().getLogger().info("Loaded player data for " + player.getName() + " (" + player.getUniqueId() + ") in " + duration + "ms");
        });
    }
}