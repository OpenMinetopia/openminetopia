package nl.openminetopia.modules.currencies.commands;

import io.papermc.paper.plugin.configuration.PluginMeta;
import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.configuration.MessageConfiguration;
import nl.openminetopia.modules.currencies.CurrencyModule;
import nl.openminetopia.modules.currencies.models.CurrencyModel;
import nl.openminetopia.modules.currencies.objects.RegisteredCurrency;
import nl.openminetopia.modules.data.storm.StormDatabase;
import nl.openminetopia.utils.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class CurrencyCommandHolder extends Command {

    private final CurrencyModule currencyModule;
    private final RegisteredCurrency currency;

    public CurrencyCommandHolder(CurrencyModule currencyModule, RegisteredCurrency currency) {
        super(currency.getId(), "Commands voor " + currency.getDisplayName(), currency.getId() + "help", currency.getAliases());
        this.currencyModule = currencyModule;
        this.currency = currency;
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String label, @NotNull String @NotNull [] args) {
        PluginMeta pluginMeta = OpenMinetopia.getInstance().getPluginMeta();
        String pluginPrefix = (pluginMeta.getName().toLowerCase() + ":");

        if (label.startsWith(pluginPrefix)) {
            label = label.replaceFirst(pluginPrefix, "");
        }

        String finalCommand = label;

        /* - /currency */
        if (args.length == 0) {
            if (!(sender instanceof Player player)) return false;
            showCurrencySelf(player);
            return false;
        }

        if (args.length == 1 && args[0].equalsIgnoreCase("help")) {
            sendHelp(sender);
            return false;
        }

        /* - /currency <player> */
        if (args.length == 1) {
            if (!sender.hasPermission("openminetopia.currency.info." + currency.getId())) {
                ChatUtils.sendMessage(sender, "<red>Sorry, je hebt geen toestemming om dit commando uit te voeren.");
                return false;
            }

            OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);

            if (!target.hasPlayedBefore()) {
                ChatUtils.sendMessage(sender, MessageConfiguration.message("player_not_found"));
                return false;
            }

            showCurrencyOther(sender, target);
            return false;
        }

        /* - /currency set <player> <amount> */
        if (args.length == 3 && args[0].equalsIgnoreCase("set")) {
            if (!sender.hasPermission("openminetopia.currency.set." + currency.getId())) {
                ChatUtils.sendMessage(sender, "<red>Sorry, je hebt geen toestemming om dit commando uit te voeren.");
                return false;
            }

            OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);

            if (!target.hasPlayedBefore()) {
                ChatUtils.sendMessage(sender, MessageConfiguration.message("player_not_found"));
                return false;
            }

            if (!isDouble(args[2])) {
                ChatUtils.sendMessage(sender, MessageConfiguration.message("currency_invalid_amount"));
                return false;
            }

            double amount = Double.parseDouble(args[2]);
            setCurrency(sender, target, amount);
            return false;
        }

        /* - /currency add <player> <amount> */
        if (args.length == 3 && args[0].equalsIgnoreCase("add")) {
            if (!sender.hasPermission("openminetopia.currency.add." + currency.getId())) {
                ChatUtils.sendMessage(sender, "<red>Sorry, je hebt geen toestemming om dit commando uit te voeren.");
                return false;
            }

            OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);

            if (!target.hasPlayedBefore()) {
                ChatUtils.sendMessage(sender, MessageConfiguration.message("player_not_found"));
                return false;
            }

            if (!isDouble(args[2])) {
                ChatUtils.sendMessage(sender, MessageConfiguration.message("currency_invalid_amount"));
                return false;
            }

            double amount = Double.parseDouble(args[2]);
            addCurrency(sender, target, amount);
            return false;
        }

        /* - /currency remove <player> <amount> */
        if (args.length == 3 && args[0].equalsIgnoreCase("remove")) {
            if (!sender.hasPermission("openminetopia.currency.remove." + currency.getId())) {
                ChatUtils.sendMessage(sender, "<red>Sorry, je hebt geen toestemming om dit commando uit te voeren.");
                return false;
            }

            OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);

            if (!target.hasPlayedBefore()) {
                ChatUtils.sendMessage(sender, MessageConfiguration.message("player_not_found"));
                return false;
            }

            if (!isDouble(args[2])) {
                ChatUtils.sendMessage(sender, MessageConfiguration.message("currency_invalid_amount"));
                return false;
            }

            double amount = Double.parseDouble(args[2]);
            removeCurrency(sender, target, amount);
            return false;
        }

        sendHelp(sender);
        return false;
    }


    private void showCurrencySelf(Player player) {
        currencyModule.getCurrencies(player.getUniqueId()).whenComplete((models, throwable) -> {
            CurrencyModel currencyModel = getCurrencyModel(models);

            if (currencyModel == null) {
                ChatUtils.sendMessage(player, "<red>Er ging wat mis.");
                return;
            }

            String message = MessageConfiguration.message("currency_show")
                    .replaceAll("<amount>", String.valueOf(currencyModel.getBalance()))
                    .replaceAll("<display_name>", currency.getDisplayName());

            ChatUtils.sendMessage(player, message);
        });
    }

    private void showCurrencyOther(CommandSender executor, OfflinePlayer player) {
        currencyModule.getCurrencies(player.getUniqueId()).whenComplete((models, throwable) -> {
            CurrencyModel currencyModel = getCurrencyModel(models);

            if (player == null) {
                ChatUtils.sendMessage(executor, "<red>Speler niet gevonden.");
                return;
            }

            if (currencyModel == null) {
                ChatUtils.sendMessage(executor, "<red>Er ging wat mis.");
                return;
            }

            String message = MessageConfiguration.message("currency_show_other")
                    .replaceAll("<target>", player.getName())
                    .replaceAll("<amount>", String.valueOf(currencyModel.getBalance()))
                    .replaceAll("<display_name>", currency.getDisplayName());

            ChatUtils.sendMessage(executor, message);
        });

    }

    private void setCurrency(CommandSender executor, OfflinePlayer player, double amount) {
        currencyModule.getCurrencies(player.getUniqueId()).whenComplete((models, throwable) -> {
            CurrencyModel currencyModel = getCurrencyModel(models);

            if (player == null) {
                ChatUtils.sendMessage(executor, "<red>Speler niet gevonden.");
                return;
            }

            if (currencyModel == null) {
                ChatUtils.sendMessage(executor, "<red>Er ging wat mis.");
                return;
            }

            String message = MessageConfiguration.message("currency_balance_set")
                    .replaceAll("<target>", player.getName())
                    .replaceAll("<amount>", String.valueOf(amount))
                    .replaceAll("<display_name>", currency.getDisplayName());

            currencyModel.setBalance(amount);
            StormDatabase.getInstance().saveStormModel(currencyModel);
            ChatUtils.sendMessage(executor, message);
        });
    }

    private void addCurrency(CommandSender executor, OfflinePlayer player, double amount) {
        currencyModule.getCurrencies(player.getUniqueId()).whenComplete((models, throwable) -> {
            CurrencyModel currencyModel = getCurrencyModel(models);

            if (player == null) {
                ChatUtils.sendMessage(executor, "<red>Speler niet gevonden.");
                return;
            }

            if (currencyModel == null) {
                ChatUtils.sendMessage(executor, "<red>Er ging wat mis.");
                return;
            }

            String message = MessageConfiguration.message("currency_balance_add")
                    .replaceAll("<target>", player.getName())
                    .replaceAll("<amount>", String.valueOf(amount))
                    .replaceAll("<display_name>", currency.getDisplayName());

            currencyModel.setBalance(currencyModel.getBalance() + amount);
            StormDatabase.getInstance().saveStormModel(currencyModel);
            ChatUtils.sendMessage(executor, message);
        });
    }

    private void removeCurrency(CommandSender executor, OfflinePlayer player, double amount) {
        currencyModule.getCurrencies(player.getUniqueId()).whenComplete((models, throwable) -> {
            CurrencyModel currencyModel = getCurrencyModel(models);

            if (player == null) {
                ChatUtils.sendMessage(executor, "<red>Speler niet gevonden.");
                return;
            }

            if (currencyModel == null) {
                ChatUtils.sendMessage(executor, "<red>Er ging wat mis.");
                return;
            }

            String message = MessageConfiguration.message("currency_balance_remove")
                    .replaceAll("<target>", player.getName())
                    .replaceAll("<amount>", String.valueOf(amount))
                    .replaceAll("<display_name>", currency.getDisplayName());


            currencyModel.setBalance(currencyModel.getBalance() - amount);
            StormDatabase.getInstance().saveStormModel(currencyModel);
            ChatUtils.sendMessage(executor, message);
        });
    }

    private CurrencyModel getCurrencyModel(Collection<CurrencyModel> models) {
        if (models.isEmpty()) return null;

        return models.stream()
                .filter(model -> model.getName().equalsIgnoreCase(currency.getId()))
                .findAny()
                .orElse(null);
    }

    private void sendHelp(CommandSender sender) {
        if (!sender.hasPermission("openminetopia.currency." + currency.getId())) return;
        ChatUtils.sendMessage(sender, "<gold>/" + currency.getId() + " <gray>- Laat je huidige bedrag zien.");
        ChatUtils.sendMessage(sender, "<gold>/" + currency.getId() + " <yellow><speler> <gray>- Laat huidige bedrag van speler zien.");
        ChatUtils.sendMessage(sender, "<gold>/" + currency.getId() + " <yellow>set <speler> <hoeveelheid> <gray>- Zet een speler zijn " + currency.getDisplayName());
        ChatUtils.sendMessage(sender, "<gold>/" + currency.getId() + " <yellow>add <speler> <hoeveelheid> <gray>- Voeg een aantal toe aan een speler zijn " + currency.getDisplayName());
        ChatUtils.sendMessage(sender, "<gold>/" + currency.getId() + " <yellow>remove <speler> <hoeveelheid> <gray>- Verwijder een aantal van een speler zijn " + currency.getDisplayName());
    }

    private boolean isDouble(String d) {
        try {
            Double.parseDouble(d);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String @NotNull [] args) throws IllegalArgumentException {
        if (!sender.hasPermission("openminetopia.currency." + currency.getId())) return List.of();

        List<String> usernames = Bukkit.getOnlinePlayers().stream()
                .map(Player::getName)
                .toList();

        if (args.length == 1) {
            List<String> commands = new ArrayList<>();
            commands.add("remove");
            commands.add("set");
            commands.add("add");
            commands.add("help");
            commands.addAll(usernames);

            List<String> completions = new ArrayList<>();
            StringUtil.copyPartialMatches(args[0], commands, completions);
            return completions;
        }

        if (args.length == 2) {
            List<String> completions = new ArrayList<>();
            StringUtil.copyPartialMatches(args[args.length - 1], usernames, completions);
            return completions;
        }

        return List.of();
    }
}
