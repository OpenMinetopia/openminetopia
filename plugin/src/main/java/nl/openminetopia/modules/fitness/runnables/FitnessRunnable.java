package nl.openminetopia.modules.fitness.runnables;

import lombok.SneakyThrows;
import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.api.player.PlayerManager;
import nl.openminetopia.api.player.fitness.Fitness;
import nl.openminetopia.api.player.fitness.FitnessStatisticType;
import nl.openminetopia.api.player.objects.MinetopiaPlayer;
import nl.openminetopia.framework.runnables.AbstractDirtyRunnable;

import nl.openminetopia.modules.fitness.FitnessModule;
import nl.openminetopia.modules.fitness.configuration.FitnessConfiguration;
import nl.openminetopia.modules.fitness.models.FitnessBoosterModel;
import nl.openminetopia.modules.fitness.models.FitnessStatisticModel;
import nl.openminetopia.modules.fitness.utils.FitnessUtils;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;


import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

public class FitnessRunnable extends AbstractDirtyRunnable<UUID> {

    private final PlayerManager playerManager;
    private final FitnessModule fitnessModule;

    public FitnessRunnable(PlayerManager playerManager, FitnessModule fitnessModule, long minIntervalMs, int batch, long heartbeatMs, Supplier<List<UUID>> allKeysSupplier) {
        super(minIntervalMs, batch, heartbeatMs, allKeysSupplier);
        this.playerManager = playerManager;
        this.fitnessModule = fitnessModule;
    }

    @Override
    protected void process(UUID key) {
        FitnessConfiguration config = fitnessModule.getConfiguration();
        MinetopiaPlayer minetopiaPlayer = playerManager.getOnlinePlayers().get(key);
        if (minetopiaPlayer == null) return;
        Fitness fitness = minetopiaPlayer.getFitness();
        if (fitness == null) return;
        OfflinePlayer player = minetopiaPlayer.getBukkit();
        if (player == null || !player.isOnline()) {
            remove(key);
            return;
        }

        List<FitnessBoosterModel> boosterModels = new ArrayList<>(fitness.getBoosters());
        for (FitnessBoosterModel boosterModel : boosterModels) {
            if (!boosterModel.isExpired()) continue;
            fitness.removeBooster(boosterModel);
        }

        updateFitnessStatistic(FitnessStatisticType.WALKING, Statistic.WALK_ONE_CM, fitness, player);
        updateFitnessStatistic(FitnessStatisticType.CLIMBING, Statistic.CLIMB_ONE_CM, fitness, player);
        updateFitnessStatistic(FitnessStatisticType.SPRINTING, Statistic.SPRINT_ONE_CM, fitness, player);
        updateFitnessStatistic(FitnessStatisticType.SWIMMING, Statistic.SWIM_ONE_CM, fitness, player);
        updateFitnessStatistic(FitnessStatisticType.FLYING, Statistic.AVIATE_ONE_CM, fitness, player);
        updateEatingFitness(fitness);

        int totalFitness = calculateTotalFitness(fitness) + calculateFitnessBoost(fitness);
        if (totalFitness <= 0) totalFitness = 1;

        fitness.setTotalFitness(Math.min(totalFitness, config.getMaxFitnessLevel()));
        if (player.isOnline()) fitness.apply();

    }


    private void updateFitnessStatistic(FitnessStatisticType type, Statistic statistic, Fitness fitness, OfflinePlayer player) {
        FitnessStatisticModel statModel = fitness.getStatistic(type);
        int currentDistance = player.getStatistic(statistic);
        int newFitness = FitnessUtils.calculateFitness(currentDistance, (int) statModel.getProgressPerPoint());

        if (statModel.getFitnessGained() != newFitness && newFitness <= statModel.getMaximum()) {
            statModel.setFitnessGained(Math.min(newFitness, statModel.getMaximum()));
        }

        fitness.setStatistic(type, statModel);
    }

    private void updateEatingFitness(Fitness fitness) {
        FitnessConfiguration config = OpenMinetopia.getModuleManager().get(FitnessModule.class).getConfiguration();

        FitnessStatisticModel eatingStat = fitness.getStatistic(FitnessStatisticType.EATING);
        double eatingPoints = (eatingStat.getSecondaryPoints() * config.getPointsForCheapFood())
                + (eatingStat.getTertiaryPoints() * config.getPointsForLuxuryFood());

        eatingStat.setPoints(eatingPoints);
        if (eatingPoints >= 1 && eatingStat.getFitnessGained() <= eatingStat.getMaximum()) {
            eatingStat.setFitnessGained(eatingStat.getFitnessGained() + 1);
            eatingStat.setPoints(0.0);
        }

        fitness.setStatistic(FitnessStatisticType.EATING, eatingStat);
    }

    private int calculateTotalFitness(Fitness fitness) {
        FitnessConfiguration config = fitnessModule.getConfiguration();

        return fitness.getStatistics().stream()
                .mapToInt(FitnessStatisticModel::getFitnessGained)
                .sum() + config.getDefaultFitnessLevel();
    }

    private int calculateFitnessBoost(Fitness fitness) {
        return fitness.getBoosters().stream()
                .mapToInt(FitnessBoosterModel::getAmount)
                .sum();
    }
}