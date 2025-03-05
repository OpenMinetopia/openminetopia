package nl.openminetopia.utils.placeholderapi;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.api.player.PlayerManager;
import nl.openminetopia.api.player.objects.MinetopiaPlayer;
import nl.openminetopia.modules.banking.BankingModule;
import nl.openminetopia.modules.banking.models.BankAccountModel;
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
    public String onRequest(OfflinePlayer player, @NotNull String params) {
        MinetopiaPlayer minetopiaPlayer = PlayerManager.getInstance().getOnlinePlayers().get(player.getUniqueId());

        if (minetopiaPlayer == null) return null;

        long playtimeInSeconds = minetopiaPlayer.getPlaytime();
        long days = playtimeInSeconds / 86400;
        long hours = (playtimeInSeconds % 86400) / 3600;
        long minutes = ((playtimeInSeconds % 86400) % 3600) / 60;
        long seconds = ((playtimeInSeconds % 86400) % 3600) % 60;

        return switch (params.toLowerCase()) {
            case "prefix" -> minetopiaPlayer.getActivePrefix().getPrefix();
            case "level" -> String.valueOf(minetopiaPlayer.getLevel());
            case "calculated_level" -> String.valueOf(minetopiaPlayer.getCalculatedLevel());
            case "city" -> minetopiaPlayer.getPlace().getName();
            case "world" -> minetopiaPlayer.getWorld().getName();
            case "temperature" -> String.valueOf(minetopiaPlayer.getPlace().getTemperature());
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
            case "max_fitness" -> String.valueOf(OpenMinetopia.getFitnessConfiguration().getMaxFitnessLevel());
            case "playtime_total" -> String.valueOf(playtimeInSeconds);
            case "playtime_days" -> String.valueOf(days);
            case "playtime_hours" -> String.valueOf(hours);
            case "playtime_minutes" -> String.valueOf(minutes);
            case "playtime_seconds" -> String.valueOf(seconds);
            default -> null;
        };
    }
}
