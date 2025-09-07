package nl.openminetopia.utils.placeholderapi;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.api.player.PlayerManager;
import nl.openminetopia.api.player.objects.MinetopiaPlayer;
import nl.openminetopia.modules.banking.BankingModule;
import nl.openminetopia.modules.banking.models.BankAccountModel;
import nl.openminetopia.modules.color.enums.OwnableColorType;
import nl.openminetopia.modules.currencies.CurrencyModule;
import nl.openminetopia.modules.currencies.models.CurrencyModel;
import nl.openminetopia.modules.fitness.FitnessModule;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public class OpenMinetopiaExpansion extends PlaceholderExpansion {

    private final OpenMinetopia plugin = OpenMinetopia.getInstance();

    @Override
    public @NotNull String getIdentifier() {
        return "OpenMinetopia";
    }

    @Override
    @NotNull
    public String getAuthor() {
        return String.join(", ", plugin.getDescription().getAuthors());
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {
        MinetopiaPlayer minetopiaPlayer = PlayerManager.getInstance().getOnlinePlayers().get(player.getUniqueId());
        FitnessModule fitnessModule = OpenMinetopia.getModuleManager().get(FitnessModule.class);

        if (minetopiaPlayer == null) return null;

        long playtimeInSeconds = minetopiaPlayer.getPlaytime() / 1000;
        long days = playtimeInSeconds / 86400;
        long hours = (playtimeInSeconds % 86400) / 3600;
        long minutes = ((playtimeInSeconds % 86400) % 3600) / 60;
        long seconds = ((playtimeInSeconds % 86400) % 3600) % 60;

        if (params.startsWith("currency_")) {
            String currencyId = params.substring("currency_".length());
            CurrencyModule module = OpenMinetopia.getModuleManager().get(CurrencyModule.class);
            CurrencyModel currencyModel = module.getCurrencyModels().get(player.getUniqueId()).stream()
                    .filter(currency -> currency.getName().equals(currencyId))
                    .findFirst().orElse(null);
            if (currencyModel == null) return null;
            return String.valueOf(currencyModel.getBalance());
        }

        return switch (params.toLowerCase()) {
            case "prefix" -> minetopiaPlayer.getActivePrefix().getPrefix();
            case "level" -> String.valueOf(minetopiaPlayer.getLevel());
            case "calculated_level" -> String.valueOf(minetopiaPlayer.getCalculatedLevel());
            case "city" -> minetopiaPlayer.getPlace().getName();
            case "world" -> minetopiaPlayer.getWorld().getName();
            case "temperature" -> String.valueOf(minetopiaPlayer.getPlace().getTemperature());
            case "city_color" -> minetopiaPlayer.getPlace().getColor();
            case "world_color" -> minetopiaPlayer.getWorld().getColor();
            case "prefix_color" -> minetopiaPlayer.getActiveColor(OwnableColorType.PREFIX).color();
            case "name_color" -> minetopiaPlayer.getActiveColor(OwnableColorType.NAME).color();
            case "chat_color" -> minetopiaPlayer.getActiveColor(OwnableColorType.CHAT).color();
            case "level_color" -> minetopiaPlayer.getActiveColor(OwnableColorType.LEVEL).color();
            case "balance", "balance_formatted" -> {
                BankingModule bankingModule = OpenMinetopia.getModuleManager().get(BankingModule.class);
                BankAccountModel accountModel = bankingModule.getAccountById(player.getUniqueId());
                if (accountModel != null) {
                    yield params.equalsIgnoreCase("balance_formatted")
                            ? bankingModule.format(accountModel.getBalance())
                            : String.valueOf(accountModel.getBalance());
                }
                yield null;
            }
            case "fitness" -> String.valueOf(minetopiaPlayer.getFitness().getTotalFitness());
            case "max_fitness" -> String.valueOf(fitnessModule.getConfiguration().getMaxFitnessLevel());
            case "playtime_total" -> String.valueOf(playtimeInSeconds);
            case "playtime_days" -> String.valueOf(days);
            case "playtime_hours" -> String.valueOf(hours);
            case "playtime_minutes" -> String.valueOf(minutes);
            case "playtime_seconds" -> String.valueOf(seconds);
            default -> null;
        };
    }
}
