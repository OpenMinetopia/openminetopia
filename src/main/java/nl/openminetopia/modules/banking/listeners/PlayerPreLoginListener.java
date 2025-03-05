package nl.openminetopia.modules.banking.listeners;

import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.configuration.MessageConfiguration;
import nl.openminetopia.modules.banking.BankingModule;
import nl.openminetopia.modules.banking.enums.AccountPermission;
import nl.openminetopia.modules.banking.enums.AccountType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

public class PlayerPreLoginListener implements Listener {

    @EventHandler
    public void playerPreLogin(final AsyncPlayerPreLoginEvent event) {
        BankingModule bankingModule = OpenMinetopia.getModuleManager().get(BankingModule.class);

        bankingModule.getAccountModel(event.getUniqueId()).whenComplete(((bankAccountModel, throwable) -> {
            if (throwable != null) {
                OpenMinetopia.getInstance().getLogger().info("Could not account for: " + throwable.getMessage());
                event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, MessageConfiguration.component("player_bank_data_not_loaded"));
                return;
            }

            if (bankAccountModel == null) {
                OpenMinetopia.getInstance().getLogger().info("account is null, creating.");
                bankingModule.createBankAccount(event.getUniqueId(), AccountType.PRIVATE, 0L, event.getName(), false).whenComplete((accountModel, accountThrowable) -> {
                    if (accountThrowable != null) {
                        OpenMinetopia.getInstance().getLogger().severe("Couldn't create account for " + event.getName() + ": " + accountThrowable.getMessage());
                        event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, MessageConfiguration.component("player_bank_data_not_loaded"));
                    }

                    accountModel.initSavingTask();
                    accountModel.getUsers().put(event.getUniqueId(), AccountPermission.ADMIN);
                    bankingModule.getBankAccountModels().add(accountModel);
                    OpenMinetopia.getInstance().getLogger().info("Loaded account for: " + event.getName() + " (" + accountModel.getUniqueId() + ")");
                });
                return;
            }

            OpenMinetopia.getInstance().getLogger().info("account is not null, loading.");

            bankAccountModel.getUsers().put(event.getUniqueId(), AccountPermission.ADMIN);
            bankAccountModel.initSavingTask();
            bankingModule.getBankAccountModels().add(bankAccountModel);
        }));
    }
}
