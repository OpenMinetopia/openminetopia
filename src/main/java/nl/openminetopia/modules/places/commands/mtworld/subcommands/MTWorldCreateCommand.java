package nl.openminetopia.modules.places.commands.mtworld.subcommands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.api.player.PlayerManager;
import nl.openminetopia.api.player.ScoreboardManager;
import nl.openminetopia.api.player.objects.MinetopiaPlayer;
import nl.openminetopia.modules.places.PlacesModule;
import nl.openminetopia.modules.places.models.WorldModel;
import nl.openminetopia.utils.ChatUtils;
import org.bukkit.entity.Player;

@CommandAlias("mtwereld|mtworld")
public class MTWorldCreateCommand extends BaseCommand {

    @Subcommand("create")
    @CommandPermission("openminetopia.world.create")
    public void create(Player player, String loadingName) {
        String title = "<bold>" + loadingName.toUpperCase();

        PlacesModule placesModule = OpenMinetopia.getModuleManager().get(PlacesModule.class);
        for (WorldModel worldModel : placesModule.getWorldModels()) {
            if (worldModel.getName().equalsIgnoreCase(player.getWorld().getName())) {
                player.sendMessage(ChatUtils.color("<red>World <white>" + loadingName + " <red>already exists!"));
                return;
            }
        }

        placesModule.createWorld(player.getWorld().getName(), title, "<gold>", 21.64, loadingName)
                .whenComplete((worldModel, throwable) -> {
                    if (throwable != null) {
                        player.sendMessage(ChatUtils.color("<red>Failed to create world: " + throwable.getMessage()));
                        return;
                    }
                    placesModule.getWorldModels().add(worldModel);
                });

        player.sendMessage(ChatUtils.color("<green>World <white>" + loadingName + " <green>has been created!"));

        for (Player worldPlayer : player.getWorld().getPlayers()) {
            MinetopiaPlayer minetopiaPlayer = PlayerManager.getInstance().getOnlineMinetopiaPlayer(worldPlayer);
            if (minetopiaPlayer == null) return;

            minetopiaPlayer.getFitness().getRunnable().run();

            ScoreboardManager.getInstance().addScoreboard(worldPlayer);
        }
    }
}
