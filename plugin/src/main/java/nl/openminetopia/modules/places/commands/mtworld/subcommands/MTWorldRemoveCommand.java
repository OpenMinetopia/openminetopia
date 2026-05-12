package nl.openminetopia.modules.places.commands.mtworld.subcommands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.modules.places.PlacesModule;
import nl.openminetopia.modules.places.models.WorldModel;
import nl.openminetopia.utils.ChatUtils;
import org.bukkit.entity.Player;

@CommandAlias("mtwereld|mtworld")
public class MTWorldRemoveCommand extends BaseCommand {

    @Subcommand("remove")
    @CommandPermission("openminetopia.world.remove")
    public void onRemove(Player player, String worldName) {
        // Remove the world from the database

        PlacesModule placesModule = OpenMinetopia.getModuleManager().get(PlacesModule.class);

        WorldModel worldModel = placesModule.getWorldModels().stream()
                .filter(model -> model.getName().equalsIgnoreCase(worldName))
                .findFirst()
                .orElse(null);

        if (worldModel == null) {
            player.sendMessage(ChatUtils.color("<red>World <white>" + worldName + " <red>does not exist!"));
            return;
        }

        placesModule.deleteWorld(worldName);
        placesModule.getWorldModels().remove(worldModel);

        player.sendMessage(ChatUtils.color("<red>World <white>" + worldName + " <red>has been removed!"));
    }
}
