package nl.openminetopia.utils.modules;

import com.jazzkuh.modulemanager.spigot.SpigotModule;
import com.jazzkuh.modulemanager.spigot.SpigotModuleManager;
import nl.openminetopia.OpenMinetopia;
import org.jetbrains.annotations.NotNull;

public class ExtendedSpigotModule extends SpigotModule<@NotNull OpenMinetopia> {

    public ExtendedSpigotModule(SpigotModuleManager<@NotNull OpenMinetopia> moduleManager) {
        super(moduleManager);
    }

    @Override
    public boolean shouldLoad() {
        return !OpenMinetopia.getDefaultConfiguration().isModuleDisabled(this.getClass());
    }
}