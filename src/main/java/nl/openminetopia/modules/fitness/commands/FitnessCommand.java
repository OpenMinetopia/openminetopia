package nl.openminetopia.modules.fitness.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.HelpCommand;
import co.aikar.commands.annotation.Subcommand;
import nl.openminetopia.api.player.PlayerManager;
import org.bukkit.entity.Player;

@CommandAlias("fitness|fitheid")
public class FitnessCommand extends BaseCommand {

    @HelpCommand
    public void onHelp(CommandHelp help) {
        help.showHelp();
    }

    @Subcommand("trigger")
    public void trigger(Player player) {
        PlayerManager.getInstance().getMinetopiaPlayerAsync(player, minetopiaPlayer -> {
            if (minetopiaPlayer == null) return;

            minetopiaPlayer.getFitness().getRunnable().run();
        }, Throwable::printStackTrace);
    }
}
