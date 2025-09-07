package nl.openminetopia.modules.player.runnables;

import nl.openminetopia.api.player.PlayerManager;
import nl.openminetopia.api.player.objects.MinetopiaPlayer;
import nl.openminetopia.framework.runnables.AbstractDirtyRunnable;

import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

public class PlayerPlaytimeRunnable extends AbstractDirtyRunnable<UUID> {

    private final PlayerManager playerManager;

    public PlayerPlaytimeRunnable(PlayerManager playerManager, long minIntervalMs, int batch, long heartbeatMs, Supplier<List<UUID>> allKeysSupplier, boolean async) {
        super(minIntervalMs, batch, heartbeatMs, allKeysSupplier, async);
        this.playerManager = playerManager;
    }

    @Override
    protected void process(UUID key) {
        MinetopiaPlayer minetopiaPlayer = playerManager.getOnlinePlayers().get(key);
        if (minetopiaPlayer == null) return;
        minetopiaPlayer.updatePlaytime();
    }
}
