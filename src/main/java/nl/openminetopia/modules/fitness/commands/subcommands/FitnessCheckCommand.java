package nl.openminetopia.modules.fitness.commands.subcommands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.api.player.PlayerManager;
import nl.openminetopia.api.player.objects.MinetopiaPlayer;
import nl.openminetopia.configuration.MessageConfiguration;
import nl.openminetopia.utils.ChatUtils;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.concurrent.CompletableFuture;

@CommandAlias("fitness|fitheid")
public class FitnessCheckCommand extends BaseCommand {

    @Subcommand("check")
    @CommandPermission("openminetopia.fitness.check")
    @CommandCompletion("@players")
    public void onCheck(Player sender, OfflinePlayer target) {
        MinetopiaPlayer minetopiaPlayer = PlayerManager.getInstance().getOnlineMinetopiaPlayer(sender);

        if (target.getPlayer() == null) {
            ChatUtils.sendFormattedMessage(minetopiaPlayer, MessageConfiguration.message("player_not_found"));
            return;
        }

        CompletableFuture<MinetopiaPlayer> targetFuture = PlayerManager.getInstance().getMinetopiaPlayer(target.getPlayer());

        targetFuture.whenCompleteAsync((targetMinetopiaPlayer, throwable) -> {
            if (throwable != null) {
                OpenMinetopia.getInstance().getLogger().warning(throwable.getMessage());
                return;
            }

            if (targetMinetopiaPlayer == null) {
                ChatUtils.sendFormattedMessage(minetopiaPlayer, MessageConfiguration.message("player_not_found"));
                return;
            }

            ChatUtils.sendFormattedMessage(minetopiaPlayer, MessageConfiguration.message("fitness_check")
                    .replace("<player>", (target.getName() == null ? "null" : target.getName()))
                    .replace("<fitness>", "" + targetMinetopiaPlayer.getFitness().getTotalFitness())
            );

        });
    }

}