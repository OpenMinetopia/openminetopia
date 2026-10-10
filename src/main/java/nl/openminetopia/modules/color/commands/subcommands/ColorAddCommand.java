package nl.openminetopia.modules.color.commands.subcommands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.annotation.*;
import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.api.player.PlayerManager;
import nl.openminetopia.api.player.objects.MinetopiaPlayer;
import nl.openminetopia.configuration.MessageConfiguration;
import nl.openminetopia.modules.color.ColorModule;
import nl.openminetopia.modules.color.configuration.components.ColorComponent;
import nl.openminetopia.modules.color.enums.OwnableColorType;
import nl.openminetopia.modules.color.objects.OwnableColor;
import nl.openminetopia.utils.ChatUtils;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.util.List;

@CommandAlias("color")
public class ColorAddCommand extends BaseCommand {

    @Subcommand("add")
    @Syntax("<speler> <type|all> <kleur|all> [<minuten>]")
    @CommandCompletion("@players @colorTypes @colorIds @range:0-1440")
    @CommandPermission("openminetopia.color.add")
    @Description("Add a color to a player.")
    public void color(CommandSender sender, OfflinePlayer offlinePlayer, String draftType, @Optional String draftColor, @Optional Long minutes) {
        if (offlinePlayer == null) {
            ChatUtils.sendMessage(sender, MessageConfiguration.message("player_not_found"));
            return;
        }

        List<OwnableColorType> types;
        String colorId;
        if (draftType.equalsIgnoreCase("all")) {
            types = List.of(OwnableColorType.values());
            colorId = "all";
            // With "all" as type there is no colour argument, so the minutes end up in draftColor.
            if (draftColor != null) minutes = parseMinutes(draftColor);
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

        long expiresAt = minutes == null || minutes == -1 ? -1 : System.currentTimeMillis() + minutes * 60 * 1000L;

        PlayerManager.getInstance().getMinetopiaPlayer(offlinePlayer).whenComplete((target, throwable) -> {
            if (target == null) {
                ChatUtils.sendMessage(sender, MessageConfiguration.message("player_not_found"));
                return;
            }

            if (colorId.equals("all")) {
                int added = 0;
                for (OwnableColorType type : types) {
                    for (ColorComponent component : colorModule.getConfiguration().components()) {
                        if (owns(target, type, component.identifier())) continue;
                        target.addColor(type.create(component.identifier(), expiresAt));
                        added++;
                    }
                }

                ChatUtils.sendMessage(sender, MessageConfiguration.message("color_all_added")
                        .replace("<amount>", String.valueOf(added))
                        .replace("<player>", String.valueOf(offlinePlayer.getName())));
                return;
            }

            OwnableColorType type = types.getFirst();
            String key = type.name().toLowerCase();
            if (owns(target, type, colorId)) {
                ChatUtils.sendMessage(sender, MessageConfiguration.message("color_" + key + "_exists"));
                return;
            }

            OwnableColor color = type.create(colorId, expiresAt);
            target.addColor(color);
            target.setActiveColor(color, type);
            ChatUtils.sendMessage(sender, MessageConfiguration.message("color_" + key + "_added")
                    .replace("<color>", color.getColorId()));
        });
    }

    private boolean owns(MinetopiaPlayer player, OwnableColorType type, String colorId) {
        return player.getColors().stream()
                .anyMatch(color -> color.getType() == type && color.getColorId().equalsIgnoreCase(colorId));
    }

    private long parseMinutes(String input) {
        try {
            return Long.parseLong(input);
        } catch (NumberFormatException exception) {
            throw new InvalidCommandArgument(true);
        }
    }
}
