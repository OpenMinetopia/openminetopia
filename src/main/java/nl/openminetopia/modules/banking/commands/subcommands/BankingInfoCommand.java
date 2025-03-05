package nl.openminetopia.modules.banking.commands.subcommands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.api.player.PlayerManager;
import nl.openminetopia.configuration.MessageConfiguration;
import nl.openminetopia.modules.banking.BankingModule;
import nl.openminetopia.modules.banking.models.BankAccountModel;
import nl.openminetopia.utils.ChatUtils;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

@CommandAlias("accounts|account|rekening")
public class BankingInfoCommand extends BaseCommand {

    @Subcommand("info")
    @CommandCompletion("@accountNames")
    @CommandPermission("openminetopia.banking.info")
    public void infoAccount(CommandSender sender, String accountName) {
        BankingModule bankingModule = OpenMinetopia.getModuleManager().get(BankingModule.class);
        BankAccountModel accountModel = bankingModule.getAccountByName(accountName);

        PlayerManager.getInstance().getMinetopiaPlayer((OfflinePlayer) sender).whenComplete((minetopiaPlayer, throwable1 ) -> {
            if (accountModel == null) {
                ChatUtils.sendFormattedMessage(minetopiaPlayer, MessageConfiguration.message("banking_account_not_found"));
                return;
            }

            ChatUtils.sendFormattedMessage(minetopiaPlayer, replacePlaceholders(MessageConfiguration.message("banking_account_info_line1"), accountModel));
            ChatUtils.sendFormattedMessage(minetopiaPlayer, replacePlaceholders(MessageConfiguration.message("banking_account_info_line2"), accountModel));
            ChatUtils.sendFormattedMessage(minetopiaPlayer, replacePlaceholders(MessageConfiguration.message("banking_account_info_line3"), accountModel));
            ChatUtils.sendFormattedMessage(minetopiaPlayer, replacePlaceholders(MessageConfiguration.message("banking_account_info_line4"), accountModel));

        });
    }

    private String replacePlaceholders(String message, BankAccountModel accountModel) {
        return message.replace("<account_name>", accountModel.getName())
                .replace("<account_id>", accountModel.getUniqueId().toString())
                .replace("<account_frozen>", String.valueOf(accountModel.getFrozen()))
                .replace("<account_balance>", String.valueOf(accountModel.getBalance()))
                .replace("<account_users>", accountModel.getUsers().toString().replace("[", "").replace("]", ""));
    }
}
