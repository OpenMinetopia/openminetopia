package nl.openminetopia.api.player.fitness;

import com.craftmend.storm.api.enums.Where;
import lombok.Getter;
import lombok.Setter;
import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.modules.data.storm.StormDatabase;
import nl.openminetopia.modules.data.utils.StormUtils;
import nl.openminetopia.modules.fitness.FitnessModule;
import nl.openminetopia.modules.fitness.models.FitnessBoosterModel;
import nl.openminetopia.modules.fitness.models.FitnessModel;
import nl.openminetopia.modules.fitness.models.FitnessStatisticModel;
import nl.openminetopia.modules.fitness.runnables.FitnessRunnable;
import nl.openminetopia.modules.fitness.utils.FitnessUtils;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Getter
public class Fitness {

    private final UUID uuid;
    private final FitnessModel fitnessModel;
    private final FitnessRunnable runnable;

    private @Setter int totalFitness;

    private final FitnessModule fitnessModule = OpenMinetopia.getModuleManager().getModule(FitnessModule.class);

    public Fitness(UUID uuid, FitnessModel fitnessModel) {
        this.uuid = uuid;
        this.fitnessModel = fitnessModel;
        this.runnable = new FitnessRunnable(this);

        runnable.runTaskTimerAsynchronously(OpenMinetopia.getInstance(), 0, 60 * 20L);
    }

    public CompletableFuture<Void> save() {
        CompletableFuture<Void> future = new CompletableFuture<>();

        StormDatabase.getInstance().saveStormModel(fitnessModel);
        fitnessModel.getBoosters().forEach(StormDatabase.getInstance()::saveStormModel);
        fitnessModel.getStatistics().forEach(StormDatabase.getInstance()::saveStormModel);

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
        FitnessStatisticModel model = fitnessModel.getStatistics().stream().filter(statistic -> statistic.getType().equals(type)).findFirst()
                .orElse(null);

        if (model == null) {
            model = new FitnessStatisticModel();
            model.setFitnessId(fitnessModel.getId());
            model.setType(type);
            model.setFitnessGained(0);
            model.setPoints(0.0);
            model.setSecondaryPoints(0.0);
            model.setTertiaryPoints(0.0);
            fitnessModel.getStatistics().add(model);
            StormDatabase.getInstance().saveStormModel(model);
            return model;
        }

        return model;
    }

    public void setStatistic(FitnessStatisticType type, FitnessStatisticModel model) {
        fitnessModel.getStatistics().stream()
                .filter(statistic -> statistic.getType().equals(type))
                .findFirst()
                .ifPresent(statistic -> {
                    statistic.setFitnessGained(model.getFitnessGained());
                    statistic.setPoints(model.getPoints());
                    statistic.setSecondaryPoints(model.getSecondaryPoints());
                    statistic.setTertiaryPoints(model.getTertiaryPoints());
                });
    }

    public void addBooster(FitnessBoosterModel booster) {
        fitnessModel.getBoosters().add(booster);
        StormDatabase.getInstance().saveStormModel(booster);
    }

    public void removeBooster(FitnessBoosterModel booster) {
        fitnessModel.getBoosters().remove(booster);
        StormUtils.deleteModelData(FitnessBoosterModel.class, query ->
                query.where("id", Where.EQUAL, booster.getId()));
    }

    public List<FitnessStatisticModel> getStatistics() {
        return fitnessModel.getStatistics();
    }

    public List<FitnessBoosterModel> getBoosters() {
        return fitnessModel.getBoosters();
    }
}