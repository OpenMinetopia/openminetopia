package nl.openminetopia.modules.teleporter.listeners.block;

import com.jeff_media.customblockdata.events.CustomBlockDataRemoveEvent;
import nl.openminetopia.modules.teleporter.utils.TeleporterUtil;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public final class TeleporterBreakListener implements Listener {

    @EventHandler
    public void blockBreak(final CustomBlockDataRemoveEvent event) {
        Block block = event.getBlock();
        if (!TeleporterUtil.isTeleporterBlock(block)) return;

        TeleporterUtil.removeTeleporter(block);
    }

}
