package nl.openminetopia.modules.scoreboard.runnables;

import nl.openminetopia.api.player.PlayerManager;
import nl.openminetopia.api.player.ScoreboardManager;
import nl.openminetopia.api.player.objects.MinetopiaPlayer;
import nl.openminetopia.framework.runnables.AbstractDirtyRunnable;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

public class ScoreboardUpdateRunnable extends AbstractDirtyRunnable<UUID> {

    private final ScoreboardManager scoreboardManager;
    private final PlayerManager playerManager;

    public ScoreboardUpdateRunnable(ScoreboardManager scoreboardManager, PlayerManager playerManager, long minIntervalMs, int batch, long heartbeatMs, Supplier<List<UUID>> allKeysSupplier) {
        super(minIntervalMs, batch, heartbeatMs, allKeysSupplier);
        this.scoreboardManager = scoreboardManager;
        this.playerManager = playerManager;
    }

    @Override
    protected void process(UUID key) {
        Player player = Bukkit.getPlayer(key);
        if (player == null || !player.isOnline()) {
            scoreboardManager.removeScoreboard(key);
            remove(key);
            return;
        }
        MinetopiaPlayer mtp = playerManager.getOnlineMinetopiaPlayer(player);
        if (mtp == null) return;

        scoreboardManager.updateBoard(mtp);
    }
}
