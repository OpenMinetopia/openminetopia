package nl.openminetopia.modules.police.listeners;

import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.api.player.PlayerManager;
import nl.openminetopia.api.player.objects.MinetopiaPlayer;
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
                source.sendMessage(ChatUtils.color("<red>Je moet wachten tot jouw taser <dark_red>afgekoeld <red>is!"));
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
                source.sendMessage(ChatUtils.color("<red>Jouw taser is <dark_red>leeg<red>!"));
                return;
            }

            ItemStack finalItemStack = PersistentDataUtil.set(itemStack, currentUsages - 1, "openmt.usages");
            source.getInventory().setItemInMainHand(finalItemStack);
        }

        source.sendMessage(ChatUtils.color("<red>Je hebt <dark_red>" + target.getName() + " <red>geraakt met jouw taser!"));

        MinetopiaPlayer minetopiaPlayer = PlayerManager.getInstance().getOnlineMinetopiaPlayer(source);
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
