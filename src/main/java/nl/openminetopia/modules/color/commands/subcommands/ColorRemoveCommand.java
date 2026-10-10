package nl.openminetopia.modules.color.commands.subcommands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.annotation.*;
import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.api.player.PlayerManager;
import nl.openminetopia.configuration.MessageConfiguration;
import nl.openminetopia.modules.color.ColorModule;
import nl.openminetopia.modules.color.enums.OwnableColorType;
import nl.openminetopia.modules.color.objects.OwnableColor;
import nl.openminetopia.utils.ChatUtils;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.util.List;

@CommandAlias("color")
public class ColorRemoveCommand extends BaseCommand {

    @Subcommand("remove")
    @Syntax("<speler> <type|all> <kleur|all>")
    @CommandCompletion("@players @colorTypes @playerColors")
    @CommandPermission("openminetopia.color.remove")
    @Description("Remove a color from a player.")
    public void remove(CommandSender sender, OfflinePlayer offlinePlayer, String draftType, @Optional String draftColor) {
        if (offlinePlayer == null) {
            ChatUtils.sendMessage(sender, MessageConfiguration.message("player_not_found"));
            return;
        }

        List<OwnableColorType> types;
        String colorId;
        if (draftType.equalsIgnoreCase("all")) {
            types = List.of(OwnableColorType.values());
            colorId = "all";
        } else {
            OwnableColorType type = OwnableColorType.byName(draftType);
            if (type == null) {
                ChatUtils.sendMessage(sender, MessageConfiguration.message("color_type_not_found"));
                return;
            }
            if (draftColor == null) throw new InvalidCommandArgument(true);

            types = List.of(type);
            colorId = draftColor.toLowerCase();
        }

        ColorModule colorModule = OpenMinetopia.getModuleManager().get(ColorModule.class);
        if (!colorId.equals("all") && !colorModule.getConfiguration().exists(colorId)) {
            ChatUtils.sendMessage(sender, MessageConfiguration.message("color_not_found"));
            return;
        }

        PlayerManager.getInstance().getMinetopiaPlayer(offlinePlayer).whenComplete((target, throwable) -> {
            if (target == null) {
                ChatUtils.sendMessage(sender, MessageConfiguration.message("player_not_found"));
                return;
            }

            if (colorId.equals("all")) {
                List<OwnableColor> colors = target.getColors().stream()
                        .filter(color -> types.contains(color.getType()))
                        .toList();
                colors.forEach(target::removeColor);
                types.forEach(type -> target.setActiveColor(type.defaultColor(), type));

                ChatUtils.sendMessage(sender, MessageConfiguration.message("color_all_removed")
                        .replace("<amount>", String.valueOf(colors.size()))
                        .replace("<player>", String.valueOf(offlinePlayer.getName())));
                return;
            }

            OwnableColorType type = types.getFirst();
            String key = type.name().toLowerCase();
            OwnableColor color = target.getColors().stream()
                    .filter(c -> c.getType() == type && c.getColorId().equals(colorId))
                    .findAny().orElse(null);
            if (color == null) {
                ChatUtils.sendMessage(sender, MessageConfiguration.message("color_" + key + "_not_found"));
                return;
            }

            target.removeColor(color);
            ChatUtils.sendMessage(sender, MessageConfiguration.message("color_" + key + "_removed")
                    .replace("<color>", color.getColorId()));
        });
    }
}
