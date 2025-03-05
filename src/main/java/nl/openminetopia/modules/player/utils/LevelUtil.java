package nl.openminetopia.modules.player.utils;

import lombok.experimental.UtilityClass;
import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.api.player.events.PlayerLevelCalculateEvent;
import nl.openminetopia.api.player.fitness.Fitness;
import nl.openminetopia.api.player.objects.MinetopiaPlayer;
import nl.openminetopia.configuration.LevelCheckConfiguration;
import nl.openminetopia.modules.banking.BankingModule;
import nl.openminetopia.modules.banking.models.BankAccountModel;
import nl.openminetopia.utils.WorldGuardUtils;
import org.bukkit.entity.Player;

@UtilityClass
public class LevelUtil {

    public int calculateLevel(MinetopiaPlayer minetopiaPlayer) {
        LevelCheckConfiguration configuration = OpenMinetopia.getLevelcheckConfiguration();
        double points = 0;
        int oldCalculatedLevel = minetopiaPlayer.getCalculatedLevel();

        // Points per 5k balance
        BankAccountModel accountModel = OpenMinetopia.getModuleManager().get(BankingModule.class).getAccountById(minetopiaPlayer.getUuid());
        double balance = (accountModel == null) ? 0 : accountModel.getBalance();
        points += Math.floor(balance / 5000) * configuration.getPointsPer5KBalance();

        // Points for having a prefix
        if (minetopiaPlayer.getPrefixes() != null && !minetopiaPlayer.getPrefixes().isEmpty()) {
            points += configuration.getPointsForPrefix();
        }

        // Points per 20 fitness
        Fitness fitness = minetopiaPlayer.getFitness();
        if (fitness == null) return 0;
        points += (double) (fitness.getTotalFitness() / 20) * configuration.getPointsPer20Fitness();

        // Points per 1 hour playtime
        points += (double) (minetopiaPlayer.getPlaytime() / 3600) * configuration.getPointsPerHourPlayed();

        // Points per plot
        Player player = minetopiaPlayer.getBukkit().getPlayer();
        if (player == null) return OpenMinetopia.getDefaultConfiguration().getDefaultLevel();
        points += WorldGuardUtils.getOwnedRegions(player) * configuration.getPointsPerPlot();

        int neededPoints = configuration.getPointsNeededForLevelUp();
        int level = (int) Math.floor(points / neededPoints);

        level = Math.max(OpenMinetopia.getDefaultConfiguration().getDefaultLevel(),
                Math.min(level, OpenMinetopia.getLevelcheckConfiguration().getMaxLevel()));

        PlayerLevelCalculateEvent event = new PlayerLevelCalculateEvent(minetopiaPlayer, level, (int) points);
        if (!event.callEvent()) return oldCalculatedLevel;

        return level;
    }
}