package nl.openminetopia.modules.banking.commands.subcommands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.api.player.PlayerManager;
import nl.openminetopia.configuration.MessageConfiguration;
import nl.openminetopia.modules.banking.BankingModule;
import nl.openminetopia.modules.banking.enums.AccountPermission;
import nl.openminetopia.modules.banking.models.BankAccountModel;
import nl.openminetopia.utils.ChatUtils;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("accounts|account|rekening")
public class BankingUsersCommand extends BaseCommand {

    @Subcommand("adduser")
    @CommandPermission("openminetopia.banking.adduser")
    @Syntax("<player> <naam> <permission>")
    @CommandCompletion("@players @accountNames")
    public void addUser(CommandSender sender, OfflinePlayer target, String accountName, AccountPermission permission) {
        BankingModule bankingModule = OpenMinetopia.getModuleManager().get(BankingModule.class);
        BankAccountModel accountModel = bankingModule.getAccountByName(accountName);
        PlayerManager.getInstance().getMinetopiaPlayer((Player) sender).whenComplete((minetopiaPlayer, throwable1) -> {
            if (accountModel == null) {
                ChatUtils.sendFormattedMessage(minetopiaPlayer, MessageConfiguration.message("banking_account_not_found"));
                return;
            }

            if (accountModel.getUsers().containsKey(target.getUniqueId())) {
                ChatUtils.sendFormattedMessage(minetopiaPlayer, MessageConfiguration.message("banking_user_already_exists")
                        .replace("<player>", (target.getName() == null ? "null" : target.getName())));
                return;
            }

            bankingModule.createBankPermission(target.getUniqueId(), accountModel.getUniqueId(), permission).whenComplete(((permissionModel, throwable) -> {
                if (throwable != null) {
                    ChatUtils.sendFormattedMessage(minetopiaPlayer, MessageConfiguration.message("database_update_error"));
                    return;
                }

                accountModel.getUsers().put(target.getUniqueId(), permission);
                ChatUtils.sendFormattedMessage(minetopiaPlayer, MessageConfiguration.message("banking_user_added")
                        .replace("<player>", (target.getName() == null ? "null" : target.getName()))
                        .replace("<account_name>", accountModel.getName())
                        .replace("<permission>", permission.name()));
            }));
        });
    }

    @Subcommand("removeuser")
    @CommandPermission("openminetopia.banking.removeuser")
    @Syntax("<player> <naam>")
    @CommandCompletion("@players @accountNames")
    public void removeUser(CommandSender sender, OfflinePlayer target, String accountName) {
        BankingModule bankingModule = OpenMinetopia.getModuleManager().get(BankingModule.class);
        BankAccountModel accountModel = bankingModule.getAccountByName(accountName);

        PlayerManager.getInstance().getMinetopiaPlayer((Player) sender).whenComplete((minetopiaPlayer, throwable1) -> {
            if (accountModel == null) {
                ChatUtils.sendFormattedMessage(minetopiaPlayer, MessageConfiguration.message("banking_account_not_found"));
                return;
            }

            bankingModule.deleteBankPermission(accountModel.getUniqueId(), target.getUniqueId()).whenComplete((v, throwable) -> {
                if (throwable != null) {
                    ChatUtils.sendFormattedMessage(minetopiaPlayer, MessageConfiguration.message("database_update_error"));
                    return;
                }

                accountModel.getUsers().remove(target.getUniqueId());
                ChatUtils.sendFormattedMessage(minetopiaPlayer, MessageConfiguration.message("banking_user_deleted")
                        .replace("<player>", (target.getName() == null ? "null" : target.getName()))
                        .replace("<account_name>", accountModel.getName()));
            });
        });
    }
}
