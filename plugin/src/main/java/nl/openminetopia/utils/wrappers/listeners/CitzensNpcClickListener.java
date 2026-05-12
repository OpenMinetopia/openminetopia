package nl.openminetopia.utils.wrappers.listeners;

import net.citizensnpcs.api.event.NPCLeftClickEvent;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import nl.openminetopia.utils.wrappers.CustomNpcClickEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class CitzensNpcClickListener implements Listener {

    @EventHandler
    public void citizensRightClick(final NPCRightClickEvent event) {
        CustomNpcClickEvent.ClickType clickType = CustomNpcClickEvent.ClickType.RIGHT_CLICK;
        CustomNpcClickEvent customNpcClickEvent = new CustomNpcClickEvent(event.getClicker(), clickType, event.getNPC().getName());
        if (event.isCancelled()) return;
        if (!customNpcClickEvent.callEvent()) event.setCancelled(true);
    }

    @EventHandler
    public void citizensLeftClick(final NPCLeftClickEvent event) {
        CustomNpcClickEvent.ClickType clickType = CustomNpcClickEvent.ClickType.LEFT_CLICK;
        CustomNpcClickEvent customNpcClickEvent = new CustomNpcClickEvent(event.getClicker(), clickType, event.getNPC().getName());
        if (event.isCancelled()) return;
        if (!customNpcClickEvent.callEvent()) event.setCancelled(true);
    }
}
