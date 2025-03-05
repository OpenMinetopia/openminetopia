package nl.openminetopia.modules.places.commands.mtcity.subcommands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.api.player.PlayerManager;
import nl.openminetopia.modules.places.PlacesModule;
import nl.openminetopia.modules.places.models.CityModel;
import nl.openminetopia.modules.places.models.WorldModel;
import nl.openminetopia.utils.ChatUtils;
import nl.openminetopia.utils.WorldGuardUtils;
import org.bukkit.entity.Player;

@CommandAlias("mtstad|mtcity")
public class MTCityCreateCommand extends BaseCommand {

    @Subcommand("create")
    @CommandPermission("openminetopia.city.create")
    public void create(Player player, String name, String loadingName) {

        PlacesModule placesModule = OpenMinetopia.getModuleManager().get(PlacesModule.class);

        PlayerManager.getInstance().getMinetopiaPlayer(player).whenComplete((minetopiaPlayer, throwable) -> {
            if (minetopiaPlayer == null) return;

            WorldModel world = minetopiaPlayer.getWorld();
            if (world == null) {
                player.sendMessage(ChatUtils.color("<red>You are not in a world!"));
                return;
            }

            for (CityModel city : placesModule.getCityModels()) {
                if (city.getName().equalsIgnoreCase(name)) {
                    player.sendMessage(ChatUtils.color("<red>City <white>" + name + " <red>already exists!"));
                    return;
                }
            }

            // check if region exists
            for (ProtectedRegion region : WorldGuardUtils.getProtectedRegions(player.getWorld(), priority -> priority >= 0)) {
                if (!region.getId().equalsIgnoreCase(name)) continue;

                String title = "<bold>" + loadingName.toUpperCase();
                placesModule.createCity(name, title, "<gold>", 21.64, loadingName)
                        .whenComplete((cityModel, throwable1) -> {
                            if (throwable != null) {
                                player.sendMessage(ChatUtils.color("<red>Failed to create city: " + throwable.getMessage()));
                                return;
                            }
                            placesModule.getCityModels().add(cityModel);
                        });

                player.sendMessage(ChatUtils.color("<green>City <white>" + loadingName + " <green>has been created!"));
                return;
            }

            player.sendMessage(ChatUtils.color("<red>Region <white>" + name + " <red>does not exist!"));
        });
    }
}
