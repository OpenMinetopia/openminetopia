package nl.openminetopia.modules.misc.listeners;

import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.api.places.MTPlaceManager;
import nl.openminetopia.api.player.PlayerManager;
import nl.openminetopia.api.player.objects.MinetopiaPlayer;
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
            sendAttackedMessages(player, target, pvpItem);
            return;
        }

        if (event.getDamager() instanceof Arrow && ((Arrow) event.getDamager()).getShooter() instanceof Player player && event.getEntity() instanceof Player target) {
            PvPItem pvpItem = MiscUtils.getPvPItem(new ItemStack(Material.ARROW));
            if (pvpItem == null) {
                event.setCancelled(true);
                return;
            }
            sendAttackedMessages(player, target, pvpItem);
            return;
        }

        if (!(event.getDamager() instanceof Player player)) return;
        if (!(event.getEntity() instanceof Player target)) return;

        MinetopiaPlayer minetopiaPlayer = PlayerManager.getInstance().getOnlineMinetopiaPlayer(player);
        if (HandcuffManager.getInstance().isHandcuffed(player) && !OpenMinetopia.getDefaultConfiguration().isHandcuffCanPvP()) {
            event.setCancelled(true);
            player.sendMessage(ChatUtils.format(minetopiaPlayer, MessageConfiguration.message("police_handcuff_cant_pvp")
                    .replace("<victim>", target.getName())
                    .replace("<attacker>", player.getName())
            ));
            return;
        }

        if (player.getInventory().getItemInMainHand().getType() == Material.ARROW ||
                player.getInventory().getItemInMainHand().getType() == Material.SNOWBALL ||
                player.getInventory().getItemInMainHand().getType() == Material.AIR) {
            event.setCancelled(true);
            player.sendMessage(ChatUtils.format(minetopiaPlayer, MessageConfiguration.message("misc_pvp_disabled")
                    .replace("<victim>", target.getName())
                    .replace("<attacker>", player.getName())
            ));
            return;
        }

        PvPItem pvpItem = MiscUtils.getPvPItem(player.getInventory().getItemInMainHand());
        if (pvpItem == null) {
            event.setCancelled(true);
            player.sendMessage(ChatUtils.format(minetopiaPlayer, MessageConfiguration.message("misc_pvp_disabled")
                    .replace("<victim>", target.getName())
                    .replace("<attacker>", player.getName())
            ));
            return;
        }

        sendAttackedMessages(player, target, pvpItem);
    }

    private void sendAttackedMessages(Player player, Player target, PvPItem pvpItem) {
        MinetopiaPlayer minetopiaPlayer = PlayerManager.getInstance().getOnlineMinetopiaPlayer(player);
        MinetopiaPlayer targetMinetopiaPlayer = PlayerManager.getInstance().getOnlineMinetopiaPlayer(target);

        player.sendMessage(ChatUtils.format(minetopiaPlayer, pvpItem.attackerMessage()
                .replace("<attacker>", player.getName()).replace("<victim>", target.getName())));
        target.sendMessage(ChatUtils.format(targetMinetopiaPlayer, pvpItem.victimMessage()
                .replace("<attacker>", player.getName()).replace("<victim>", target.getName())));
    }
}
