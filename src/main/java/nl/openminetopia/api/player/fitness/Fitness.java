package nl.openminetopia.api.player.fitness;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.api.player.objects.MinetopiaPlayer;
import nl.openminetopia.modules.data.storm.StormDatabase;
import nl.openminetopia.modules.fitness.FitnessModule;
import nl.openminetopia.modules.fitness.models.FitnessBoosterModel;
import nl.openminetopia.modules.fitness.models.FitnessStatisticModel;
import nl.openminetopia.modules.fitness.runnables.FitnessRunnable;
import nl.openminetopia.modules.fitness.runnables.HealthStatisticRunnable;
import nl.openminetopia.modules.fitness.utils.FitnessUtils;
import nl.openminetopia.modules.player.models.PlayerModel;
import org.bukkit.Bukkit;
import org.bukkit.Statistic;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Getter
public class Fitness {

    private final MinetopiaPlayer minetopiaPlayer;
    private final UUID uuid;
    private final PlayerModel playerModel;
    private final FitnessRunnable runnable;
    private final HealthStatisticRunnable healthStatisticRunnable;
    private boolean fitnessReset;

    private @Setter int totalFitness;

    private final FitnessModule fitnessModule = OpenMinetopia.getModuleManager().get(FitnessModule.class);

    public Fitness(MinetopiaPlayer minetopiaPlayer) {
        this.minetopiaPlayer = minetopiaPlayer;
        this.uuid = minetopiaPlayer.getUuid();
        this.playerModel = minetopiaPlayer.getPlayerModel();
        this.runnable = new FitnessRunnable(this);
        this.fitnessReset = playerModel.getFitnessReset();
        this.healthStatisticRunnable = new HealthStatisticRunnable(minetopiaPlayer);

        runnable.runTaskTimerAsynchronously(OpenMinetopia.getInstance(), 20L, 60 * 20L);
        healthStatisticRunnable.runTaskTimerAsynchronously(OpenMinetopia.getInstance(), 0, 20L);
    }

    public CompletableFuture<Void> save() {
        CompletableFuture<Void> future = new CompletableFuture<>();

        StormDatabase.getInstance().saveStormModel(playerModel);
        playerModel.getBoosters().forEach(StormDatabase.getInstance()::saveStormModel);
        playerModel.getStatistics().forEach(StormDatabase.getInstance()::saveStormModel);

        future.complete(null);
        return future;
    }

    public void apply() {
        FitnessUtils.applyFitness(Bukkit.getPlayer(uuid));
    }

    public void setLastDrinkingTime(long time) {
        fitnessModule.getLastDrinkingTimes().put(uuid, time);
    }

    public long getLastDrinkingTime() {
        return fitnessModule.getLastDrinkingTimes().getOrDefault(uuid, 0L);
    }

    @NotNull
    public FitnessStatisticModel getStatistic(FitnessStatisticType type) {
        FitnessStatisticModel model = playerModel.getStatistics().stream()
                .filter(statistic -> statistic.getType().equals(type))
                .findFirst()
                .orElse(null);

        if (model == null) {
            model = new FitnessStatisticModel();
            model.setPlayerId(playerModel.getId());
            model.setType(type);
            model.setFitnessGained(0);
            model.setPoints(0.0);
            model.setSecondaryPoints(0.0);
            model.setTertiaryPoints(0.0);
            playerModel.getStatistics().add(model);
            StormDatabase.getInstance().saveStormModel(model);
        }
        return model;
    }

    public void setStatistic(FitnessStatisticType type, FitnessStatisticModel model) {
        playerModel.getStatistics().stream()
                .filter(statistic -> statistic.getType().equals(type))
                .findFirst()
                .ifPresent(statistic -> {
                    statistic.setFitnessGained(model.getFitnessGained());
                    statistic.setPoints(model.getPoints());
                    statistic.setSecondaryPoints(model.getSecondaryPoints());
                    statistic.setTertiaryPoints(model.getTertiaryPoints());
                });
    }

    public void addBooster(int amount, long expiresAtMillis) {
        FitnessBoosterModel boosterModel = new FitnessBoosterModel();
        boosterModel.setPlayerId(this.playerModel.getId());
        boosterModel.setAmount(amount);
        boosterModel.setExpiresAt(expiresAtMillis);
        this.playerModel.getBoosters().add(boosterModel);
        StormDatabase.getInstance().saveStormModel(boosterModel);
        runnable.run();
    }

    @SneakyThrows
    public void removeBooster(FitnessBoosterModel booster) {
        this.playerModel.getBoosters().remove(booster);
        StormDatabase.getInstance().getStorm().delete(booster);

        runnable.run();
    }

    public List<FitnessStatisticModel> getStatistics() {
        return playerModel.getStatistics();
    }

    public List<FitnessBoosterModel> getBoosters() {
        return playerModel.getBoosters();
    }

    public void setFitnessReset(boolean enabled) {
        this.fitnessReset = enabled;
        playerModel.setFitnessReset(enabled);
        StormDatabase.getInstance().saveStormModel(playerModel);
    }

    @SneakyThrows
    public void reset() {
        setFitnessReset(false);

        minetopiaPlayer.getBukkit().setStatistic(Statistic.WALK_ONE_CM, 0);
        minetopiaPlayer.getBukkit().setStatistic(Statistic.CLIMB_ONE_CM, 0);
        minetopiaPlayer.getBukkit().setStatistic(Statistic.SPRINT_ONE_CM, 0);
        minetopiaPlayer.getBukkit().setStatistic(Statistic.SWIM_ONE_CM, 0);
        minetopiaPlayer.getBukkit().setStatistic(Statistic.AVIATE_ONE_CM, 0);

        List<FitnessStatisticModel> statistics = playerModel.getStatistics();
        statistics.forEach(statistic -> {
            statistic.setFitnessGained(0);
            statistic.setPoints(0.0);
            statistic.setSecondaryPoints(0.0);
            statistic.setTertiaryPoints(0.0);
            StormDatabase.getInstance().saveStormModel(statistic);
        });

        List<FitnessBoosterModel> boosters = playerModel.getBoosters();
        boosters.forEach(booster -> {
            try {
                StormDatabase.getInstance().getStorm().delete(booster);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
        playerModel.getBoosters().clear();

        this.totalFitness = OpenMinetopia.getFitnessConfiguration().getDefaultFitnessLevel();

        FitnessUtils.clearFitnessEffects(minetopiaPlayer.getBukkit().getPlayer());

        save();
        runnable.run();
    }
}
