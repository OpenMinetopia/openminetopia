package nl.openminetopia.modules.banking.commands.subcommands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import nl.openminetopia.modules.banking.menus.BankTypeSelectionMenu;
import org.bukkit.entity.Player;

@CommandAlias("accounts|account|rekening")
public class BankingOpenCommand extends BaseCommand {

    @Subcommand("open")
    @CommandPermission("openminetopia.banking.open")
    public void openBank(Player player) {
        new BankTypeSelectionMenu(player).open(player);
    }

}
