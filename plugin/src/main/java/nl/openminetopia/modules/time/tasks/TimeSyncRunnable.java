package nl.openminetopia.modules.time.tasks;

import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.modules.places.PlacesModule;
import nl.openminetopia.modules.places.models.WorldModel;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Calendar;
import java.util.TimeZone;

public class TimeSyncRunnable extends BukkitRunnable {


    @Override
    public void run() {
        String timeZone = OpenMinetopia.getDefaultConfiguration().getSyncTimeZone();
        PlacesModule placesModule = OpenMinetopia.getModuleManager().get(PlacesModule.class);
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(timeZone));

        for (WorldModel worldModel : placesModule.worldModels) {
            World bukkitWorld = Bukkit.getWorld(worldModel.getName());
            if (bukkitWorld == null) return;

            bukkitWorld.setTime((1000L * calendar.get(Calendar.HOUR_OF_DAY)) + (16 * (calendar.get(Calendar.MINUTE) + 1)) - 6000);
        }
    }
}
