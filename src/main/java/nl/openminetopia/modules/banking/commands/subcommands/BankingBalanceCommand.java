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
import nl.openminetopia.utils.ChatUtils;
import org.bukkit.command.CommandSender;

@CommandAlias("accounts|account|rekening")
public class BankingBalanceCommand extends BaseCommand {

    @Subcommand("setbalance")
    @CommandCompletion("@accountNames")
    @CommandPermission("openminetopia.banking.setbalance")
    public void setBalance(CommandSender sender, String accountName, double balance) {
        BankingModule bankingModule = OpenMinetopia.getModuleManager().get(BankingModule.class);
        BankAccountModel accountModel = bankingModule.getAccountByName(accountName);

        if (accountModel == null) {
            sender.sendMessage(MessageConfiguration.component("banking_account_not_found"));
            return;
        }

        accountModel.setBalance(balance);
        accountModel.save();
        sender.sendMessage(ChatUtils.color("<gold>De balans van <red>" + accountModel.getName() + " <gold>is nu ingesteld op <red>" + bankingModule.format(balance) + "<gold>."));
    }

}
