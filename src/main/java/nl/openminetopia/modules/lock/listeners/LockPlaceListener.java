package nl.openminetopia.modules.lock.listeners;

import nl.openminetopia.modules.lock.utils.LockUtil;
import org.bukkit.block.data.type.Door;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class LockPlaceListener implements Listener {

    @EventHandler
    public void onPlace(final BlockPlaceEvent event) {
        if (!(event.getBlockPlaced().getBlockData() instanceof Door)) return;
        LockUtil.inheritDoubleDoorLock(event.getBlockPlaced());
    }
}
