package nl.openminetopia.modules.player.utils;

import lombok.experimental.UtilityClass;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.api.player.fitness.Fitness;
import nl.openminetopia.api.player.objects.MinetopiaPlayer;
import nl.openminetopia.modules.banking.BankingModule;
import nl.openminetopia.modules.banking.models.BankAccountModel;
import nl.openminetopia.modules.player.PlayerModule;
import nl.openminetopia.modules.player.configuration.LevelCheckConfiguration;
import nl.openminetopia.modules.player.events.PlayerLevelCalculateEvent;
import nl.openminetopia.utils.WorldGuardUtils;
import nl.openminetopia.utils.events.EventUtils;
import org.bukkit.entity.Player;

import java.util.concurrent.CompletableFuture;

@UtilityClass
public class LevelUtil {

    public int calculateLevel(MinetopiaPlayer minetopiaPlayer) {
        PlayerModule playerModule = OpenMinetopia.getModuleManager().get(PlayerModule.class);
        LevelCheckConfiguration configuration = playerModule.getConfiguration();
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
        points += (double) (minetopiaPlayer.getPlaytime() / 1000 / 3600) * configuration.getPointsPerHourPlayed();

        // Points per plot
        Player player = minetopiaPlayer.getBukkit().getPlayer();
        if (player == null) return OpenMinetopia.getDefaultConfiguration().getDefaultLevel();
        points += WorldGuardUtils.getOwnedRegions(player) * configuration.getPointsPerPlot();

        int neededPoints = configuration.getPointsNeededForLevelUp();
        int level = (int) Math.floor(points / neededPoints);

        level = Math.max(OpenMinetopia.getDefaultConfiguration().getDefaultLevel(),
                Math.min(level, configuration.getMaxLevel()));

        PlayerLevelCalculateEvent event = new PlayerLevelCalculateEvent(player, level, (int) points);
        if (EventUtils.callCancellable(event)) return oldCalculatedLevel;

        return level;
    }

    public CompletableFuture<Double> calculateLevelupCosts(int currentLevel, int newLevel) {
        LevelCheckConfiguration configuration = OpenMinetopia.getModuleManager().get(PlayerModule.class).getConfiguration();
        return CompletableFuture.supplyAsync(() -> {
            double cost = 0;
            for (int i = currentLevel + 1; i <= newLevel; i++) {
                if (configuration.getLevelUpCostOverrides().get(i) != null) {
                    cost += configuration.getLevelUpCostOverrides().get(i);
                    continue;
                }

                String wageFormula = configuration.getWageFormula().replace("<level>", "l");
                Expression expression = new ExpressionBuilder(wageFormula)
                        .variables("l")
                        .build()
                        .setVariable("l", i);
                cost += expression.evaluate();
            }
            return cost;
        });
    }
}