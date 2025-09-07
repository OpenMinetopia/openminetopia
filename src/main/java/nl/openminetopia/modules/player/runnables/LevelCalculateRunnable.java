package nl.openminetopia.modules.player.runnables;

import nl.openminetopia.api.player.PlayerManager;
import nl.openminetopia.api.player.objects.MinetopiaPlayer;
import nl.openminetopia.framework.runnables.AbstractDirtyRunnable;
import nl.openminetopia.modules.player.PlayerModule;
import nl.openminetopia.modules.player.configuration.LevelCheckConfiguration;
import nl.openminetopia.modules.player.events.PlayerLevelChangeEvent;
import nl.openminetopia.modules.player.utils.LevelUtil;
import nl.openminetopia.utils.events.EventUtils;


import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

public class LevelCalculateRunnable extends AbstractDirtyRunnable<UUID> {

    private final PlayerManager playerManager;
    private final PlayerModule playerModule;

    public LevelCalculateRunnable(PlayerModule playerModule, PlayerManager playerManager, long minIntervalMs, int batch, long heartbeatMs, Supplier<List<UUID>> allKeysSupplier) {
        super(minIntervalMs, batch, heartbeatMs, allKeysSupplier);
        this.playerModule = playerModule;
        this.playerManager = playerManager;
    }

    @Override
    protected void process(UUID key) {
        MinetopiaPlayer minetopiaPlayer = playerManager.getOnlinePlayers().get(key);
        if (minetopiaPlayer == null) return;

        int oldLevel = minetopiaPlayer.getLevel();
        LevelCheckConfiguration configuration = playerModule.getConfiguration();
        int calculatedLevel = LevelUtil.calculateLevel(minetopiaPlayer);
        if (configuration.isAutoLevelUp()) {
            PlayerLevelChangeEvent event = new PlayerLevelChangeEvent(minetopiaPlayer.getBukkit().getPlayer(), oldLevel, calculatedLevel);
            if (EventUtils.callCancellable(event)) return;

            minetopiaPlayer.setLevel(calculatedLevel);
        }
        minetopiaPlayer.setCalculatedLevel(calculatedLevel);
    }
}
