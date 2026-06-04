package nl.openminetopia.modules.lock;

import nl.openminetopia.utils.modules.ExtendedSpigotModule;
import com.jazzkuh.modulemanager.spigot.SpigotModuleManager;
import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.modules.lock.commands.KeyCommand;
import nl.openminetopia.modules.lock.commands.LockCommand;
import nl.openminetopia.modules.lock.commands.UnlockCommand;
import nl.openminetopia.modules.lock.listeners.LockInteractListener;
import nl.openminetopia.modules.lock.listeners.LockPlaceListener;
import org.jetbrains.annotations.NotNull;

public class LockModule extends ExtendedSpigotModule {
    public LockModule(SpigotModuleManager<@NotNull OpenMinetopia> moduleManager) {
        super(moduleManager);
    }

    @Override
    public void onEnable() {
        registerComponent(new LockCommand());
        registerComponent(new UnlockCommand());
        registerComponent(new KeyCommand());
        registerComponent(new LockInteractListener());
        registerComponent(new LockPlaceListener());
    }
}
