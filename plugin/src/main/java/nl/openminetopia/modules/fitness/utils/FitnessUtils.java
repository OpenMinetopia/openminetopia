package nl.openminetopia.modules.fitness.utils;

import lombok.experimental.UtilityClass;
import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.api.player.PlayerManager;
import nl.openminetopia.api.player.fitness.FitnessStatisticType;
import nl.openminetopia.api.player.objects.MinetopiaPlayer;
import nl.openminetopia.modules.fitness.FitnessModule;
import nl.openminetopia.modules.fitness.configuration.FitnessConfiguration;
import nl.openminetopia.modules.fitness.models.FitnessStatisticModel;
import nl.openminetopia.modules.fitness.objects.FitnessLevelEffect;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Utility class for fitness-related operations and calculations.
 */
@UtilityClass
public class FitnessUtils {

    // Constants
    private static final float DEFAULT_WALK_SPEED = 0.2f;
    private static final float RAIN_SLOWDOWN_AMOUNT = 0.05f;
    private static final int HIGH_FOOD_THRESHOLD = 18; // Above 9 hearts
    private static final int LOW_FOOD_THRESHOLD = 4;   // Below 2 hearts  
    private static final int MID_FOOD_THRESHOLD = 10;  // Below 5 hearts

    /**
     * Applies fitness effects to a player based on their current fitness level.
     * This includes walk speed modifications and potion effects.
     *
     * @param player The player to apply fitness effects to
     */
    public static void applyFitness(Player player) {
        MinetopiaPlayer minetopiaPlayer = PlayerManager.getInstance().getOnlineMinetopiaPlayer(player);

        if (minetopiaPlayer == null || !minetopiaPlayer.isInPlace()) {
            clearFitnessEffects(player);
            return;
        }

        FitnessConfiguration config = getFitnessConfiguration();
        int totalFitness = minetopiaPlayer.getFitness().getTotalFitness();

        // Clear existing effects to ensure clean state
        clearExistingFitnessEffects(player, config);

        // Find the appropriate fitness level effect
        FitnessLevelEffect effectLevel = findApplicableFitnessEffect(config, totalFitness);
        if (effectLevel == null) return;

        // Apply the effects
        applyWalkSpeed(player, config, (float) effectLevel.getWalkSpeed());
        applyPotionEffects(player, effectLevel);
    }

    /**
     * Clears all fitness-related effects from a player and resets their walk speed to default.
     *
     * @param player The player to clear effects from
     */
    public static void clearFitnessEffects(Player player) {
        FitnessConfiguration config = getFitnessConfiguration();
        List<PotionEffectType> effectsToRemove = getAllFitnessEffectTypes(config);

        Bukkit.getScheduler().runTask(OpenMinetopia.getInstance(), () -> {
            effectsToRemove.forEach(player::removePotionEffect);
            player.setWalkSpeed(DEFAULT_WALK_SPEED);
        });
    }

    /**
     * Calculates fitness points based on distance traveled and points per unit.
     *
     * @param currentDistance     The total distance traveled in cm
     * @param amountOfCmPerPoint  The amount of cm required per fitness point
     * @return The calculated fitness points
     */
    public static int calculateFitness(int currentDistance, int amountOfCmPerPoint) {
        if (amountOfCmPerPoint <= 0) return 0;
        return (currentDistance - currentDistance % amountOfCmPerPoint) / amountOfCmPerPoint;
    }

    /**
     * Performs a health check for a player and updates their health-based fitness statistics.
     *
     * @param minetopiaPlayer The player to perform the health check on
     */
    public static void performHealthCheck(MinetopiaPlayer minetopiaPlayer) {
        Player player = minetopiaPlayer.getBukkit().getPlayer();
        if (player == null) return;

        FitnessConfiguration config = getFitnessConfiguration();
        FitnessStatisticModel healthStatistic = minetopiaPlayer.getFitness().getStatistic(FitnessStatisticType.HEALTH);
        
        double healthPoints = calculateHealthPoints(player, config, healthStatistic.getPoints());
        healthStatistic.setPoints(healthPoints);

        // Check if fitness level should be increased
        if (healthPoints >= 1 && healthStatistic.getFitnessGained() < healthStatistic.getMaximum()) {
            healthStatistic.setFitnessGained(healthStatistic.getFitnessGained() + 1);
            healthStatistic.setPoints(0.0);
        }

        minetopiaPlayer.getFitness().setStatistic(FitnessStatisticType.HEALTH, healthStatistic);
    }

