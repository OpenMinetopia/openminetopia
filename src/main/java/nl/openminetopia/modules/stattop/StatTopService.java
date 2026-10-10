package nl.openminetopia.modules.stattop;

import lombok.RequiredArgsConstructor;
import nl.openminetopia.api.player.PlayerManager;
import nl.openminetopia.api.player.objects.MinetopiaPlayer;
import nl.openminetopia.api.stattop.StatTopEntry;
import nl.openminetopia.api.stattop.StatTopType;
import nl.openminetopia.modules.stattop.configuration.StatTopConfiguration;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
public class StatTopService {

    private final StatTopModule module;
    private final Map<String, CachedTop> cache = new ConcurrentHashMap<>();

    public CompletableFuture<List<StatTopEntry>> getTop(StatTopType type) {
        StatTopConfiguration configuration = module.getConfiguration();

        CachedTop cached = cache.get(type.key());
        if (cached != null && System.currentTimeMillis() - cached.createdAt() < configuration.getCacheSeconds() * 1000L) {
            return CompletableFuture.completedFuture(cached.entries());
        }

        // Online players are saved periodically, so their in-memory value is more recent than the database.
        Map<UUID, Double> liveValues = new HashMap<>();
        for (MinetopiaPlayer player : PlayerManager.getInstance().getOnlinePlayers().values()) {
            Double value = type.live(player);
            if (value != null) liveValues.put(player.getUuid(), value);
        }

        int limit = configuration.getEntries();
        return type.stored(limit + liveValues.size()).thenApply(stored -> {
            Map<UUID, Double> values = new HashMap<>();
            stored.forEach(entry -> values.put(entry.uuid(), entry.value()));
            values.putAll(liveValues);

            List<StatTopEntry> top = values.entrySet().stream()
                    .map(entry -> new StatTopEntry(entry.getKey(), entry.getValue()))
                    .sorted(Comparator.comparingDouble(StatTopEntry::value).reversed())
                    .limit(limit)
                    .toList();

            cache.put(type.key(), new CachedTop(top, System.currentTimeMillis()));
            return top;
        });
    }

    public void clearCache() {
        cache.clear();
    }

    private record CachedTop(List<StatTopEntry> entries, long createdAt) {
    }
}
