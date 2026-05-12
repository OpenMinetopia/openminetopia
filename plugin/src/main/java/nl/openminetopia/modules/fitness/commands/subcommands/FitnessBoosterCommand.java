package nl.openminetopia.modules.fitness.commands.subcommands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import nl.openminetopia.api.player.PlayerManager;
import nl.openminetopia.api.player.objects.MinetopiaPlayer;
import nl.openminetopia.configuration.MessageConfiguration;
import nl.openminetopia.utils.ChatUtils;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.concurrent.CompletableFuture;

@CommandAlias("fitness|fitheid")
public class FitnessBoosterCommand extends BaseCommand {

    @Subcommand("booster")
    @CommandPermission("openminetopia.fitness.booster")
    @CommandCompletion("@players")
    public void booster(Player player, OfflinePlayer offlinePlayer, int amount, @Optional Integer expiresAt) {
        MinetopiaPlayer minetopiaPlayer = PlayerManager.getInstance().getOnlineMinetopiaPlayer(player);

        if (offlinePlayer.getPlayer() == null) {
            ChatUtils.sendFormattedMessage(minetopiaPlayer, MessageConfiguration.message("player_not_found"));
            return;
        }

        CompletableFuture<MinetopiaPlayer> targetFuture = PlayerManager.getInstance().getMinetopiaPlayer(offlinePlayer.getPlayer());

        targetFuture.whenComplete((targetMinetopiaPlayer, throwable) -> {
            if (throwable != null) {
                throwable.printStackTrace();
                return;
            }

            if (targetMinetopiaPlayer == null) {
                ChatUtils.sendFormattedMessage(minetopiaPlayer, MessageConfiguration.message("player_not_found"));
                return;
            }

            int expiry = expiresAt == null || expiresAt <= 0 ? -1 : expiresAt;
            long expiresAtMillis = expiry == -1 ? -1 : System.currentTimeMillis() + (expiry * 1000L);

            targetMinetopiaPlayer.getFitness().addBooster(amount, expiresAtMillis);

            ChatUtils.sendFormattedMessage(minetopiaPlayer, MessageConfiguration.message("fitness_booster_added_to")
                    .replace("<player>", (offlinePlayer.getName() == null ? "null" : offlinePlayer.getName())));
        });
    }
}