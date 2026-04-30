package nl.openminetopia.modules.police.handcuff.listeners;

import nl.openminetopia.api.player.PlayerManager;
import nl.openminetopia.api.player.objects.MinetopiaPlayer;
import nl.openminetopia.configuration.MessageConfiguration;
import nl.openminetopia.modules.police.handcuff.HandcuffManager;
import nl.openminetopia.modules.police.handcuff.objects.HandcuffedPlayer;
import nl.openminetopia.modules.police.utils.HandcuffUtils;
import nl.openminetopia.utils.ChatUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;

public class PlayerHandcuffListener implements Listener {

    @EventHandler
    public void playerInteract(final PlayerInteractEntityEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;
        if (!(event.getRightClicked() instanceof Player target)) return;

        Player source = event.getPlayer();

        if (!HandcuffUtils.isHandcuffItem(source.getInventory().getItemInMainHand())) return;
        if (!event.getPlayer().hasPermission("openminetopia.handcuff")) return;

        if (HandcuffManager.getInstance().isHandcuffed(target)) {
            HandcuffedPlayer handcuffedPlayer = HandcuffManager.getInstance().getHandcuffedPlayer(target);
            HandcuffManager.getInstance().release(handcuffedPlayer);
            source.sendMessage(ChatUtils.color(MessageConfiguration.message("police_handcuff_removed")
                    .replace("<player>", target.getName())));
            return;
        }

        if (HandcuffManager.getInstance().isHandcuffing(source)) {
            source.sendMessage(MessageConfiguration.component("police_handcuff_already_handcuffing"));
            return;
        }

        event.setCancelled(true);

        source.sendMessage(ChatUtils.color(MessageConfiguration.message("police_handcuff_applied")
                .replace("<player>", target.getName())));

        MinetopiaPlayer targetMinetopiaPlayer = PlayerManager.getInstance().getOnlineMinetopiaPlayer(target);
        if (targetMinetopiaPlayer == null) return;

        MinetopiaPlayer sourceMinetopiaPlayer = PlayerManager.getInstance().getOnlineMinetopiaPlayer(source);
        if (sourceMinetopiaPlayer == null) return;

        HandcuffManager.getInstance().handcuff(targetMinetopiaPlayer, sourceMinetopiaPlayer);
    }
}
