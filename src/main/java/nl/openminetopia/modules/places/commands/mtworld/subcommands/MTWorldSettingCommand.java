package nl.openminetopia.modules.places.commands.mtworld.subcommands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.modules.data.storm.StormDatabase;
import nl.openminetopia.modules.places.PlacesModule;
import nl.openminetopia.modules.places.models.WorldModel;
import nl.openminetopia.utils.ChatUtils;
import org.bukkit.entity.Player;

@CommandAlias("mtwereld|mtworld")
public class MTWorldSettingCommand extends BaseCommand {

    private final PlacesModule placesModule = OpenMinetopia.getModuleManager().get(PlacesModule.class);

    @Subcommand("setcolor")
    @CommandPermission("openminetopia.world.setcolor")
    @CommandCompletion("@worldNames")
    public void setColor(Player player, String worldName, String color) {
        WorldModel worldModel = placesModule.getWorldModels().stream()
                .filter(worldModel1 -> worldModel1.getName().equalsIgnoreCase(worldName))
                .findFirst().orElse(null);
        if (worldModel == null) {
            player.sendMessage(ChatUtils.color("<red>World <white>" + worldName + " <red>does not exist!"));
            return;
        }

        worldModel.setColor(color);
        StormDatabase.getInstance().saveStormModel(worldModel);
        player.sendMessage(ChatUtils.color("<red>World color of " + color + worldName + " <red>has been changed!"));
    }

    @Subcommand("settemperature")
    @CommandPermission("openminetopia.world.settemperature")
    @CommandCompletion("@worldNames")
    public void setTemperature(Player player, String worldName, Double temperature) {
        WorldModel worldModel = placesModule.getWorldModels().stream()
                .filter(worldModel1 -> worldModel1.getName().equalsIgnoreCase(worldName))
                .findFirst().orElse(null);
        if (worldModel == null) {
            player.sendMessage(ChatUtils.color("<red>World <white>" + worldName + " <red>does not exist!"));
            return;
        }

        worldModel.setTemperature(temperature);
        StormDatabase.getInstance().saveStormModel(worldModel);
        player.sendMessage(ChatUtils.color("<red>World temperatuur of " + worldName + " <red>has been changed!"));
    }

    @Subcommand("setloadingname")
    @CommandPermission("openminetopia.world.setloadingname")
    @CommandCompletion("@worldNames")
    public void setLoadingName(Player player, String worldName, String loadingName) {
        WorldModel worldModel = placesModule.getWorldModels().stream()
                .filter(worldModel1 -> worldModel1.getName().equalsIgnoreCase(worldName))
                .findFirst().orElse(null);
        if (worldModel == null) {
            player.sendMessage(ChatUtils.color("<red>World <white>" + worldName + " <red>does not exist!"));
            return;
        }

        worldModel.setLoadingName(loadingName);
        StormDatabase.getInstance().saveStormModel(worldModel);
        player.sendMessage(ChatUtils.color("<red>World loadingName of " + worldName + " <red>has been changed!"));
    }

    @Subcommand("settitle")
    @CommandPermission("openminetopia.world.settitle")
    @CommandCompletion("@worldNames")
    public void setTitle(Player player, String worldName, String title) {
        WorldModel worldModel = placesModule.getWorldModels().stream()
                .filter(worldModel1 -> worldModel1.getName().equalsIgnoreCase(worldName))
                .findFirst().orElse(null);
        if (worldModel == null) {
            player.sendMessage(ChatUtils.color("<red>World <white>" + worldName + " <red>does not exist!"));
            return;
        }

        worldModel.setTitle(title);
        StormDatabase.getInstance().saveStormModel(worldModel);
        player.sendMessage(ChatUtils.color("<red>World title of " + worldName + " <red>has been changed!"));
    }
}
