package nl.openminetopia.modules.banking;

import com.craftmend.storm.api.enums.Where;
import com.jazzkuh.modulemanager.spigot.SpigotModule;
import com.jazzkuh.modulemanager.spigot.SpigotModuleManager;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import net.milkbowl.vault.economy.Economy;
import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.modules.banking.commands.BankingCommand;
import nl.openminetopia.modules.banking.commands.subcommands.*;
import nl.openminetopia.modules.banking.configuration.BankingConfiguration;
import nl.openminetopia.modules.banking.enums.AccountPermission;
import nl.openminetopia.modules.banking.enums.AccountType;
import nl.openminetopia.modules.banking.listeners.BankingInteractionListener;
import nl.openminetopia.modules.banking.listeners.PlayerLoginListener;
import nl.openminetopia.modules.banking.models.BankAccountModel;
import nl.openminetopia.modules.banking.models.BankPermissionModel;
import nl.openminetopia.modules.banking.vault.VaultEconomyHandler;
import nl.openminetopia.modules.data.DataModule;
import nl.openminetopia.modules.data.storm.StormDatabase;
import nl.openminetopia.modules.data.utils.StormUtils;
import nl.openminetopia.modules.transactions.TransactionsModule;
import nl.openminetopia.utils.FeatureUtils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.ServicePriority;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Getter
public class BankingModule extends SpigotModule<@NotNull OpenMinetopia> {

    public BankingModule(SpigotModuleManager<@NotNull OpenMinetopia> moduleManager, DataModule dataModule, TransactionsModule transactionsModule) {
        super(moduleManager);
    }

    private DecimalFormat decimalFormat;
    private Collection<BankAccountModel> bankAccountModels;
    @Getter @Setter
    private BankingConfiguration configuration;

