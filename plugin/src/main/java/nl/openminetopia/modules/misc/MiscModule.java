package nl.openminetopia.modules.misc;

import nl.openminetopia.utils.modules.ExtendedSpigotModule;
import com.jazzkuh.modulemanager.spigot.SpigotModuleManager;
import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.modules.misc.commands.HeadCommand;
import nl.openminetopia.modules.misc.listeners.PlayerAttackListener;
import nl.openminetopia.modules.misc.listeners.TrashcanListener;
import org.jetbrains.annotations.NotNull;

public class MiscModule extends ExtendedSpigotModule {

    public MiscModule(SpigotModuleManager<@NotNull OpenMinetopia> moduleManager) {
        super(moduleManager);
    }

    @Override
    public void onEnable() {
        registerComponent(new HeadCommand());

        registerComponent(new TrashcanListener());
        registerComponent(new PlayerAttackListener());
    }
}
