package nl.openminetopia.modules.currencies.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.configuration.MessageConfiguration;
import nl.openminetopia.modules.currencies.CurrencyModule;
import nl.openminetopia.modules.currencies.models.CurrencyModel;
import nl.openminetopia.modules.data.storm.StormDatabase;
import nl.openminetopia.utils.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@CommandAlias("currency|currencies")
public class CurrencyCommand extends BaseCommand {

    private boolean awaitingConfirmation = false;

    @Subcommand("purge-unused")
    @CommandPermission("openminetopia.currency.purge")
    public void purgeOld(CommandSender sender) {
        if (awaitingConfirmation) {
            sender.sendMessage(MessageConfiguration.component("currency_purge_started"));
            CurrencyModule currencyModule = OpenMinetopia.getModuleManager().get(CurrencyModule.class);

            currencyModule.getAllCurrencies().whenComplete((currencies, throwable) -> {
                if (throwable != null) {
                    Bukkit.getScheduler().runTask(OpenMinetopia.getInstance(), () -> {
                        sender.sendMessage(ChatUtils.color(MessageConfiguration.message("currency_purge_error")
                                .replace("<error>", throwable.getMessage())));
                    });
                    awaitingConfirmation = false;
                    return;
                }

                List<CurrencyModel> toRemove = new ArrayList<>();
                for (CurrencyModel currency : new ArrayList<>(currencies)) {
                    if (currencyModule.getCurrencies().contains(currency.configModel())) continue;

                    try {
                        StormDatabase.getInstance().getStorm().delete(currency);
                    } catch (SQLException e) {
                        OpenMinetopia.getInstance().getLogger().warning("Error while deleting currency: " + currency.getName() + " for UUID: " + currency.getUniqueId());
                    }

                    currencyModule.getCurrencyModels().values().forEach(list ->
                            list.removeIf(model -> model.getName().equalsIgnoreCase(currency.getName()) &&
                                    model.getUniqueId().equals(currency.getUniqueId()))
                    );

                    toRemove.add(currency);
                }
                currencies.removeAll(toRemove);

                int removedCount = toRemove.size();
                Bukkit.getScheduler().runTask(OpenMinetopia.getInstance(), () ->
                        sender.sendMessage(ChatUtils.color(MessageConfiguration.message("currency_purge_success")
                                .replace("<amount>", String.valueOf(removedCount))))
                );

                awaitingConfirmation = false;
            });
            return;
        }

        sender.sendMessage(MessageConfiguration.component("currency_purge_confirm"));
        awaitingConfirmation = true;

        Bukkit.getScheduler().runTaskLater(OpenMinetopia.getInstance(), () -> awaitingConfirmation = false, 20 * 20L);
    }
}