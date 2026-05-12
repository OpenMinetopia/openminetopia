package nl.openminetopia.modules.prefix.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.HelpCommand;
import nl.openminetopia.api.player.PlayerManager;
import nl.openminetopia.modules.prefix.menus.PrefixMenu;
import org.bukkit.entity.Player;

@CommandAlias("prefix")
public class PrefixCommand extends BaseCommand {

    @HelpCommand
    public void helpCommand(CommandHelp help) {
        help.showHelp();
    }

    @Default
    public void prefixCommand(Player player) {
        PlayerManager.getInstance().getMinetopiaPlayer(player).whenComplete((minetopiaPlayer, throwable) -> {
            if (minetopiaPlayer == null) return;
            new PrefixMenu(player, player, minetopiaPlayer).open(player);
        });
    }
}
