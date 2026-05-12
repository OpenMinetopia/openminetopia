package nl.openminetopia.modules.banking.commands.subcommands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import nl.openminetopia.modules.banking.menus.BankAccountListMenu;
import org.bukkit.entity.Player;

@CommandAlias("accounts|account|rekening")
public class BankingListCommand extends BaseCommand {

    @Subcommand("list")
    @CommandPermission("openminetopia.banking.list")
    public void listAccounts(Player player) {
        new BankAccountListMenu().open(player);
    }

}
