package nl.openminetopia.modules.chat.listeners;

import nl.openminetopia.modules.chat.utils.SpyUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class PlayerCommandListener implements Listener {

    @EventHandler
    public void preCommand(final PlayerCommandPreprocessEvent event) {
        SpyUtils.commandSpy(event.getPlayer(), event.getMessage());
    }
}