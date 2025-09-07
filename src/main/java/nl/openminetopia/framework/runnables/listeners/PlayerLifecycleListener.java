package nl.openminetopia.framework.runnables.listeners;

import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.framework.runnables.AbstractDirtyRunnable;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public class PlayerLifecycleListener implements Listener {

    private final OpenMinetopia openMinetopia;

    public PlayerLifecycleListener(OpenMinetopia plugin) {
        this.openMinetopia = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        for (AbstractDirtyRunnable<UUID> runnable: openMinetopia.getDirtyPlayerRunnables()) {
            if (runnable.getPolicy().dirtyOnJoin())
                Bukkit.getScheduler().runTaskLater(openMinetopia, () -> runnable.markDirty(uuid), runnable.getPolicy().initialDelayMs());
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        for (AbstractDirtyRunnable<UUID> runnable: openMinetopia.getDirtyPlayerRunnables()) runnable.remove(uuid);
    }
}
