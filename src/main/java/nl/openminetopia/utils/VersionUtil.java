package nl.openminetopia.utils;

import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;

@UtilityClass
public class VersionUtil {

    // Checks if the current server version is compatible with the given minimum version.
    public boolean isCompatible(String minVersion) {
        String currentVersion = Bukkit.getMinecraftVersion();
        if (minVersion == null) return false;
        if (minVersion.isEmpty()) return true;
        if (currentVersion.isEmpty()) return false;
        String[] currentParts = currentVersion.split("\\.");
        String[] minParts = minVersion.split("\\.");
        for (int i = 0; i < Math.min(currentParts.length, minParts.length); i++) {
            int currentPart = Integer.parseInt(currentParts[i]);
            int minPart = Integer.parseInt(minParts[i]);
            if (currentPart < minPart) {
                return false;
            } else if (currentPart > minPart) {
                return true;
            }
        }
        return currentParts.length >= minParts.length;
    }
}
