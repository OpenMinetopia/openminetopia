package nl.openminetopia.modules.fitness.listeners;

import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.api.player.PlayerManager;
import nl.openminetopia.api.player.objects.MinetopiaPlayer;
import nl.openminetopia.modules.fitness.FitnessModule;
import nl.openminetopia.modules.fitness.configuration.FitnessConfiguration;
import nl.openminetopia.modules.fitness.utils.FitnessUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerDeathListener implements Listener {

    @EventHandler
    public void playerDeath(final PlayerDeathEvent event) {
        FitnessModule fitnessModule = OpenMinetopia.getModuleManager().get(FitnessModule.class);
        FitnessConfiguration configuration = fitnessModule.getConfiguration();
        if (!configuration.isFitnessDeathPunishmentEnabled()) return;

        Player player = event.getEntity();
        MinetopiaPlayer minetopiaPlayer = PlayerManager.getInstance().getOnlineMinetopiaPlayer(player);
        if (minetopiaPlayer == null) return;

        int punishmentInMillis = configuration.getFitnessDeathPunishmentDuration() * 60 * 1000;

        int amount = configuration.getFitnessDeathPunishmentAmount();
        long expiry = System.currentTimeMillis() + punishmentInMillis;

        FitnessUtils.clearFitnessEffects(player);

        minetopiaPlayer.getFitness().addBooster(amount, expiry);
        minetopiaPlayer.getFitness().getFitnessModule().getFitnessRunnable().forceMarkDirty(player.getUniqueId());
    }
}
