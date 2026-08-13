package nl.openminetopia.api.player.fitness;

import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.modules.fitness.FitnessModule;
import nl.openminetopia.modules.fitness.configuration.FitnessConfiguration;
import org.bukkit.Material;

public enum DefaultFitnessStatisticType implements FitnessStatisticType {
    WALKING("Lopen", Material.LEATHER_BOOTS),
    SPRINTING("Rennen", Material.DIAMOND_BOOTS),
    CLIMBING("Klimmen", Material.LADDER),
    SWIMMING("Zwemmen", Material.OAK_BOAT),
    FLYING("Vliegen", Material.ELYTRA),
    DRINKING("Drinken", Material.POTION),
    EATING("Eten", Material.GOLDEN_APPLE),
    HEALTH("Fatsoenlijk eten", Material.APPLE);

    private final String displayName;
    private final Material icon;

    DefaultFitnessStatisticType(String displayName, Material icon) {
        this.displayName = displayName;
        this.icon = icon;
    }

    @Override
    public String key() {
        return name();
    }

    @Override
    public String displayName() {
        return displayName;
    }

    @Override
    public Material icon() {
        return icon;
    }

    @Override
    public int maximum() {
        FitnessConfiguration configuration = configuration();
        return switch (this) {
            case WALKING -> configuration.getMaxFitnessByWalking();
            case SPRINTING -> configuration.getMaxFitnessBySprinting();
            case CLIMBING -> configuration.getMaxFitnessByClimbing();
            case SWIMMING -> configuration.getMaxFitnessBySwimming();
            case FLYING -> configuration.getMaxFitnessByFlying();
            case DRINKING -> configuration.getMaxFitnessByDrinking();
            case EATING -> configuration.getMaxFitnessByEating();
            case HEALTH -> configuration.getMaxFitnessByHealth();
        };
    }

    @Override
    public double progressPerPoint() {
        FitnessConfiguration configuration = configuration();
        return switch (this) {
            case WALKING -> configuration.getCmPerWalkingLevel();
            case SPRINTING -> configuration.getCmPerSprintingLevel();
            case CLIMBING -> configuration.getCmPerClimbingLevel();
            case SWIMMING -> configuration.getCmPerSwimmingLevel();
            case FLYING -> configuration.getCmPerFlyingLevel();
            case DRINKING -> configuration.getDrinkingPointsPerFitnessLevel();
            case EATING -> configuration.getEatingPointsPerFitnessLevel();
            case HEALTH -> configuration.getHealthPointsPerFitnessLevel();
        };
    }

    private FitnessConfiguration configuration() {
        return OpenMinetopia.getModuleManager().get(FitnessModule.class).getConfiguration();
    }
}
