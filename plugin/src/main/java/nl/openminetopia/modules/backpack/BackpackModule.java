package nl.openminetopia.modules.backpack;

import com.jazzkuh.modulemanager.spigot.SpigotModuleManager;
import lombok.Getter;
import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.modules.backpack.commands.BackpackCommand;
import nl.openminetopia.modules.backpack.listeners.BackpackInteractListener;
import nl.openminetopia.modules.backpack.listeners.BackpackSessionListener;
import nl.openminetopia.modules.backpack.session.BackpackSessionManager;
import nl.openminetopia.utils.modules.ExtendedSpigotModule;
import org.jetbrains.annotations.NotNull;

@Getter
public class BackpackModule extends ExtendedSpigotModule {

    private BackpackSessionManager sessionManager;

    public BackpackModule(SpigotModuleManager<@NotNull OpenMinetopia> moduleManager) {
        super(moduleManager);
    }

    @Override
    public void onEnable() {
        this.sessionManager = new BackpackSessionManager();

        registerComponent(new BackpackCommand());
        registerComponent(new BackpackInteractListener());
        registerComponent(new BackpackSessionListener());
    }

    @Override
    public void onDisable() {
        if (sessionManager != null) {
            sessionManager.closeAll();
        }
    }
}