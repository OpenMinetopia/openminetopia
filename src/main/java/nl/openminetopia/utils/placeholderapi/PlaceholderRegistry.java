package nl.openminetopia.utils.placeholderapi;

import lombok.experimental.UtilityClass;
import nl.openminetopia.api.player.objects.MinetopiaPlayer;
import org.bukkit.OfflinePlayer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;

/**
 * Lets modules add placeholders without touching {@link OpenMinetopiaExpansion}.
 */
@UtilityClass
public class PlaceholderRegistry {

    private final Map<String, BiFunction<OfflinePlayer, MinetopiaPlayer, String>> placeholders = new ConcurrentHashMap<>();

    public void register(String key, BiFunction<OfflinePlayer, MinetopiaPlayer, String> resolver) {
        placeholders.put(key.toLowerCase(), resolver);
    }

    public void unregister(String key) {
        placeholders.remove(key.toLowerCase());
    }

    public String resolve(String key, OfflinePlayer player, MinetopiaPlayer minetopiaPlayer) {
        BiFunction<OfflinePlayer, MinetopiaPlayer, String> resolver = placeholders.get(key.toLowerCase());
        if (resolver == null) return null;
        return resolver.apply(player, minetopiaPlayer);
    }
}
