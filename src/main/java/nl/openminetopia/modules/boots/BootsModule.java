package nl.openminetopia.modules.boots;

import com.jazzkuh.modulemanager.spigot.SpigotModule;
import com.jazzkuh.modulemanager.spigot.SpigotModuleManager;
import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.modules.boots.commands.BootsCommand;
import nl.openminetopia.modules.boots.listeners.BootsListener;
import nl.openminetopia.utils.FeatureUtils;
import org.jetbrains.annotations.NotNull;

public class BootsModule extends SpigotModule<@NotNull OpenMinetopia> {

    public BootsModule(SpigotModuleManager<@NotNull OpenMinetopia> moduleManager) {
        super(moduleManager);
    }

    @Override
    public void onEnable() {
        // Check if boots feature is enabled
        if (FeatureUtils.isFeatureDisabled("boots")) {
            getLogger().info("Boots feature is disabled in config.yml");
            return;
        }

        // Register commands and listeners
        registerComponent(new BootsCommand());
        registerComponent(new BootsListener());
    }
}