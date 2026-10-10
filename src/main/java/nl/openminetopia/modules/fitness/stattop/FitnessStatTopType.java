package nl.openminetopia.modules.fitness.stattop;

import lombok.RequiredArgsConstructor;
import nl.openminetopia.api.player.objects.MinetopiaPlayer;
import nl.openminetopia.api.stattop.StatTopEntry;
import nl.openminetopia.api.stattop.StatTopType;
import nl.openminetopia.modules.data.utils.StormUtils;
import nl.openminetopia.modules.fitness.FitnessModule;
import nl.openminetopia.modules.fitness.configuration.FitnessConfiguration;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
public class FitnessStatTopType implements StatTopType {

    private static final String QUERY = """
            SELECT p.uuid AS uuid,
                COALESCE((SELECT SUM(s.fitness_gained) FROM fitness_statistics s WHERE s.player_id = p.id), 0)
                + COALESCE((SELECT SUM(b.amount) FROM fitness_boosters b WHERE b.player_id = p.id AND (b.expires_at = -1 OR b.expires_at > ?)), 0) AS score
            FROM players p
            ORDER BY score DESC
            LIMIT ?
            """;

    private final FitnessModule fitnessModule;

    @Override
    public String key() {
        return "fitness";
    }

    @Override
    public String displayName() {
        return "Fitheid";
    }

    @Override
    public CompletableFuture<List<StatTopEntry>> stored(int limit) {
        return StormUtils.queryTop(QUERY, System.currentTimeMillis(), limit).thenApply(entries -> {
            FitnessConfiguration configuration = fitnessModule.getConfiguration();
            return entries.stream()
                    .map(entry -> {
                        double total = Math.max(1, entry.value() + configuration.getDefaultFitnessLevel());
                        return new StatTopEntry(entry.uuid(), Math.min(total, configuration.getMaxFitnessLevel()));
                    })
                    .toList();
        });
    }

    @Override
    public Double live(MinetopiaPlayer player) {
        if (player.getFitness() == null) return null;
        return (double) player.getFitness().getTotalFitness();
    }

    @Override
    public String format(double value) {
        return String.valueOf((long) value);
    }
}
