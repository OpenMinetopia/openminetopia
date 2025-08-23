package nl.openminetopia.modules.misc.listeners;

import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.api.places.MTPlaceManager;
import nl.openminetopia.configuration.MessageConfiguration;
import nl.openminetopia.modules.misc.objects.PvPItem;
import nl.openminetopia.modules.misc.utils.MiscUtils;
import nl.openminetopia.modules.police.handcuff.HandcuffManager;
import nl.openminetopia.utils.ChatUtils;
import nl.openminetopia.utils.FeatureUtils;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerAttackListener implements Listener {

    @EventHandler
    public void damagePlayer(final EntityDamageByEntityEvent event) {
        if (MTPlaceManager.getInstance().getPlace(event.getDamager().getLocation()) == null) return;
        if (!OpenMinetopia.getDefaultConfiguration().isPvpEnabled()) return;
        if (FeatureUtils.isFeatureDisabled("pvp")) return;

        // Anti-armorstand shooting
        if (event.getEntity() instanceof ArmorStand && event.getDamager() instanceof Arrow) {
            event.setCancelled(true);
            return;
        }

        if (event.getDamager() instanceof Snowball && ((Snowball) event.getDamager()).getShooter() instanceof Player player && event.getEntity() instanceof Player target) {
            PvPItem pvpItem = MiscUtils.getPvPItem(new ItemStack(Material.SNOWBALL));
            if (pvpItem == null) {
                event.setCancelled(true);
                return;
            }

            player.sendMessage(ChatUtils.color(pvpItem.attackerMessage().replace("<player>", target.getName())));
            target.sendMessage(ChatUtils.color(pvpItem.victimMessage().replace("<player>", player.getName())));
            return;
        }

        if (event.getDamager() instanceof Arrow && ((Arrow) event.getDamager()).getShooter() instanceof Player player && event.getEntity() instanceof Player target) {
            PvPItem pvpItem = MiscUtils.getPvPItem(new ItemStack(Material.ARROW));
            if (pvpItem == null) {
                event.setCancelled(true);
                return;
            }
            player.sendMessage(ChatUtils.color(pvpItem.attackerMessage().replace("<player>", target.getName())));
            target.sendMessage(ChatUtils.color(pvpItem.victimMessage().replace("<player>", player.getName())));
            return;
        }

        if (!(event.getDamager() instanceof Player player)) return;
        if (!(event.getEntity() instanceof Player target)) return;

        if (HandcuffManager.getInstance().isHandcuffed(player) && !OpenMinetopia.getDefaultConfiguration().isHandcuffCanPvP()) {
            event.setCancelled(true);
            player.sendMessage(ChatUtils.color(MessageConfiguration.message("police_handcuff_cant_pvp")));
            return;
        }

        if (player.getInventory().getItemInMainHand().getType() == Material.ARROW ||
                player.getInventory().getItemInMainHand().getType() == Material.SNOWBALL ||
                player.getInventory().getItemInMainHand().getType() == Material.AIR) {
            event.setCancelled(true);
            player.sendMessage(ChatUtils.color(MessageConfiguration.message("misc_pvp_disabled")));
            return;
        }

        PvPItem pvpItem = MiscUtils.getPvPItem(player.getInventory().getItemInMainHand());
        if (pvpItem == null) {
            event.setCancelled(true);
            player.sendMessage(ChatUtils.color(MessageConfiguration.message("misc_pvp_disabled")));
            return;
        }

        player.sendMessage(ChatUtils.color(pvpItem.attackerMessage().replace("<player>", target.getName())));
        target.sendMessage(ChatUtils.color(pvpItem.victimMessage().replace("<player>", player.getName())));
    }
}
