package nl.openminetopia.api.stattop;

import nl.openminetopia.api.player.objects.MinetopiaPlayer;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface StatTopType {

    String key();

    String displayName();

    /**
     * The highest stored values, sorted descending. Online players are overlaid with {@link #live(MinetopiaPlayer)}.
     */
    CompletableFuture<List<StatTopEntry>> stored(int limit);

    @Nullable Double live(MinetopiaPlayer player);

    String format(double value);
}
