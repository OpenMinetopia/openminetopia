package nl.openminetopia.modules.actionbar;

import com.jazzkuh.modulemanager.spigot.SpigotModule;
import com.jazzkuh.modulemanager.spigot.SpigotModuleManager;
import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.modules.actionbar.commands.ActionbarCommand;
import nl.openminetopia.modules.actionbar.listeners.ActionbarJoinListener;
import nl.openminetopia.modules.data.DataModule;
import org.jetbrains.annotations.NotNull;

public class ActionbarModule extends SpigotModule<@NotNull OpenMinetopia> {
    public ActionbarModule(SpigotModuleManager<@NotNull OpenMinetopia> moduleManager, DataModule dataModule) {
        super(moduleManager);
    }

    @Override
    public void onEnable() {
        registerComponent(new ActionbarJoinListener());
        registerComponent(new ActionbarCommand());
    }
}
