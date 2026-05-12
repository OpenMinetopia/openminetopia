package nl.openminetopia.modules.fitness.runnables;

import nl.openminetopia.api.player.PlayerManager;
import nl.openminetopia.api.player.objects.MinetopiaPlayer;
import nl.openminetopia.framework.runnables.AbstractDirtyRunnable;
import nl.openminetopia.modules.fitness.utils.FitnessUtils;
import nl.openminetopia.modules.player.utils.PlaytimeUtil;


import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

public class HealthStatisticRunnable extends AbstractDirtyRunnable<UUID> {

    private final PlayerManager playerManager;

    public HealthStatisticRunnable(PlayerManager playerManager, long minIntervalMs, int batch, long heartbeatMs, Supplier<List<UUID>> allKeysSupplier) {
        super(minIntervalMs, batch, heartbeatMs, allKeysSupplier);
        this.playerManager = playerManager;
    }

    @Override
    protected void process(UUID key) {
        MinetopiaPlayer minetopiaPlayer = playerManager.getOnlinePlayers().get(key);
        if (minetopiaPlayer == null) return;
        if (PlaytimeUtil.minutes(minetopiaPlayer.getPlaytime()) % 60 == 0) FitnessUtils.performHealthCheck(minetopiaPlayer);
    }

}