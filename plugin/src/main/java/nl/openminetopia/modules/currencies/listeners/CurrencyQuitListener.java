package nl.openminetopia.modules.currencies.listeners;

import lombok.RequiredArgsConstructor;
import nl.openminetopia.modules.currencies.CurrencyModule;
import nl.openminetopia.modules.currencies.models.CurrencyModel;
import nl.openminetopia.modules.data.storm.StormDatabase;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.List;

@RequiredArgsConstructor
public class CurrencyQuitListener implements Listener {

    private final CurrencyModule currencyModule;

    @EventHandler
    public void onCurrencyQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if (!currencyModule.getCurrencyModels().containsKey(player.getUniqueId())) return;
        List<CurrencyModel> currencies = currencyModule.getCurrencyModels().get(player.getUniqueId());

        if (currencies.isEmpty()) return;

        for (CurrencyModel currency : currencies) {
            StormDatabase.getInstance().saveStormModel(currency);
        }

        currencyModule.getCurrencyModels().remove(player.getUniqueId());
    }

}
