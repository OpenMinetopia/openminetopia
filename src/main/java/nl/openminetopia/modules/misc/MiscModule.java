package nl.openminetopia.modules.misc;

import com.jazzkuh.modulemanager.spigot.SpigotModule;
import com.jazzkuh.modulemanager.spigot.SpigotModuleManager;
import nl.openminetopia.OpenMinetopia;
import org.jetbrains.annotations.NotNull;
import nl.openminetopia.modules.misc.commands.HeadCommand;
import nl.openminetopia.modules.misc.listeners.PlayerInteractListener;

public class MiscModule extends SpigotModule<@NotNull OpenMinetopia> {

    public MiscModule(SpigotModuleManager<@NotNull OpenMinetopia> moduleManager) {
        super(moduleManager);
    }

    @Override
    public void onEnable() {
        registerComponent(new HeadCommand());

        registerComponent(new PlayerInteractListener());
    }
}
