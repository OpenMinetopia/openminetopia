package nl.openminetopia.modules.lock.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import nl.openminetopia.api.player.PlayerManager;
import nl.openminetopia.api.player.objects.MinetopiaPlayer;
import nl.openminetopia.configuration.MessageConfiguration;
import nl.openminetopia.modules.lock.utils.LockUtil;
import nl.openminetopia.utils.ChatUtils;
import nl.openminetopia.utils.WorldGuardUtils;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

@CommandAlias("lock")
public class LockCommand extends BaseCommand {

    @Default
    public void defaultCommand(Player player) {
        Block targetBlock = player.getTargetBlockExact(5);
        if (!validateBlock(player, targetBlock)) return;
        if (LockUtil.isLocked(targetBlock)) {
            player.sendMessage(MessageConfiguration.component("lock_already_locked"));
            return;
        }
        MinetopiaPlayer minetopiaPlayer = PlayerManager.getInstance().getOnlineMinetopiaPlayer(player);
        player.sendMessage(ChatUtils.format(minetopiaPlayer, MessageConfiguration.message("lock_locked")));
        LockUtil.setLocked(targetBlock, player.getUniqueId());
    }

    @Subcommand("addmember")
    @CommandCompletion("@players")
    public void addMember(Player player, OfflinePlayer target) {
        Block targetBlock = player.getTargetBlockExact(5);
        if (!validateBlock(player, targetBlock)) return;
        if (!validateTarget(player, target)) return;

        MinetopiaPlayer minetopiaPlayer = PlayerManager.getInstance().getOnlineMinetopiaPlayer(player);
        player.sendMessage(ChatUtils.format(minetopiaPlayer, MessageConfiguration.message("lock_member_added")
                .replace("<player>", target.getName())));
        LockUtil.addLockMember(targetBlock, target.getUniqueId());
    }

    @Subcommand("removemember")
    @CommandCompletion("@players")
    public void removeMember(Player player, OfflinePlayer target) {
        Block targetBlock = player.getTargetBlockExact(5);
        if (!validateBlock(player, targetBlock)) return;
        if (!validateTarget(player, target)) return;

        MinetopiaPlayer minetopiaPlayer = PlayerManager.getInstance().getOnlineMinetopiaPlayer(player);
        player.sendMessage(ChatUtils.format(minetopiaPlayer, MessageConfiguration.message("lock_member_removed")
                .replace("<player>", target.getName())));
        LockUtil.removeLockMember(targetBlock, target.getUniqueId());
    }

    @Subcommand("addgroup")
    @CommandPermission("openminetopia.lock.group")
    public void addGroup(Player player, String group) {
        Block targetBlock = player.getTargetBlockExact(5);
        if (!validateBlock(player, targetBlock)) return;

        MinetopiaPlayer minetopiaPlayer = PlayerManager.getInstance().getOnlineMinetopiaPlayer(player);
        player.sendMessage(ChatUtils.format(minetopiaPlayer, MessageConfiguration.message("lock_group_added")
                .replace("<group>", group)));
        LockUtil.addLockGroup(targetBlock, group);
    }

    @Subcommand("removegroup")
    @CommandPermission("openminetopia.lock.group")
    public void removeGroup(Player player, String group) {
        Block targetBlock = player.getTargetBlockExact(5);
        if (!validateBlock(player, targetBlock)) return;

        MinetopiaPlayer minetopiaPlayer = PlayerManager.getInstance().getOnlineMinetopiaPlayer(player);
        player.sendMessage(ChatUtils.format(minetopiaPlayer, MessageConfiguration.message("lock_group_removed")
                .replace("<group>", group)));
        LockUtil.removeLockGroup(targetBlock, group);
    }

    private boolean validateBlock(Player player, Block targetBlock) {
        if (targetBlock == null || !LockUtil.isLockable(targetBlock)) {
            player.sendMessage(MessageConfiguration.component("lock_not_looking_at_block"));
            return false;
        }

        ProtectedRegion region = WorldGuardUtils.getProtectedRegion(targetBlock.getLocation(), p -> p >= 0);
        if (!player.hasPermission("openminetopia.lock")) {
            if (region == null) {
                player.sendMessage(MessageConfiguration.component("lock_not_on_plot"));
                return false;
            }
            if (!region.getOwners().contains(player.getUniqueId())) {
                player.sendMessage(MessageConfiguration.component("lock_not_owner"));
                return false;
            }
        }
        return true;
    }

    private boolean validateTarget(Player player, OfflinePlayer target) {
        if (target == null || !target.hasPlayedBefore()) {
            player.sendMessage(MessageConfiguration.component("lock_player_never_played"));
            return false;
        }

        if (target.getUniqueId().equals(player.getUniqueId())) {
            player.sendMessage(MessageConfiguration.component("lock_cannot_add_self"));
            return false;
        }
        return true;
    }
}
