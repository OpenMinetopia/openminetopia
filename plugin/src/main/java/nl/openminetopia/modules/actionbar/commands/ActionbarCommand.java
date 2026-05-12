package nl.openminetopia.modules.actionbar.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import nl.openminetopia.api.player.PlayerManager;
import nl.openminetopia.api.player.objects.MinetopiaPlayer;
import org.bukkit.entity.Player;

@CommandAlias("actionbar|ab")
public class ActionbarCommand extends BaseCommand {

    @Default
    public void onCommand(Player player) {
        MinetopiaPlayer minetopiaPlayer = PlayerManager.getInstance().getOnlineMinetopiaPlayer(player);
        minetopiaPlayer.setActionbarVisible(!minetopiaPlayer.isActionbarVisible());
    }
}
