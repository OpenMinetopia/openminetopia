package nl.openminetopia.utils.wrappers.listeners;

import de.oliver.fancynpcs.api.events.NpcInteractEvent;
import nl.openminetopia.utils.wrappers.CustomNpcClickEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class FancyNpcClickListener implements Listener {

    @EventHandler
    public void fancyClick(final NpcInteractEvent event) {
        CustomNpcClickEvent.ClickType clickType = switch (event.getInteractionType()) {
            case LEFT_CLICK -> CustomNpcClickEvent.ClickType.LEFT_CLICK;
            case RIGHT_CLICK -> CustomNpcClickEvent.ClickType.RIGHT_CLICK;
            default -> CustomNpcClickEvent.ClickType.RIGHT_CLICK;
        };
        CustomNpcClickEvent customNpcClickEvent = new CustomNpcClickEvent(event.getPlayer(), clickType, event.getNpc().getData().getName());
        if (event.isCancelled()) return;
        if (!customNpcClickEvent.callEvent()) event.setCancelled(true);
    }
}
