package nl.openminetopia.modules.currencies.tasks;


import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.api.player.PlayerManager;
import nl.openminetopia.api.player.objects.MinetopiaPlayer;
import nl.openminetopia.configuration.MessageConfiguration;
import nl.openminetopia.framework.runnables.AbstractDirtyRunnable;
import nl.openminetopia.modules.currencies.CurrencyModule;
import nl.openminetopia.modules.currencies.models.CurrencyModel;
import nl.openminetopia.modules.currencies.objects.RegisteredCurrency;
import nl.openminetopia.modules.data.storm.StormDatabase;

import nl.openminetopia.utils.ChatUtils;


import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

public class CurrencyTask extends AbstractDirtyRunnable<UUID> {

    private final CurrencyModule currencyModule;
    private final PlayerManager playerManager;

    public CurrencyTask(CurrencyModule currencyModule, PlayerManager playerManager, long minIntervalMs, int batch, long heartbeatMs, Supplier<List<UUID>> allKeysSupplier) {
        super(minIntervalMs, batch, heartbeatMs, allKeysSupplier);
        this.currencyModule = currencyModule;
        this.playerManager = playerManager;
    }

    @Override
    protected void process(UUID key) {
        MinetopiaPlayer minetopiaPlayer = playerManager.getOnlinePlayers().get(key);
        if (minetopiaPlayer == null) return;
        if (!currencyModule.getCurrencyModels().containsKey(minetopiaPlayer.getUuid())) return;
        List<CurrencyModel> currencies = currencyModule.getCurrencyModels().get(minetopiaPlayer.getUuid());
        if (currencies.isEmpty()) return;

        for (CurrencyModel currency : currencies) {
            RegisteredCurrency configCurrency = currency.configModel();
            if (configCurrency == null) {
                if (currencyModule.getConfiguration().isIgnoreUnused()) continue;
                OpenMinetopia.getInstance().getLogger().warning("Currency config not found for currency " + currency.getName());
                OpenMinetopia.getInstance().getLogger().warning("You should consider removing the currency model from the database (if it is not used) by using /currency purge-unused or enabling ignore-unused in currencies.yml");
                continue;
            }
            if (!configCurrency.isAutomatic()) continue;

            if (minetopiaPlayer.getPlaytime() - currency.getLastReward() >= configCurrency.getInterval() * 1000L) {
                currency.setLastReward(minetopiaPlayer.getPlaytime());

                String message = MessageConfiguration.message("currency_automatic_reward")
                        .replaceAll("<amount>", String.valueOf(configCurrency.getAmount()))
                        .replaceAll("<display_name>", configCurrency.getDisplayName());

                ChatUtils.sendFormattedMessage(minetopiaPlayer, message);
                currency.setBalance(currency.getBalance() + configCurrency.getAmount());
                StormDatabase.getInstance().saveStormModel(currency);
            }
        }
    }
}
