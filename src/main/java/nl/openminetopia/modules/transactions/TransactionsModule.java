package nl.openminetopia.modules.transactions;

import com.craftmend.storm.api.enums.Where;
import com.jazzkuh.modulemanager.spigot.SpigotModule;
import com.jazzkuh.modulemanager.spigot.SpigotModuleManager;
import lombok.AllArgsConstructor;
import lombok.Getter;
import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.modules.data.storm.StormDatabase;
import nl.openminetopia.modules.transactions.commands.TransactionCommand;
import nl.openminetopia.modules.transactions.enums.TransactionType;
import nl.openminetopia.modules.transactions.objects.TransactionModel;
import nl.openminetopia.utils.FeatureUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class TransactionsModule extends SpigotModule<@NotNull OpenMinetopia> {

    public TransactionsModule(SpigotModuleManager<@NotNull OpenMinetopia> moduleManager) {
        super(moduleManager);
    }

    @Override
    public void onEnable() {
        // Check if transactions feature is enabled
        if (FeatureUtils.isFeatureDisabled("transactions")) {
            getLogger().info("Transactions feature is disabled in config.yml");
            return;
        }

        registerComponent(new TransactionCommand());
    }

    public CompletableFuture<TransactionModel> createTransactionLog(long time, UUID player, String username, TransactionType type, double amount, UUID bankAccount, String description) {
        CompletableFuture<TransactionModel> completableFuture = new CompletableFuture<>();
        StormDatabase.getExecutorService().submit(() -> {
            TransactionModel transactionModel = new TransactionModel();
            transactionModel.setTime(time);
            transactionModel.setPlayer(player);
            transactionModel.setUsername(username);
            transactionModel.setType(type);
            transactionModel.setAmount(amount);
            transactionModel.setBankAccount(bankAccount);
            transactionModel.setDescription(description);

            StormDatabase.getInstance().saveStormModel(transactionModel);
            completableFuture.complete(transactionModel);
        });
        return completableFuture;
    }

    public CompletableFuture<Collection<TransactionModel>> getAccountTransactions(LookupType type, UUID uuid) {
        CompletableFuture<Collection<TransactionModel>> completableFuture = new CompletableFuture<>();
        StormDatabase.getExecutorService().submit(() -> {
            try {
                Collection<TransactionModel> transactionModels = StormDatabase.getInstance().getStorm().buildQuery(TransactionModel.class)
                        .where(type.getRowName(), Where.EQUAL, uuid.toString())
                        .limit(250)
                        .execute().join();

                completableFuture.complete(transactionModels);
            } catch (Exception e) {
                completableFuture.completeExceptionally(e);
            }
        });
        return completableFuture;
    }

    @Getter
    @AllArgsConstructor
    public enum LookupType {
        PLAYER("player_uuid"),
        BANK_ACCOUNT("bank_account");

        private final String rowName;
    }


}
