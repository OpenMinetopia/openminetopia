package nl.openminetopia.modules.plots.utils;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import lombok.experimental.UtilityClass;
import nl.openminetopia.modules.plots.PlotModule;
import nl.openminetopia.utils.WorldGuardUtils;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.List;

@UtilityClass
public class PlotUtil {

    public ProtectedRegion getPlot(World world, String name) {
        return WorldGuardUtils.getProtectedRegions(world, p -> p >= 0)
                .stream()
                .filter(r -> r.getId().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    public ProtectedRegion getPlot(Location location) {
        List<ProtectedRegion> regions = WorldGuardUtils.getProtectedRegions(location, priority -> priority >= 0);
        if (regions == null || regions.isEmpty()) return null;

        for (ProtectedRegion region : regions) {
            if (region.getFlag(PlotModule.PLOT_FLAG) == null) continue;
            return region;
        }
        return null;
    }
}
