package nl.openminetopia.modules.police.walkietalkie.listeners;

import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.api.player.PlayerManager;
import nl.openminetopia.api.player.objects.MinetopiaPlayer;
import nl.openminetopia.modules.police.walkietalkie.menus.WalkieTalkieMenu;
import nl.openminetopia.utils.item.ItemUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerInteractListener implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        if (item == null) return;

        if (!ItemUtils.isSimilarToAny(item, OpenMinetopia.getDefaultConfiguration().getWalkieTalkieItems())) return;
        if (!event.getPlayer().hasPermission("openminetopia.walkietalkie")) return;

        MinetopiaPlayer minetopiaPlayer = PlayerManager.getInstance().getOnlineMinetopiaPlayer(event.getPlayer());
        if (minetopiaPlayer == null) return;
        new WalkieTalkieMenu(event.getPlayer()).open(event.getPlayer());
    }
}