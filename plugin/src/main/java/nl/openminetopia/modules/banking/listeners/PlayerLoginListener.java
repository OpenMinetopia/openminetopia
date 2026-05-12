package nl.openminetopia.modules.banking.listeners;

import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.configuration.MessageConfiguration;
import nl.openminetopia.modules.banking.BankingModule;
import nl.openminetopia.modules.banking.enums.AccountPermission;
import nl.openminetopia.modules.banking.enums.AccountType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerLoginListener implements Listener {

    @EventHandler
    public void playerPreLogin(final PlayerJoinEvent event) {
        BankingModule bankingModule = OpenMinetopia.getModuleManager().get(BankingModule.class);
        Player player = event.getPlayer();

        bankingModule.getAccountModel(player.getUniqueId()).whenComplete(((bankAccountModel, throwable) -> {
            if (throwable != null) {
                OpenMinetopia.getInstance().getLogger().info("Could not find any bank account for: " + throwable.getMessage());
                player.kick(MessageConfiguration.component("player_bank_data_not_loaded"));
                return;
            }

            if (bankAccountModel == null) {
                double startingBalance = bankingModule.getConfiguration().getStartingBalance();
                bankingModule.createBankAccount(player.getUniqueId(), AccountType.PRIVATE, startingBalance, player.getName(), false).whenComplete((accountModel, accountThrowable) -> {
                    if (accountThrowable != null) {
                        OpenMinetopia.getInstance().getLogger().severe("Couldn't create account for " + player.getName() + ": " + accountThrowable.getMessage());
                        player.kick(MessageConfiguration.component("player_bank_data_not_loaded"));
                    }

                    accountModel.initSavingTask();
                    accountModel.getUsers().put(player.getUniqueId(), AccountPermission.ADMIN);
                    bankingModule.getBankAccountModels().add(accountModel);
                    OpenMinetopia.getInstance().getLogger().info("Created new account for: " + player.getName() + " (" + accountModel.getUniqueId() + ")");
                });
                return;
            }

            if (bankingModule.getBankAccountModels().contains(bankAccountModel)) {
                OpenMinetopia.getInstance().getLogger().info("duplicated account found, skipping..");
                return;
            }

            OpenMinetopia.getInstance().getLogger().info("Bank account for " + player.getName() + " was loaded.");

            bankAccountModel.getUsers().put(player.getUniqueId(), AccountPermission.ADMIN);
            bankAccountModel.initSavingTask();
            bankingModule.getBankAccountModels().add(bankAccountModel);
        }));
    }
}
