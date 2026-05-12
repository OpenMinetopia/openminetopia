package nl.openminetopia.modules.fitness.commands.subcommands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import nl.openminetopia.api.player.PlayerManager;
import nl.openminetopia.api.player.objects.MinetopiaPlayer;
import nl.openminetopia.configuration.MessageConfiguration;
import nl.openminetopia.utils.ChatUtils;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("fitness|fitheid")
public class FitnessResetCommand extends BaseCommand {

    @Subcommand("reset")
    @Description("Reset je fitheid")
    public void reset(Player player) {
        MinetopiaPlayer minetopiaPlayer = PlayerManager.getInstance().getOnlineMinetopiaPlayer(player);

        if (minetopiaPlayer == null) {
            ChatUtils.sendMessage(player, MessageConfiguration.message("player_data_not_loaded"));
            return;
        }

        if (!minetopiaPlayer.getFitness().isFitnessReset()) {
            ChatUtils.sendMessage(player, MessageConfiguration.message("fitness_no_reset"));
            return;
        }

        minetopiaPlayer.getFitness().reset();
        ChatUtils.sendMessage(player, MessageConfiguration.message("fitness_reset"));
    }

    @Subcommand("togglereset")
    @Description("Geef een speler de mogelijkheid om zijn fitheid te resetten")
    @CommandPermission("openminetopia.fitness.togglereset")
    public void toggle(CommandSender sender, OfflinePlayer target, Boolean enabled) {
        if (target == null) {
            ChatUtils.sendMessage(sender, MessageConfiguration.message("player_not_found"));
            return;
        }

        PlayerManager.getInstance().getMinetopiaPlayer(target).whenComplete((minetopiaPlayer, throwable) -> {
            if (throwable != null) {
                ChatUtils.sendMessage(sender, MessageConfiguration.message("player_data_not_loaded"));
                throwable.printStackTrace();
                return;
            }

            if (minetopiaPlayer == null) {
                ChatUtils.sendMessage(sender, MessageConfiguration.message("player_not_found"));
                return;
            }

            minetopiaPlayer.getFitness().setFitnessReset(enabled);

            ChatUtils.sendMessage(sender, MessageConfiguration.message("fitness_reset_" + (enabled ? "enabled" : "disabled"))
                    .replace("<player>", target.getName() == null ? "null" : target.getName()));
        });
    }
}
