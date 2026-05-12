package nl.openminetopia.modules.actionbar.runnables;

import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.api.player.PlayerManager;
import nl.openminetopia.api.player.objects.MinetopiaPlayer;
import nl.openminetopia.configuration.DefaultConfiguration;
import nl.openminetopia.framework.runnables.AbstractDirtyRunnable;
import nl.openminetopia.utils.ChatUtils;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Supplier;

public class ActionbarRunnable extends AbstractDirtyRunnable<UUID> {

    private final PlayerManager playerManager;

    public ActionbarRunnable(PlayerManager playerManager, long minIntervalMs, int batch, long heartbeatMs, Supplier<List<UUID>> allKeysSupplier) {
        super(minIntervalMs, batch, heartbeatMs, allKeysSupplier);
        this.playerManager = playerManager;
    }

    @Override
    protected void process(UUID key) {
        DefaultConfiguration configuration = OpenMinetopia.getDefaultConfiguration();
        if (!configuration.isActionbarEnabled()) return;
        MinetopiaPlayer minetopiaPlayer = playerManager.getOnlinePlayers().get(key);
        if (minetopiaPlayer == null) return;
        if (!minetopiaPlayer.isActionbarVisible()) return;
        if (!minetopiaPlayer.isInPlace()) return;
        Objects.requireNonNull(minetopiaPlayer.getBukkit().getPlayer()).sendActionBar(ChatUtils.format(minetopiaPlayer, configuration.getActionbarText()));
    }
}
