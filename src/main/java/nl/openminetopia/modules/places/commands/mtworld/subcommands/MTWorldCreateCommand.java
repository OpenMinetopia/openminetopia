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
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.concurrent.CompletionException;

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

        String worldName = player.getWorld().getName();
        placesModule.createWorld(worldName, title, "<gold>", 21.64, loadingName)
                .whenComplete((worldModel, throwable) -> Bukkit.getScheduler().runTask(OpenMinetopia.getInstance(), () -> {
                    if (throwable != null) {
                        OpenMinetopia.getInstance().getLogger().severe("Failed to save world " + worldName + ": " + cause(throwable).getMessage());
                        player.sendMessage(ChatUtils.color("<red>Failed to create world: " + cause(throwable).getMessage()));
                        return;
                    }
                    placesModule.getWorldModels().add(worldModel);

                    player.sendMessage(ChatUtils.color("<green>World <white>" + loadingName + " <green>has been created!"));

                    World world = Bukkit.getWorld(worldName);
                    if (world == null) return;
                    for (Player worldPlayer : world.getPlayers()) {
                        MinetopiaPlayer minetopiaPlayer = PlayerManager.getInstance().getOnlineMinetopiaPlayer(worldPlayer);
                        if (minetopiaPlayer == null) continue;

                        minetopiaPlayer.getFitness().getFitnessModule().getFitnessRunnable().forceMarkDirty(worldPlayer.getUniqueId());

                        ScoreboardManager.getInstance().addScoreboard(worldPlayer);
                    }
                }));
    }

    /** The save's own exception rather than the CompletionException wrapping it. */
    private static Throwable cause(Throwable throwable) {
        return throwable instanceof CompletionException && throwable.getCause() != null ? throwable.getCause() : throwable;
    }
}
