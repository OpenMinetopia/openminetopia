package nl.openminetopia.modules.currencies.listeners;

import lombok.RequiredArgsConstructor;
import nl.openminetopia.modules.currencies.CurrencyModule;
import nl.openminetopia.modules.currencies.models.CurrencyModel;
import nl.openminetopia.modules.currencies.objects.RegisteredCurrency;
import nl.openminetopia.modules.data.storm.StormDatabase;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class CurrencyJoinListener implements Listener {

    private final CurrencyModule currencyModule;

    @EventHandler
    public void onCurrencyJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        List<CurrencyModel> currencyModels = new ArrayList<>();
        currencyModule.getCurrencies(player.getUniqueId()).whenComplete((models, throwable) -> {
            if (throwable != null) {
                currencyModule.getLogger().error("Couldn't load player currencies: {}", throwable.getMessage());
                return;
            }

            currencyModels.addAll(models);
            currencyModule.getLogger().info("Loaded {} currencies for {}", currencyModels.size(), player.getName());

            for (RegisteredCurrency registeredCurrency : currencyModule.getCurrencies()) {
                CurrencyModel currencyModel = currencyModels.stream()
                        .filter(model -> model.getName().equalsIgnoreCase(registeredCurrency.getId()))
                        .findAny()
                        .orElse(null);

                if (currencyModel == null) {
                    CurrencyModel newCurrencymodel = new CurrencyModel(
                            player.getUniqueId(),
                            registeredCurrency.getId(),
                            0d,
                            0L
                    );

                    currencyModels.add(newCurrencymodel);
                    StormDatabase.getInstance().saveStormModel(newCurrencymodel);
                }
            }

            currencyModule.getCurrencyModels().put(player.getUniqueId(), currencyModels);
        });
    }
}
