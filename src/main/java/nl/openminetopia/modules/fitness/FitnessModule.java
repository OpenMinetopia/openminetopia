package nl.openminetopia.modules.fitness;

import com.jazzkuh.modulemanager.spigot.SpigotModule;
import com.jazzkuh.modulemanager.spigot.SpigotModuleManager;
import lombok.Getter;
import lombok.Setter;
import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.modules.data.DataModule;
import nl.openminetopia.modules.fitness.commands.FitnessCommand;
import nl.openminetopia.modules.fitness.commands.subcommands.FitnessBoosterCommand;
import nl.openminetopia.modules.fitness.commands.subcommands.FitnessResetCommand;
import nl.openminetopia.modules.fitness.configuration.FitnessConfiguration;
import nl.openminetopia.modules.fitness.listeners.*;
import nl.openminetopia.utils.FeatureUtils;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
public class FitnessModule extends SpigotModule<@NotNull OpenMinetopia> {

    public FitnessModule(SpigotModuleManager<@NotNull OpenMinetopia> moduleManager, DataModule dataModule) {
        super(moduleManager);
    }

    public final Map<UUID, Long> lastDrinkingTimes = new HashMap<>();
    public final Map<UUID, Long> fitnessItemCooldowns = new HashMap<>();

    @Getter @Setter
    private FitnessConfiguration configuration;

    public void onEnable() {
        configuration = new FitnessConfiguration(OpenMinetopia.getInstance().getDataFolder());
        configuration.saveConfiguration();

        // Check if fitness feature is enabled
        if (FeatureUtils.isFeatureDisabled("fitness")) {
            getLogger().info("Fitness feature is disabled in config.yml");
            return;
        }

        registerComponent(new FitnessCommand());
        registerComponent(new FitnessBoosterCommand());
        registerComponent(new FitnessResetCommand());

        registerComponent(new PlayerEatListener());
        registerComponent(new PlayerDrinkListener());
        registerComponent(new PlayerDeathListener());
        registerComponent(new PlayerChangeWorldListener());
        registerComponent(new PlayerConsumeBoosterListener());
    }

    public void onDisable() {

    }
}