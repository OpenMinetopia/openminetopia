package nl.openminetopia.modules.police.listeners;

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import nl.openminetopia.modules.police.utils.BalaclavaUtils;
import nl.openminetopia.modules.police.utils.NightvisionUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.EquipmentSlot;

public class PlayerArmorChangeListener implements Listener {

    @EventHandler
    public void armorChange(PlayerArmorChangeEvent event) {
        if (event.getSlot() != EquipmentSlot.HEAD) return;

        /* ---- Balaclava ---- */

        if (BalaclavaUtils.isBalaclavaItem(event.getNewItem()) && !BalaclavaUtils.isBalaclavaItem(event.getOldItem())) {
            BalaclavaUtils.obfuscate(event.getPlayer(), true);
            return;
        }

        if (BalaclavaUtils.isBalaclavaItem(event.getOldItem()) && !BalaclavaUtils.isBalaclavaItem(event.getNewItem())) {
            BalaclavaUtils.obfuscate(event.getPlayer(), false);
        }

        /* ---- Night vision ---- */

        if (NightvisionUtils.isNightvisonItem(event.getNewItem()) && !NightvisionUtils.isNightvisonItem(event.getOldItem())) {
            NightvisionUtils.applyNightvisonEffects(event.getPlayer());
            return;
        }

        if (NightvisionUtils.isNightvisonItem(event.getOldItem()) && !NightvisionUtils.isNightvisonItem(event.getNewItem())) {
            NightvisionUtils.clearNightvisionEffects(event.getPlayer());
        }
    }
}
