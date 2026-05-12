package nl.openminetopia.modules.lock.listeners;

import nl.openminetopia.api.player.PlayerManager;
import nl.openminetopia.api.player.objects.MinetopiaPlayer;
import nl.openminetopia.modules.lock.utils.LockUtil;
import nl.openminetopia.utils.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.data.Openable;
import org.bukkit.block.data.type.Door;
import org.bukkit.block.data.type.TrapDoor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.UUID;

public class LockInteractListener implements Listener {

    @EventHandler
    public void lockInteract(final PlayerInteractEvent event) {
        if (event.getHand() == null) return;
        if (event.getHand() != EquipmentSlot.HAND) return;
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        if (event.getClickedBlock() == null) return;
        if (!LockUtil.isLocked(event.getClickedBlock())) return;
        Player player = event.getPlayer();
        MinetopiaPlayer minetopiaPlayer = PlayerManager.getInstance().getOnlineMinetopiaPlayer(player);
        if (!LockUtil.canOpen(event.getClickedBlock(), player)) {
            event.setCancelled(true);
            ChatUtils.sendFormattedMessage(minetopiaPlayer,"<red>Dit slot is <dark_red>vergrendeld<red>!");
            return;
        }

        Block block = event.getClickedBlock();
        if (block.getBlockData() instanceof Openable door && block.getType().name().startsWith("IRON")) {
            door.setOpen(!door.isOpen());
            if (door.isOpen()) {
                player.getWorld().playSound(player.getLocation(), Sound.BLOCK_IRON_DOOR_OPEN, 3, 3);
            } else {
                player.getWorld().playSound(player.getLocation(), Sound.BLOCK_IRON_DOOR_CLOSE, 3, 3);
            }
            block.setBlockData(door);
        }

        UUID ownerUuid = LockUtil.getLockOwner(event.getClickedBlock());
        OfflinePlayer owner = Bukkit.getOfflinePlayer(ownerUuid);
        String ownerName = owner.getName() == null  ? "onbekend" : owner.getName();
        ChatUtils.sendFormattedMessage(minetopiaPlayer, "<gold>Je opent een slot van <yellow>" + ownerName + "<gold>.");
    }
}
