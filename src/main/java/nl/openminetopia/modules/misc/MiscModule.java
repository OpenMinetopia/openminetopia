package nl.openminetopia.modules.misc;

import com.jazzkuh.modulemanager.spigot.SpigotModule;
import com.jazzkuh.modulemanager.spigot.SpigotModuleManager;
import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.modules.misc.commands.HeadCommand;
import nl.openminetopia.modules.misc.listeners.PlayerAttackListener;
import nl.openminetopia.modules.misc.listeners.TrashcanListener;
import nl.openminetopia.utils.FeatureUtils;
import org.jetbrains.annotations.NotNull;

public class MiscModule extends SpigotModule<@NotNull OpenMinetopia> {

    public MiscModule(SpigotModuleManager<@NotNull OpenMinetopia> moduleManager) {
        super(moduleManager);
    }

    @Override
    public void onEnable() {
        // Check if misc features are enabled
        if (FeatureUtils.isFeatureDisabled("misc")) {
            getLogger().info("Misc features are disabled in config.yml");
            return;
        }

        registerComponent(new HeadCommand());

        registerComponent(new TrashcanListener());
        registerComponent(new PlayerAttackListener());
    }
}
