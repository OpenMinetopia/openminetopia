package nl.openminetopia.modules.currencies;

import com.craftmend.storm.api.enums.Where;
import com.jazzkuh.modulemanager.spigot.SpigotModule;
import com.jazzkuh.modulemanager.spigot.SpigotModuleManager;
import lombok.Getter;
import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.modules.currencies.commands.CurrencyCommandHolder;
import nl.openminetopia.modules.currencies.configuration.CurrencyConfiguration;
import nl.openminetopia.modules.currencies.listeners.CurrencyJoinListener;
import nl.openminetopia.modules.currencies.listeners.CurrencyQuitListener;
import nl.openminetopia.modules.currencies.models.CurrencyModel;
import nl.openminetopia.modules.currencies.objects.RegisteredCurrency;
import nl.openminetopia.modules.currencies.tasks.CurrencyTask;
import nl.openminetopia.modules.data.storm.StormDatabase;
import nl.openminetopia.modules.player.PlayerModule;
import nl.openminetopia.utils.FeatureUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.CompletableFuture;

@Getter
@SuppressWarnings("UnstableApiUsage")
public class CurrencyModule extends SpigotModule<@NotNull OpenMinetopia> {

    private final List<RegisteredCurrency> currencies = new ArrayList<>();
    private final Map<UUID, List<CurrencyModel>> currencyModels = new HashMap<>();

    private CurrencyConfiguration configuration;

    public CurrencyModule(SpigotModuleManager<@NotNull OpenMinetopia> moduleManager, PlayerModule playerModule) {
        super(moduleManager);
    }

    @Override
    public void onEnable() {
        configuration = new CurrencyConfiguration(this, OpenMinetopia.getInstance().getDataFolder());
        configuration.saveConfiguration();

        // Check if currencies feature is enabled
        if (FeatureUtils.isFeatureDisabled("currencies")) {
            getLogger().info("Currencies feature is disabled in config.yml");
            return;
        }

        registerComponent(new CurrencyJoinListener(this));
        registerComponent(new CurrencyQuitListener(this));
        registerComponent(new CurrencyTask(this));

        String pluginName = getPlugin().getPluginMeta().getName();
        CommandMap commandMap = Bukkit.getCommandMap();

        for (RegisteredCurrency currency : currencies) {
            CurrencyCommandHolder command = new CurrencyCommandHolder(this, currency);
            commandMap.register(pluginName, command);
        }

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

}
