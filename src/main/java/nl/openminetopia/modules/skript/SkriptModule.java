package nl.openminetopia.modules.skript;

import ch.njol.skript.Skript;
import ch.njol.skript.SkriptAddon;
import lombok.Getter;
import nl.openminetopia.modules.data.DataModule;
import org.bukkit.Bukkit;
import com.jazzkuh.modulemanager.spigot.SpigotModule;
import com.jazzkuh.modulemanager.spigot.SpigotModuleManager;
import nl.openminetopia.OpenMinetopia;
import org.jetbrains.annotations.NotNull;
import java.io.IOException;

@Getter
public class SkriptModule extends SpigotModule<@NotNull OpenMinetopia> {

    public SkriptModule(SpigotModuleManager<@NotNull OpenMinetopia> moduleManager, DataModule dataModule) {
        super(moduleManager);
    }

    private SkriptAddon addon;

    @Override
    public void onEnable() {
        if(!Bukkit.getServer().getPluginManager().isPluginEnabled("Skript")) return;

        addon = Skript.registerAddon(OpenMinetopia.getInstance());
        try {
            addon.loadClasses("nl.openminetopia.modules.skript", "expressions");
            addon.loadClasses("nl.openminetopia.modules.skript", "effects");
        } catch (IOException e) {
            OpenMinetopia.getInstance().getLogger().severe("Failed to load classes for Skript module");
        }
    }
}
