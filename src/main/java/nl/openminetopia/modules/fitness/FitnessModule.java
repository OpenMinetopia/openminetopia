package nl.openminetopia.modules.fitness;

import lombok.Getter;
import nl.openminetopia.modules.data.DataModule;
import nl.openminetopia.modules.fitness.commands.FitnessCommand;
import nl.openminetopia.modules.fitness.commands.subcommands.FitnessBoosterCommand;
import nl.openminetopia.modules.fitness.commands.subcommands.FitnessResetCommand;
import nl.openminetopia.modules.fitness.listeners.PlayerChangeWorldListener;
import nl.openminetopia.modules.fitness.listeners.PlayerDeathListener;
import nl.openminetopia.modules.fitness.listeners.PlayerDrinkListener;
import nl.openminetopia.modules.fitness.listeners.PlayerEatListener;
import com.jazzkuh.modulemanager.spigot.SpigotModule;
import com.jazzkuh.modulemanager.spigot.SpigotModuleManager;
import nl.openminetopia.OpenMinetopia;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
public class FitnessModule extends SpigotModule<@NotNull OpenMinetopia> {

    public final Map<UUID, Long> lastDrinkingTimes = new HashMap<>();

    public FitnessModule(SpigotModuleManager<@NotNull OpenMinetopia> moduleManager, DataModule dataModule) {
        super(moduleManager);
    }

    public void onEnable() {
        registerComponent(new FitnessCommand());
        registerComponent(new FitnessBoosterCommand());
        registerComponent(new FitnessResetCommand());

        registerComponent(new PlayerEatListener());
        registerComponent(new PlayerDrinkListener());
        registerComponent(new PlayerDeathListener());
        registerComponent(new PlayerChangeWorldListener());
    }

    public void onDisable() {

    }
}