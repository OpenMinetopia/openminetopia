package nl.openminetopia.modules.lock.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import nl.openminetopia.api.player.PlayerManager;
import nl.openminetopia.api.player.objects.MinetopiaPlayer;
import nl.openminetopia.configuration.MessageConfiguration;
import nl.openminetopia.modules.lock.utils.KeyUtil;
import nl.openminetopia.modules.lock.utils.LockUtil;
import nl.openminetopia.utils.ChatUtils;
import nl.openminetopia.utils.WorldGuardUtils;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@CommandAlias("key|sleutel")
@CommandPermission("openminetopia.key")
public class KeyCommand extends BaseCommand {

    @Default
    public void defaultCommand(Player player) {
        player.sendMessage(MessageConfiguration.component("key_usage"));
    }

    @Subcommand("all|alle|master")
    public void all(Player player) {
        ItemStack key = resolveKey(player);
        KeyUtil.setMaster(key);
        giveOrUpdate(player, key, MessageConfiguration.message("key_master_received"));
    }

    @Subcommand("lock|slot")
    public void lock(Player player) {
        Block targetBlock = player.getTargetBlockExact(5);
        if (targetBlock == null || !LockUtil.isLocked(targetBlock)) {
            player.sendMessage(MessageConfiguration.component("key_not_looking_at_lock"));
            return;
        }

        ItemStack key = resolveKey(player);
        KeyUtil.addLock(key, targetBlock);
        giveOrUpdate(player, key, MessageConfiguration.message("key_lock_added"));
    }

    @Subcommand("plot")
    public void plot(Player player) {
        ProtectedRegion region = WorldGuardUtils.getProtectedRegion(player.getLocation(), p -> p >= 0);
        if (region == null) {
            player.sendMessage(MessageConfiguration.component("key_not_on_plot"));
            return;
        }

        ItemStack key = resolveKey(player);
        KeyUtil.addPlot(key, region.getId(), player.getWorld());
        giveOrUpdate(player, key, MessageConfiguration.message("key_plot_added"));
    }

    private ItemStack resolveKey(Player player) {
        ItemStack hand = player.getInventory().getItemInMainHand();
        return KeyUtil.isKey(hand) ? hand : KeyUtil.createKey();
    }

    private void giveOrUpdate(Player player, ItemStack key, String message) {
        MinetopiaPlayer minetopiaPlayer = PlayerManager.getInstance().getOnlineMinetopiaPlayer(player);
        if (KeyUtil.isKey(player.getInventory().getItemInMainHand())) {
            player.getInventory().setItemInMainHand(key);
        } else {
            player.getInventory().addItem(key);
        }
        player.sendMessage(ChatUtils.format(minetopiaPlayer, message));
    }
}
