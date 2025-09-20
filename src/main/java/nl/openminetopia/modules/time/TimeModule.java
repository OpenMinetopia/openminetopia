package nl.openminetopia.modules.time;

import com.jazzkuh.modulemanager.spigot.SpigotModule;
import com.jazzkuh.modulemanager.spigot.SpigotModuleManager;
import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.modules.data.DataModule;
import nl.openminetopia.modules.places.PlacesModule;
import nl.openminetopia.modules.time.tasks.TimeSyncRunnable;
import nl.openminetopia.utils.FeatureUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

public class TimeModule extends SpigotModule<@NotNull OpenMinetopia> {

    public TimeModule(SpigotModuleManager<@NotNull OpenMinetopia> moduleManager, DataModule dataModule) {
        super(moduleManager);
    }

    @Override
    public void onEnable() {
        if (!OpenMinetopia.getDefaultConfiguration().isSyncTime()) return;

        // Check if time feature is enabled
        if (FeatureUtils.isFeatureDisabled("time")) {
            getLogger().info("Time feature is disabled in config.yml");
            return;
        }

        PlacesModule placesModule = OpenMinetopia.getModuleManager().get(PlacesModule.class);
        placesModule.worldModels.forEach(worldModel -> {
            World bukkitWorld = Bukkit.getWorld(worldModel.getName());
            if (bukkitWorld == null) return;

            bukkitWorld.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
        });

        TimeSyncRunnable runnable = new TimeSyncRunnable();
        runnable.runTaskTimer(OpenMinetopia.getInstance(), 0L, 200L);
    }
}
