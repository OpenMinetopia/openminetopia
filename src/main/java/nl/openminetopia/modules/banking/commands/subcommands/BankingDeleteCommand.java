package nl.openminetopia.modules.banking.commands.subcommands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.api.player.PlayerManager;
import nl.openminetopia.configuration.MessageConfiguration;
import nl.openminetopia.modules.banking.BankingModule;
import nl.openminetopia.modules.banking.models.BankAccountModel;
import nl.openminetopia.utils.ChatUtils;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

@CommandAlias("accounts|account|rekening")
public class BankingDeleteCommand extends BaseCommand {

    @Subcommand("delete")
    @Syntax("<name>")
    @CommandCompletion("@accountNames")
    @CommandPermission("openminetopia.banking.delete")
    public void deleteAccount(CommandSender sender, String accountName) {
        BankingModule bankingModule = OpenMinetopia.getModuleManager().get(BankingModule.class);
        BankAccountModel accountModel = bankingModule.getAccountByName(accountName);

        PlayerManager.getInstance().getMinetopiaPlayer((OfflinePlayer) sender).whenComplete((minetopiaPlayer, throwable1) -> {
            if (accountModel == null) {
                ChatUtils.sendFormattedMessage(minetopiaPlayer, MessageConfiguration.message("banking_account_not_found"));
                return;
            }

            bankingModule.deleteBankAccount(accountModel.getUniqueId()).whenComplete((v, throwable) -> {
                if (throwable != null) {
                    ChatUtils.sendFormattedMessage(minetopiaPlayer, MessageConfiguration.message("banking_account_deletion_error"));
                    return;
                }

                ChatUtils.sendFormattedMessage(minetopiaPlayer, MessageConfiguration.message("banking_account_deleted")
                        .replace("<account_name>", accountModel.getName()));
                bankingModule.getBankAccountModels().remove(accountModel);
            });
        });
    }
}
