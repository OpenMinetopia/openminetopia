package nl.openminetopia.modules.places.commands.mtcity.subcommands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.modules.places.PlacesModule;
import nl.openminetopia.modules.places.models.CityModel;
import nl.openminetopia.utils.ChatUtils;
import org.bukkit.entity.Player;

@CommandAlias("mtstad|mtcity")
public class MTCityRemoveCommand extends BaseCommand {

    @Subcommand("remove")
    @CommandPermission("openminetopia.city.remove")
    public void onRemove(Player player, String cityName) {
        PlacesModule placesModule = OpenMinetopia.getModuleManager().get(PlacesModule.class);

        CityModel cityModel = placesModule.getCity(cityName);
        if (cityModel == null) {
            player.sendMessage(ChatUtils.color("<red>City <white>" + cityName + " <red>does not exist!"));
            return;
        }

        placesModule.deleteCity(cityModel);
        player.sendMessage(ChatUtils.color("<red>World <white>" + cityName + " <red>has been removed!"));
    }
}
