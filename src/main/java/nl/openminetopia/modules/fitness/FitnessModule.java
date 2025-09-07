package nl.openminetopia.modules.fitness;

import com.jazzkuh.modulemanager.spigot.SpigotModule;
import com.jazzkuh.modulemanager.spigot.SpigotModuleManager;
import lombok.Getter;
import lombok.Setter;
import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.api.player.PlayerManager;
import nl.openminetopia.modules.data.DataModule;
import nl.openminetopia.modules.fitness.commands.FitnessCommand;
import nl.openminetopia.modules.fitness.commands.subcommands.FitnessBoosterCommand;
import nl.openminetopia.modules.fitness.commands.subcommands.FitnessResetCommand;
import nl.openminetopia.modules.fitness.configuration.FitnessConfiguration;
import nl.openminetopia.modules.fitness.listeners.*;
import nl.openminetopia.modules.fitness.runnables.FitnessRunnable;
import nl.openminetopia.modules.fitness.runnables.HealthStatisticRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
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

    private HealthStatisticRunnable healthStatisticRunnable;
    private FitnessRunnable fitnessRunnable;

    @Getter @Setter
    private FitnessConfiguration configuration;

    public void onEnable() {
        configuration = new FitnessConfiguration(OpenMinetopia.getInstance().getDataFolder());
        configuration.saveConfiguration();

        registerComponent(new FitnessCommand());
        registerComponent(new FitnessBoosterCommand());
        registerComponent(new FitnessResetCommand());

        registerComponent(new PlayerEatListener());
        registerComponent(new PlayerDrinkListener());
        registerComponent(new PlayerDeathListener());
        registerComponent(new PlayerChangeWorldListener());
        registerComponent(new PlayerConsumeBoosterListener());

        healthStatisticRunnable = new HealthStatisticRunnable(PlayerManager.getInstance(), 15000L, 50, 50 * 1000L, () -> new ArrayList<>(PlayerManager.getInstance().getOnlinePlayers().keySet()));
        OpenMinetopia.getInstance().registerDirtyPlayerRunnable(healthStatisticRunnable, 20L * 3);

        fitnessRunnable = new FitnessRunnable(PlayerManager.getInstance(), this, 60000L, 50, 60 * 5 * 1000L, () -> new ArrayList<>(PlayerManager.getInstance().getOnlinePlayers().keySet()));
        OpenMinetopia.getInstance().registerDirtyPlayerRunnable(fitnessRunnable, 20L * 2);
    }

    public void onDisable() {
        OpenMinetopia.getInstance().unregisterDirtyPlayerRunnable(healthStatisticRunnable);
    }
}