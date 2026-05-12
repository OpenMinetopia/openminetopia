package nl.openminetopia.modules.lock.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import nl.openminetopia.api.player.PlayerManager;
import nl.openminetopia.api.player.objects.MinetopiaPlayer;
import nl.openminetopia.modules.lock.utils.LockUtil;
import nl.openminetopia.utils.ChatUtils;
import nl.openminetopia.utils.WorldGuardUtils;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

@CommandAlias("unlock")
public class UnlockCommand extends BaseCommand {

    @Default
    public void defaultCommand(Player player) {
        Block targetBlock = player.getTargetBlockExact(5);
        MinetopiaPlayer minetopiaPlayer = PlayerManager.getInstance().getOnlineMinetopiaPlayer(player);
        if (targetBlock == null || !LockUtil.isLockable(targetBlock)) {
            ChatUtils.sendFormattedMessage(minetopiaPlayer, "<red>Je kijkt niet naar een blok!");
            return;
        }

        ProtectedRegion region = WorldGuardUtils.getProtectedRegion(targetBlock.getLocation(), p -> p >= 0);
        if (!player.hasPermission("openminetopia.unlock")) {
            if (region == null) {
                ChatUtils.sendFormattedMessage(minetopiaPlayer, "<red>Je staat niet op een plot!");
                return;
            }
            if (!region.getOwners().contains(player.getUniqueId())) {
                ChatUtils.sendFormattedMessage(minetopiaPlayer, "<red>Je bent geen eigenaar van dit plot!");
                return;
            }
        }

        if (!LockUtil.isLocked(targetBlock)) {
            ChatUtils.sendFormattedMessage(minetopiaPlayer, "<red>Dit blok is niet <dark_red>vergrendeld<red>!");
            return;
        }

        ChatUtils.sendFormattedMessage(minetopiaPlayer, "<gold>Je hebt de blok <yellow>ontgrendeld<gold>.");
        LockUtil.removeLock(targetBlock);
    }
}