    // Private helper methods

    private static FitnessConfiguration getFitnessConfiguration() {
        return OpenMinetopia.getModuleManager().get(FitnessModule.class).getConfiguration();
    }

    private static FitnessLevelEffect findApplicableFitnessEffect(FitnessConfiguration config, int totalFitness) {
        return config.getLevelEffects().entrySet().stream()
                .filter(entry -> totalFitness >= entry.getKey())
                .map(Map.Entry::getValue)
                .reduce((first, second) -> second) // Get the highest matching effect
                .orElse(null);
    }

    private static void clearExistingFitnessEffects(Player player, FitnessConfiguration config) {
        List<PotionEffectType> effectsToRemove = getAllFitnessEffectTypes(config);

        Bukkit.getScheduler().runTask(OpenMinetopia.getInstance(), () -> {
            effectsToRemove.forEach(player::removePotionEffect);
        });
    }

    private static List<PotionEffectType> getAllFitnessEffectTypes(FitnessConfiguration config) {
        return config.getLevelEffects().values().stream()
                .flatMap(levelEffect -> levelEffect.getEffects().stream())
                .map(effect -> effect.split(":")[0].toLowerCase())
                .distinct()
                .map(effectName -> Registry.EFFECT.get(NamespacedKey.minecraft(effectName)))
                .filter(Objects::nonNull)
                .toList();
    }

    private static void applyWalkSpeed(Player player, FitnessConfiguration config, float walkSpeed) {
        Bukkit.getScheduler().runTask(OpenMinetopia.getInstance(), () -> {
            float finalWalkSpeed = walkSpeed;
            if (player.getWorld().hasStorm() && config.isRainSlowdownEnabled()) {
                finalWalkSpeed = Math.max(0.1f, walkSpeed - RAIN_SLOWDOWN_AMOUNT);
            }
            player.setWalkSpeed(finalWalkSpeed);
        });
    }

    private static void applyPotionEffects(Player player, FitnessLevelEffect effectLevel) {
        List<PotionEffect> validEffects = effectLevel.getEffects().stream()
                .map(FitnessUtils::parseEffect)
                .filter(Objects::nonNull)
                .toList();

        Bukkit.getScheduler().runTask(OpenMinetopia.getInstance(), () -> {
            validEffects.forEach(effect -> {
                player.removePotionEffect(effect.getType());
                player.addPotionEffect(effect);
            });
        });
    }

    private static PotionEffect parseEffect(String effectString) {
        try {
            String[] parts = effectString.split(":");
            if (parts.length != 2) return null;

            PotionEffectType effectType = Registry.EFFECT.get(NamespacedKey.minecraft(parts[0].toLowerCase()));
            if (effectType == null) return null;

            int amplifier = Integer.parseInt(parts[1]) - 1;
            if (amplifier < 0) return null;

            return new PotionEffect(effectType, PotionEffect.INFINITE_DURATION, amplifier, true, false);
        } catch (NumberFormatException e) {
            OpenMinetopia.getInstance().getLogger().warning("Invalid effect format: " + effectString);
            return null;
        }
    }

    private static double calculateHealthPoints(Player player, FitnessConfiguration config, double currentPoints) {
        int foodLevel = player.getFoodLevel();
        
        if (foodLevel >= HIGH_FOOD_THRESHOLD) {
            return currentPoints + config.getPointsAbove9Hearts();
        } else if (foodLevel <= LOW_FOOD_THRESHOLD) {
            return currentPoints + config.getPointsBelow2Hearts();
        } else if (foodLevel <= MID_FOOD_THRESHOLD) {
            return currentPoints + config.getPointsBelow5Hearts();
        }
        
        return currentPoints;
    }
}