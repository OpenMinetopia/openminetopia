package nl.openminetopia.modules.fitness.listeners;

import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.api.player.PlayerManager;
import nl.openminetopia.api.player.objects.MinetopiaPlayer;
import nl.openminetopia.modules.places.PlacesModule;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;

public class PlayerChangeWorldListener implements Listener {

    @EventHandler
    public void worldChange(final PlayerChangedWorldEvent event) {
        PlacesModule placesModule = OpenMinetopia.getModuleManager().get(PlacesModule.class);
        if (placesModule.getWorld(event.getFrom().getSpawnLocation()) != null) return;
        MinetopiaPlayer minetopiaPlayer = PlayerManager.getInstance().getOnlineMinetopiaPlayer(event.getPlayer());
        if (minetopiaPlayer == null) return;
        minetopiaPlayer.getFitness().getFitnessModule().getFitnessRunnable().forceMarkDirty(event.getPlayer().getUniqueId());
    }
}
