package nl.openminetopia.modules.banking.commands.subcommands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.configuration.MessageConfiguration;
import nl.openminetopia.modules.banking.BankingModule;
import nl.openminetopia.modules.banking.models.BankAccountModel;
import nl.openminetopia.modules.transactions.TransactionsModule;
import nl.openminetopia.modules.transactions.enums.TransactionType;
import nl.openminetopia.modules.transactions.events.TransactionUpdateEvent;
import nl.openminetopia.utils.ChatUtils;
import nl.openminetopia.utils.events.EventUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

@CommandAlias("accounts|account|rekening")
public class BankingBalanceCommand extends BaseCommand {

    @Subcommand("setbalance")
    @CommandCompletion("@accountNames")
    @CommandPermission("openminetopia.banking.setbalance")
    public void setBalance(CommandSender sender, String accountName, double balance) {
        BankingModule bankingModule = OpenMinetopia.getModuleManager().get(BankingModule.class);
        bankingModule.getAccountByNameAsync(accountName).whenComplete((accountModel, throwable) -> {
            if (accountModel == null) {
                sender.sendMessage(MessageConfiguration.component("banking_account_not_found"));
                return;
            }

            UUID executorUuid = ((sender instanceof Player executor) ? executor.getUniqueId() : new UUID(0, 0));
            String username = ((sender instanceof Player executor) ? executor.getName() : "Console");

            TransactionUpdateEvent event = new TransactionUpdateEvent(executorUuid, username, TransactionType.SET, balance, accountModel, "Set via '/account setbalance'", System.currentTimeMillis());
            if (EventUtils.callCancellable(event)) {
                sender.sendMessage(MessageConfiguration.component("banking_transaction_cancelled_by_plugin"));
                return;
            }

            accountModel.setBalance(balance); 
            accountModel.save();

            TransactionsModule transactionsModule = OpenMinetopia.getModuleManager().get(TransactionsModule.class);
            transactionsModule.createTransactionLog(System.currentTimeMillis(), executorUuid, username, TransactionType.SET, balance, accountModel.getUniqueId(), "Set via '/account setbalance'");

            sender.sendMessage(ChatUtils.color(MessageConfiguration.message("banking_balance_set")
                    .replace("<account_name>", accountModel.getName())
                    .replace("<balance>", bankingModule.format(balance))));
        });
    }
}