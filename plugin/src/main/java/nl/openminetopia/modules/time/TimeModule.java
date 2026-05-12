package nl.openminetopia.modules.time;

import nl.openminetopia.utils.modules.ExtendedSpigotModule;
import com.jazzkuh.modulemanager.spigot.SpigotModuleManager;
import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.modules.data.DataModule;
import nl.openminetopia.modules.places.PlacesModule;
import nl.openminetopia.modules.time.tasks.TimeSyncRunnable;
import org.bukkit.Bukkit;
import org.bukkit.GameRules;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

public class TimeModule extends ExtendedSpigotModule {

    public TimeModule(SpigotModuleManager<@NotNull OpenMinetopia> moduleManager, DataModule dataModule) {
        super(moduleManager);
    }

    @Override
    public void onEnable() {
        if (!OpenMinetopia.getDefaultConfiguration().isSyncTime()) return;

        PlacesModule placesModule = OpenMinetopia.getModuleManager().get(PlacesModule.class);
        placesModule.worldModels.forEach(worldModel -> {
            World bukkitWorld = Bukkit.getWorld(worldModel.getName());
            if (bukkitWorld == null) return;

            bukkitWorld.setGameRule(GameRules.ADVANCE_TIME, false);
        });

        TimeSyncRunnable runnable = new TimeSyncRunnable();
        runnable.runTaskTimer(OpenMinetopia.getInstance(), 0L, 200L);
    }
}
