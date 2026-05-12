package nl.openminetopia.modules.currencies;

import com.craftmend.storm.api.enums.Where;
import nl.openminetopia.utils.modules.ExtendedSpigotModule;
import com.jazzkuh.modulemanager.spigot.SpigotModuleManager;
import lombok.Getter;
import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.api.player.PlayerManager;
import nl.openminetopia.modules.currencies.commands.CurrencyCommand;
import nl.openminetopia.modules.currencies.commands.CurrencyCommandHolder;
import nl.openminetopia.modules.currencies.configuration.CurrencyConfiguration;
import nl.openminetopia.modules.currencies.listeners.CurrencyJoinListener;
import nl.openminetopia.modules.currencies.listeners.CurrencyQuitListener;
import nl.openminetopia.modules.currencies.models.CurrencyModel;
import nl.openminetopia.modules.currencies.objects.RegisteredCurrency;
import nl.openminetopia.modules.currencies.tasks.CurrencyTask;
import nl.openminetopia.modules.data.storm.StormDatabase;
import nl.openminetopia.modules.player.PlayerModule;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.CompletableFuture;

@Getter
@SuppressWarnings("UnstableApiUsage")
public class CurrencyModule extends ExtendedSpigotModule {

    private final List<RegisteredCurrency> currencies = new ArrayList<>();
    private final Map<UUID, List<CurrencyModel>> currencyModels = new HashMap<>();

    private CurrencyConfiguration configuration;
    @Getter
    private CurrencyTask currencyTask;

    public CurrencyModule(SpigotModuleManager<@NotNull OpenMinetopia> moduleManager, PlayerModule playerModule) {
        super(moduleManager);
    }

    @Override
    public void onEnable() {
        configuration = new CurrencyConfiguration(this, OpenMinetopia.getInstance().getDataFolder());
        configuration.saveConfiguration();

        registerComponent(new CurrencyJoinListener(this));
        registerComponent(new CurrencyQuitListener(this));

        registerComponent(new CurrencyCommand());

        String pluginName = getPlugin().getPluginMeta().getName();
        CommandMap commandMap = Bukkit.getCommandMap();

        for (RegisteredCurrency currency : currencies) {
            CurrencyCommandHolder command = new CurrencyCommandHolder(this, currency);
            commandMap.register(pluginName, command);
        }

        currencyTask = new CurrencyTask(this, PlayerManager.getInstance(), 5000L, 50, 30 * 1000L, () -> new ArrayList<>(PlayerManager.getInstance().getOnlinePlayers().keySet()));
        OpenMinetopia.getInstance().registerDirtyPlayerRunnable(currencyTask, 20L);

    }

    @Override
    public void onDisable() {
        OpenMinetopia.getInstance().unregisterDirtyPlayerRunnable(currencyTask);
    }

    public CompletableFuture<Collection<CurrencyModel>> getCurrencies(UUID uuid) {
        CompletableFuture<Collection<CurrencyModel>> completableFuture = new CompletableFuture<>();

        if (currencyModels.containsKey(uuid)) {
            completableFuture.complete(currencyModels.get(uuid));
            return completableFuture;
        }

        StormDatabase.getExecutorService().submit(() -> {
            try {
                Collection<CurrencyModel> accountModels = StormDatabase.getInstance().getStorm().buildQuery(CurrencyModel.class)
                        .where("uuid", Where.EQUAL, uuid.toString())
                        .execute()
                        .join();
                completableFuture.complete(accountModels);
            } catch (Exception e) {
                completableFuture.completeExceptionally(e);
            }
        });

        return completableFuture;
    }

    public CompletableFuture<Collection<CurrencyModel>> getAllCurrencies() {
        CompletableFuture<Collection<CurrencyModel>> completableFuture = new CompletableFuture<>();

        StormDatabase.getExecutorService().submit(() -> {
            try {
                Collection<CurrencyModel> accountModels = StormDatabase.getInstance().getStorm()
                        .findAll(CurrencyModel.class).join();
                completableFuture.complete(accountModels);
            } catch (Exception e) {
                completableFuture.completeExceptionally(e);
            }
        });

        return completableFuture;
    }
}
