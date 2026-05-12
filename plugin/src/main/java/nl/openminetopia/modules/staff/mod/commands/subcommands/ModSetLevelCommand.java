package nl.openminetopia.modules.staff.mod.commands.subcommands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import nl.openminetopia.api.player.PlayerManager;
import nl.openminetopia.modules.player.events.PlayerLevelChangeEvent;
import nl.openminetopia.utils.events.EventUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

@CommandAlias("mod")
public class ModSetLevelCommand extends BaseCommand {

    @Subcommand("setlevel")
    @Syntax("<player> <level>")
    @CommandPermission("openminetopia.mod.setlevel")
    @CommandCompletion("@players @range:1-100")
    @Description("Set the level of a player.")
    public void level(Player player, OfflinePlayer offlinePlayer, int newLevel) {
        if (offlinePlayer.getPlayer() == null) {
            player.sendMessage("This player does not exist.");
            return;
        }

        PlayerManager.getInstance().getMinetopiaPlayer(offlinePlayer.getPlayer()).whenComplete((minetopiaPlayer, throwable1) -> {
            if (minetopiaPlayer == null) return;
            int oldLevel = minetopiaPlayer.getLevel();

            PlayerLevelChangeEvent event = new PlayerLevelChangeEvent(offlinePlayer.getPlayer(), oldLevel, newLevel);
            if (EventUtils.callCancellable(event)) return;

            minetopiaPlayer.setLevel(newLevel);

            player.sendMessage("Set the level of the player to " + newLevel + ".");
        });
    }
}
