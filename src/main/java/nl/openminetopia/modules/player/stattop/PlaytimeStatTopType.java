package nl.openminetopia.modules.player.stattop;

import nl.openminetopia.api.player.objects.MinetopiaPlayer;
import nl.openminetopia.api.stattop.StatTopEntry;
import nl.openminetopia.api.stattop.StatTopType;
import nl.openminetopia.modules.data.utils.StormUtils;
import nl.openminetopia.modules.player.utils.PlaytimeUtil;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class PlaytimeStatTopType implements StatTopType {

    @Override
    public String key() {
        return "playtime";
    }

    @Override
    public String displayName() {
        return "Speeltijd";
    }

    @Override
    public CompletableFuture<List<StatTopEntry>> stored(int limit) {
        return StormUtils.queryTop("SELECT uuid, playtime AS score FROM players ORDER BY playtime DESC LIMIT ?", limit);
    }

    @Override
    public Double live(MinetopiaPlayer player) {
        return (double) player.getPlaytime();
    }

    @Override
    public String format(double value) {
        return PlaytimeUtil.formatPlaytime((long) value);
    }
}
