package nl.openminetopia.modules.police.listeners;

import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.api.player.PlayerManager;
import nl.openminetopia.api.player.objects.MinetopiaPlayer;
import nl.openminetopia.configuration.MessageConfiguration;
import nl.openminetopia.modules.police.utils.PeppersprayUtils;
import nl.openminetopia.utils.ChatUtils;
import nl.openminetopia.utils.PersistentDataUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class PlayerPeppersprayListener implements Listener {

    @EventHandler
    public void playerInteract(final PlayerInteractEntityEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;
        if (!(event.getRightClicked() instanceof Player target)) return;

        Player source = event.getPlayer();

        if (!PeppersprayUtils.isPeppersprayItem(source.getInventory().getItemInMainHand())) return;

        event.setCancelled(true);

        ItemStack itemStack = source.getInventory().getItemInMainHand();

        if (OpenMinetopia.getDefaultConfiguration().isPeppersprayUsagesEnabled()) {
            if (PersistentDataUtil.getInteger(itemStack, "openmt.usages") == null) {
                ItemStack finalItemStack = PersistentDataUtil.set(itemStack,
                        OpenMinetopia.getDefaultConfiguration().getPeppersprayMaxUsages(), "openmt.usages");
                source.getInventory().setItemInMainHand(finalItemStack);
            }

            Integer currentUsages = PersistentDataUtil.getInteger(itemStack, "openmt.usages");
            if (currentUsages == null || currentUsages <= 0) {
                source.sendMessage(MessageConfiguration.component("police_pepperspray_empty"));
                return;
            }

            ItemStack finalItemStack = PersistentDataUtil.set(itemStack, currentUsages - 1, "openmt.usages");
            source.getInventory().setItemInMainHand(finalItemStack);
        }

        source.sendMessage(ChatUtils.color(MessageConfiguration.message("police_pepperspray_used")
                .replace("<player>", target.getName())));

        MinetopiaPlayer sourceMinetopiaPlayer = PlayerManager.getInstance().getOnlineMinetopiaPlayer(source);
        if (sourceMinetopiaPlayer == null) return;

        target.sendMessage(ChatUtils.format(sourceMinetopiaPlayer, MessageConfiguration.message("police_pepperspray_target")));
        PeppersprayUtils.applyPeppersprayEffects(target);
    }
}
