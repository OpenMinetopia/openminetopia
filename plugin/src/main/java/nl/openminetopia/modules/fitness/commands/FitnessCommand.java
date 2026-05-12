package nl.openminetopia.modules.fitness.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import nl.openminetopia.api.player.PlayerManager;
import org.bukkit.entity.Player;

@CommandAlias("fitness|fitheid")
public class FitnessCommand extends BaseCommand {

    @HelpCommand
    public void onHelp(CommandHelp help) {
        help.showHelp();
    }

    @Subcommand("trigger")
    @CommandPermission("openminetopia.fitness.trigger")
    @Description("Trigger de fitheid runnable voor een speler (update de fitheid)")
    public void trigger(Player player) {
        PlayerManager.getInstance().getMinetopiaPlayer(player).whenComplete((minetopiaPlayer, throwable) -> {
            if (throwable != null) {
                throwable.printStackTrace();
                return;
            }

            if (minetopiaPlayer == null) return;

            minetopiaPlayer.getFitness().getFitnessModule().getFitnessRunnable().forceMarkDirty(player.getUniqueId());
        });
    }
}
