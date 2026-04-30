package nl.openminetopia.modules.places.commands.mtcity.subcommands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.configuration.MessageConfiguration;
import nl.openminetopia.modules.data.storm.StormDatabase;
import nl.openminetopia.modules.places.PlacesModule;
import nl.openminetopia.modules.places.models.CityModel;
import nl.openminetopia.utils.ChatUtils;
import org.bukkit.entity.Player;

@CommandAlias("mtstad|mtcity")
public class MTCitySettingCommand extends BaseCommand {

    @Subcommand("setcolor")
    @CommandPermission("openminetopia.city.setcolor")
    @CommandCompletion("@cityNames")
    public void setColor(Player player, String cityName, String color) {
        PlacesModule placesModule = OpenMinetopia.getModuleManager().get(PlacesModule.class);

        CityModel cityModel = placesModule.getCity(cityName);
        if (cityModel == null) {
            player.sendMessage(ChatUtils.color(MessageConfiguration.message("places_city_not_found")
                    .replace("<city>", cityName)));
            return;
        }

        cityModel.setColor(color);
        StormDatabase.getInstance().saveStormModel(cityModel);
        player.sendMessage(ChatUtils.color(MessageConfiguration.message("places_city_color_changed")
                .replace("<city>", cityName)
                .replace("<new_color>", color)));
    }

    @Subcommand("settemperature")
    @CommandPermission("openminetopia.city.settemperature")
    @CommandCompletion("@cityNames")
    public void setTemperature(Player player, String cityName, Double temperature) {
        PlacesModule placesModule = OpenMinetopia.getModuleManager().get(PlacesModule.class);

        CityModel cityModel = placesModule.getCity(cityName);
        if (cityModel == null) {
            player.sendMessage(ChatUtils.color(MessageConfiguration.message("places_city_not_found")
                    .replace("<city>", cityName)));
            return;
        }

        cityModel.setTemperature(temperature);
        StormDatabase.getInstance().saveStormModel(cityModel);
        player.sendMessage(ChatUtils.color(MessageConfiguration.message("places_city_temperature_changed")
                .replace("<city>", cityName)));
    }

    @Subcommand("setloadingname")
    @CommandPermission("openminetopia.city.setloadingname")
    @CommandCompletion("@cityNames")
    public void setLoadingName(Player player, String cityName, String loadingName) {
        PlacesModule placesModule = OpenMinetopia.getModuleManager().get(PlacesModule.class);

        CityModel cityModel = placesModule.getCity(cityName);
        if (cityModel == null) {
            player.sendMessage(ChatUtils.color(MessageConfiguration.message("places_city_not_found")
                    .replace("<city>", cityName)));
            return;
        }

        cityModel.setLoadingName(loadingName);
        StormDatabase.getInstance().saveStormModel(cityModel);
        player.sendMessage(ChatUtils.color(MessageConfiguration.message("places_city_loadingname_changed")
                .replace("<city>", cityName)));
    }

    @Subcommand("settitle")
    @CommandPermission("openminetopia.city.settitle")
    @CommandCompletion("@cityNames")
    public void setTitle(Player player, String cityName, String title) {
        PlacesModule placesModule = OpenMinetopia.getModuleManager().get(PlacesModule.class);

        CityModel cityModel = placesModule.getCity(cityName);
        if (cityModel == null) {
            player.sendMessage(ChatUtils.color(MessageConfiguration.message("places_city_not_found")
                    .replace("<city>", cityName)));
            return;
        }

        cityModel.setTitle(title);
        StormDatabase.getInstance().saveStormModel(cityModel);
        player.sendMessage(ChatUtils.color(MessageConfiguration.message("places_city_title_changed")
                .replace("<city>", cityName)));
    }
}