    @Override
    public void onEnable() {
        configuration = new BankingConfiguration(OpenMinetopia.getInstance().getDataFolder());
        configuration.saveConfiguration();

        // Check if banking feature is enabled
        if (FeatureUtils.isFeatureDisabled("banking")) {
            getLogger().info("Banking feature is disabled in config.yml");
            return;
        }

        decimalFormat = new DecimalFormat(configuration.getEconomyFormat());
        decimalFormat.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.GERMAN));

        this.bankAccountModels = new ArrayList<>();

        OpenMinetopia.getInstance().getLogger().info("Loading bank accounts..");
        this.getBankAccounts().whenComplete((accounts, accountThrowable) -> {
            if (accountThrowable != null) {
                OpenMinetopia.getInstance().getLogger().severe("Something went wrong while trying to load all bank accounts: " + accountThrowable.getMessage());
                return;
            }

            bankAccountModels = accounts;
            bankAccountModels.forEach(BankAccountModel::initSavingTask);

            OpenMinetopia.getInstance().getLogger().info("Loaded a total of " + bankAccountModels.size() + " accounts.");

            this.getBankPermissions().whenComplete((permissions, throwable) -> {
                if (throwable != null) {
                    OpenMinetopia.getInstance().getLogger().severe("Something went wrong while trying to load all bank permissions: " + throwable.getMessage());
                    return;
                }

                permissions.forEach(permission -> {
                    BankAccountModel accountModel = getAccountById(permission.getAccount());
                    if (accountModel == null) {
                            /*
                            todo: remove permission from db?
                             dataModule.getAdapter().deleteBankPermission(permission.getAccount(), permission.getUuid());
                             */
                        return;
                    }
                    accountModel.getUsers().put(permission.getUuid(), permission.getPermission());
                });
                OpenMinetopia.getInstance().getLogger().info("Found and applied " + permissions.size() + " bank permissions.");
            });
        });

        OpenMinetopia.getCommandManager().getCommandCompletions().registerCompletion("accountNames", context -> bankAccountModels.stream().map(BankAccountModel::getName).collect(Collectors.toList()));

        registerComponent(new BankingCommand());
        registerComponent(new BankingCreateCommand());
        registerComponent(new BankingDeleteCommand());
        registerComponent(new BankingUsersCommand());
        registerComponent(new BankingOpenCommand());
        registerComponent(new BankingFreezeCommand());
        registerComponent(new BankingInfoCommand());
        registerComponent(new BankingBalanceCommand());
        registerComponent(new BankingListCommand());

        registerComponent(new PlayerLoginListener());
        registerComponent(new BankingInteractionListener());

        if (Bukkit.getPluginManager().isPluginEnabled("Vault")) {
            Bukkit.getServicesManager().register(Economy.class, new VaultEconomyHandler(), OpenMinetopia.getInstance(), ServicePriority.Normal);
            OpenMinetopia.getInstance().getLogger().info("Registered Vault economy handler.");
        }
    }

    @Override
    public void onDisable() {
        bankAccountModels.forEach(accountModel -> {
            if (accountModel.getSavingTask() != null) {
                accountModel.save();
                accountModel.getSavingTask().cancel();
            }
        });
    }

    public List<BankAccountModel> getAccountsFromPlayer(UUID uuid) {
        return bankAccountModels.stream().filter(account -> account.getUsers().containsKey(uuid)).collect(Collectors.toList());
    }

    public BankAccountModel getAccountByName(String name) {
        return bankAccountModels.stream().filter(account -> account.getName().equals(name)).findAny().orElse(null);
    }

    public CompletableFuture<BankAccountModel> getAccountByNameAsync(String name) {
        CompletableFuture<BankAccountModel> completableFuture = new CompletableFuture<>();

        if (this.getAccountByName(name) != null) {
            completableFuture.complete(this.getAccountByName(name));
            return completableFuture;
        }

        StormDatabase.getExecutorService().submit(() -> {
            try {
                Collection<BankAccountModel> accountModels = StormDatabase.getInstance().getStorm().buildQuery(BankAccountModel.class)
                        .where("name", Where.EQUAL, name)
                        .execute().join();
                completableFuture.complete(accountModels.stream().findFirst().orElse(null));
            } catch (Exception e) {
                completableFuture.completeExceptionally(e);
            }
        });

        return completableFuture;
    }

    public BankAccountModel getAccountById(UUID uuid) {
        return bankAccountModels.stream().filter(account -> account.getUniqueId().equals(uuid)).findAny().orElse(null);
    }

    public CompletableFuture<BankAccountModel> getAccountByIdAsync(UUID uuid) {
        CompletableFuture<BankAccountModel> completableFuture = new CompletableFuture<>();

        if (this.getAccountById(uuid) != null) {
            completableFuture.complete(this.getAccountById(uuid));
            return completableFuture;
        }

        StormDatabase.getExecutorService().submit(() -> {
            try {
                Collection<BankAccountModel> accountModels = StormDatabase.getInstance().getStorm().buildQuery(BankAccountModel.class)
                        .where("uuid", Where.EQUAL, uuid.toString())
                        .execute().join();
                completableFuture.complete(accountModels.stream().findFirst().orElse(null));
            } catch (Exception e) {
                completableFuture.completeExceptionally(e);
            }
        });

        return completableFuture;
    }

    public CompletableFuture<Collection<BankAccountModel>> getBankAccounts() {
        CompletableFuture<Collection<BankAccountModel>> completableFuture = new CompletableFuture<>();

        StormDatabase.getExecutorService().submit(() -> {
            try {
                Collection<BankAccountModel> accountModels = StormDatabase.getInstance().getStorm().buildQuery(BankAccountModel.class)
                        .where("type", Where.NOT_EQUAL, AccountType.PRIVATE.toString())
                        .execute().join();
                completableFuture.complete(accountModels);
            } catch (Exception e) {
                completableFuture.completeExceptionally(e);
            }
        });

        return completableFuture;
    }

    public CompletableFuture<Collection<BankPermissionModel>> getBankPermissions() {
        CompletableFuture<Collection<BankPermissionModel>> completableFuture = new CompletableFuture<>();

        StormDatabase.getExecutorService().submit(() -> {
            try {
                Collection<BankPermissionModel> permissionModels = StormDatabase.getInstance().getStorm().buildQuery(BankPermissionModel.class)
                        .execute().join();
                completableFuture.complete(permissionModels);
            } catch (Exception e) {
                completableFuture.completeExceptionally(e);
            }
        });

        return completableFuture;
    }

    public CompletableFuture<BankAccountModel> createBankAccount(UUID uuid, AccountType type, double balance, String name, boolean frozen) {
        CompletableFuture<BankAccountModel> completableFuture = new CompletableFuture<>();

        StormDatabase.getExecutorService().submit(() -> {
            BankAccountModel accountModel = new BankAccountModel();
            accountModel.setUniqueId(uuid);
            accountModel.setType(type);
            accountModel.setBalance(balance);
            accountModel.setName(name);
            accountModel.setFrozen(frozen);

            StormDatabase.getInstance().saveStormModel(accountModel);
            completableFuture.complete(accountModel);
        });

        return completableFuture;
    }

    public CompletableFuture<Void> deleteBankAccount(UUID accountUuid) {
        CompletableFuture<Void> completableFuture = new CompletableFuture<>();

        StormDatabase.getExecutorService().submit(() -> {
            StormUtils.deleteModelData(BankAccountModel.class,
                    query -> query.where("uuid", Where.EQUAL, accountUuid.toString())
            ).join();

            StormUtils.deleteModelData(BankPermissionModel.class,
                    query -> query.where("account", Where.EQUAL, accountUuid.toString())
            ).join();

            completableFuture.complete(null);
        });

        return completableFuture;
    }

    public CompletableFuture<BankPermissionModel> createBankPermission(UUID player, UUID accountId, AccountPermission permission) {
        CompletableFuture<BankPermissionModel> completableFuture = new CompletableFuture<>();

        StormDatabase.getExecutorService().submit(() -> {
            BankPermissionModel permissionModel = new BankPermissionModel();
            permissionModel.setUuid(player);
            permissionModel.setAccount(accountId);
            permissionModel.setPermission(permission);

            StormDatabase.getInstance().saveStormModel(permissionModel);
            completableFuture.complete(permissionModel);
        });

        return completableFuture;
    }

    public CompletableFuture<Void> deleteBankPermission(UUID accountUuid, UUID playerUuid) {
        return StormUtils.deleteModelData(BankPermissionModel.class, query -> {
            query.where("uuid", Where.EQUAL, playerUuid.toString());
            query.where("account", Where.EQUAL, accountUuid.toString());
        });
    }

    @SneakyThrows
    public CompletableFuture<BankAccountModel> getAccountModel(UUID accountId) {
        CompletableFuture<BankAccountModel> accountModelFuture = new CompletableFuture<>();

        Collection<BankAccountModel> collectionFuture = StormDatabase.getInstance().getStorm()
                .buildQuery(BankAccountModel.class)
                .where("uuid", Where.EQUAL, accountId.toString())
                .execute().join();

        accountModelFuture.complete(collectionFuture.stream().findFirst().orElse(null));

        return accountModelFuture;
    }

    public void createPermissions(BankPermissionModel permissionModel) throws SQLException {
        BankAccountModel accountModel = getAccountById(permissionModel.getAccount());
        accountModel.getUsers().put(permissionModel.getUuid(), permissionModel.getPermission());
        StormDatabase.getInstance().getStorm().save(permissionModel);
    }

    public String format(double amount) {
        return "€" + decimalFormat.format(amount);
    }

}
