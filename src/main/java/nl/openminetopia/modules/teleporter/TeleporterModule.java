package nl.openminetopia.modules.teleporter;

import com.jazzkuh.modulemanager.spigot.SpigotModule;
import com.jazzkuh.modulemanager.spigot.SpigotModuleManager;
import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.modules.data.DataModule;
import nl.openminetopia.modules.teleporter.commands.TeleporterCommand;
import nl.openminetopia.modules.teleporter.commands.subcommands.TeleporterCreateCommand;
import nl.openminetopia.modules.teleporter.listeners.TeleporterInteractListener;
import nl.openminetopia.modules.teleporter.listeners.block.TeleporterBreakListener;
import nl.openminetopia.modules.teleporter.listeners.block.TeleporterPlaceListener;
import nl.openminetopia.utils.FeatureUtils;
import org.jetbrains.annotations.NotNull;

public final class TeleporterModule extends SpigotModule<@NotNull OpenMinetopia> {

    public TeleporterModule(SpigotModuleManager<@NotNull OpenMinetopia> moduleManager, DataModule dataModule) {
        super(moduleManager);
    }

    @Override
    public void onEnable() {
        // Check if teleporter feature is enabled
        if (FeatureUtils.isFeatureDisabled("teleporter")) {
            getLogger().info("Teleporter feature is disabled in config.yml");
            return;
        }

        registerComponent(new TeleporterCommand());
        registerComponent(new TeleporterCreateCommand());

        registerComponent(new TeleporterPlaceListener());
        registerComponent(new TeleporterInteractListener());
        registerComponent(new TeleporterBreakListener());
    }



}
