package nl.openminetopia.modules.skript;

import ch.njol.skript.Skript;
import ch.njol.skript.SkriptAddon;
import com.jazzkuh.modulemanager.spigot.SpigotModule;
import com.jazzkuh.modulemanager.spigot.SpigotModuleManager;
import lombok.Getter;
import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.modules.data.DataModule;
import nl.openminetopia.utils.FeatureUtils;
import org.bukkit.Bukkit;
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

        // Check if skript feature is enabled
        if (FeatureUtils.isFeatureDisabled("skript")) {
            getLogger().info("Skript feature is disabled in config.yml");
            return;
        }

        addon = Skript.registerAddon(OpenMinetopia.getInstance());
        try {
            addon.loadClasses("nl.openminetopia.modules.skript", "expressions");
            addon.loadClasses("nl.openminetopia.modules.skript", "effects");
        } catch (IOException e) {
            OpenMinetopia.getInstance().getLogger().severe("Failed to load classes for Skript module");
        }
    }
}
