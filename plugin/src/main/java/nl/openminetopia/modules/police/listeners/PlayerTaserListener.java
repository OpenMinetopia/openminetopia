package nl.openminetopia.modules.police.listeners;

import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.api.player.PlayerManager;
import nl.openminetopia.api.player.objects.MinetopiaPlayer;
import nl.openminetopia.configuration.MessageConfiguration;
import nl.openminetopia.modules.police.PoliceModule;
import nl.openminetopia.modules.police.utils.TaserUtils;
import nl.openminetopia.utils.ChatUtils;
import nl.openminetopia.utils.PersistentDataUtil;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class PlayerTaserListener implements Listener {

    @EventHandler
    public void playerInteract(final PlayerInteractEntityEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;
        if (!(event.getRightClicked() instanceof Player target)) return;

        Player source = event.getPlayer();

        if (!TaserUtils.isTaserItem(source.getInventory().getItemInMainHand())) return;

        event.setCancelled(true);

        ItemStack itemStack = source.getInventory().getItemInMainHand();

        if (OpenMinetopia.getDefaultConfiguration().getTaserCooldown() > 0) {
            Long cooldown = PersistentDataUtil.getLong(itemStack, "openmt.cooldown");

            if (cooldown == null) cooldown = 0L;
            if (cooldown > System.currentTimeMillis()) {
                source.sendMessage(MessageConfiguration.component("police_taser_cooldown"));
                return;
            }

            ItemStack updatedItemStack = PersistentDataUtil.set(itemStack,
                    System.currentTimeMillis() + (OpenMinetopia.getDefaultConfiguration().getTaserCooldown() * 1000L),
                    "openmt.cooldown");
            source.getInventory().setItemInMainHand(updatedItemStack);
        }

        if (OpenMinetopia.getDefaultConfiguration().isTaserUsagesEnabled()) {
            if (PersistentDataUtil.getInteger(itemStack, "openmt.usages") == null) {
                ItemStack finalItemStack = PersistentDataUtil.set(itemStack,
                        OpenMinetopia.getDefaultConfiguration().getTaserMaxUsages(), "openmt.usages");
                source.getInventory().setItemInMainHand(finalItemStack);
            }

            Integer currentUsages = PersistentDataUtil.getInteger(itemStack, "openmt.usages");
            if (currentUsages == null || currentUsages <= 0) {
                source.sendMessage(MessageConfiguration.component("police_taser_empty"));
                return;
            }

            ItemStack finalItemStack = PersistentDataUtil.set(itemStack, currentUsages - 1, "openmt.usages");
            source.getInventory().setItemInMainHand(finalItemStack);
        }

        source.sendMessage(ChatUtils.color(MessageConfiguration.message("police_taser_hit")
                .replace("<player>", target.getName())));

        MinetopiaPlayer minetopiaPlayer = PlayerManager.getInstance().getOnlineMinetopiaPlayer(target);
        if (minetopiaPlayer == null) return;
        PoliceModule policeModule = OpenMinetopia.getModuleManager().get(PoliceModule.class);
        policeModule.getTaserManager().taser(minetopiaPlayer);
    }

    @EventHandler
    public void playerMove(final PlayerMoveEvent event) {
        PoliceModule policeModule = OpenMinetopia.getModuleManager().get(PoliceModule.class);
        if (!policeModule.getTaserManager().isTasered(event.getPlayer())) return;

        Location from = event.getFrom();
        Location to = event.getTo();

        if (from.y() > to.y()) return;
        if (from.x() != to.x() || from.z() != to.z()) {
            event.setCancelled(true);
        }
    }
}
