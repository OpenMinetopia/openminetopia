package nl.openminetopia.modules.core;

import com.jazzkuh.modulemanager.spigot.SpigotModule;
import com.jazzkuh.modulemanager.spigot.SpigotModuleManager;
import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.modules.core.commands.OpenMinetopiaCommand;
import nl.openminetopia.utils.FeatureUtils;
import org.jetbrains.annotations.NotNull;

public class CoreModule extends SpigotModule<@NotNull OpenMinetopia> {
    public CoreModule(SpigotModuleManager<@NotNull OpenMinetopia> moduleManager) {
        super(moduleManager);
    }

    @Override
    public void onEnable() {
        // Check if core feature is enabled
        if (FeatureUtils.isFeatureDisabled("core")) {
            getLogger().info("Core feature is disabled in config.yml");
            return;
        }

        registerComponent(new OpenMinetopiaCommand());
    }
}
