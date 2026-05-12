package nl.openminetopia.modules.transactions.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.configuration.MessageConfiguration;
import nl.openminetopia.modules.banking.BankingModule;
import nl.openminetopia.modules.banking.menus.BankTransactionsMenu;
import nl.openminetopia.modules.banking.models.BankAccountModel;
import nl.openminetopia.modules.transactions.TransactionsModule;
import nl.openminetopia.utils.ChatUtils;
import org.bukkit.entity.Player;

@CommandAlias("transactions|transacties")
@CommandPermission("openminetopia.transactions")
public class TransactionCommand extends BaseCommand {

    private final TransactionsModule transactionsModule = OpenMinetopia.getModuleManager().get(TransactionsModule.class);
    private final BankingModule bankingModule = OpenMinetopia.getModuleManager().get(BankingModule.class);

    @Default
    @Syntax("<name>")
    @CommandCompletion("@accountNames")
    public void checkTransactionHistory(Player player, String accountName) {
        BankAccountModel accountModel = bankingModule.getAccountByName(accountName);

        if (accountModel == null) {
            ChatUtils.sendMessage(player, MessageConfiguration.message("banking_account_not_found"));
            return;
        }

        new BankTransactionsMenu(player, accountModel).open(player);
    }

}
