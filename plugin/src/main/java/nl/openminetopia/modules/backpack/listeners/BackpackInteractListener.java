package nl.openminetopia.modules.backpack.listeners;

import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.modules.backpack.item.BackpackItem;
import nl.openminetopia.modules.backpack.BackpackModule;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class BackpackInteractListener implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;

        Action action = event.getAction();
        if (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK) return;

        ItemStack item = event.getItem();
        if (!BackpackItem.isBackpack(item)) return;

        event.setCancelled(true);

        BackpackModule module = OpenMinetopia.getModuleManager().get(BackpackModule.class);
        module.getSessionManager().open(event.getPlayer(), item);
    }
}