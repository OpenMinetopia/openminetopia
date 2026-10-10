package nl.openminetopia.modules.player.stattop;

import nl.openminetopia.api.player.objects.MinetopiaPlayer;
import nl.openminetopia.api.stattop.StatTopEntry;
import nl.openminetopia.api.stattop.StatTopType;
import nl.openminetopia.modules.data.utils.StormUtils;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class LevelStatTopType implements StatTopType {

    @Override
    public String key() {
        return "level";
    }

    @Override
    public String displayName() {
        return "Level";
    }

    @Override
    public CompletableFuture<List<StatTopEntry>> stored(int limit) {
        return StormUtils.queryTop("SELECT uuid, level AS score FROM players ORDER BY level DESC LIMIT ?", limit);
    }

    @Override
    public Double live(MinetopiaPlayer player) {
        return (double) player.getLevel();
    }

    @Override
    public String format(double value) {
        return String.valueOf((long) value);
    }
}
