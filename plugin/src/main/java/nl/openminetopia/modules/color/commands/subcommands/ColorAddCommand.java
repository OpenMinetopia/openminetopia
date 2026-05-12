package nl.openminetopia.modules.color.commands.subcommands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.api.player.PlayerManager;
import nl.openminetopia.api.player.objects.MinetopiaPlayer;
import nl.openminetopia.configuration.MessageConfiguration;
import nl.openminetopia.modules.color.ColorModule;
import nl.openminetopia.modules.color.enums.OwnableColorType;
import nl.openminetopia.modules.color.objects.ChatColor;
import nl.openminetopia.modules.color.objects.LevelColor;
import nl.openminetopia.modules.color.objects.NameColor;
import nl.openminetopia.modules.color.objects.PrefixColor;
import nl.openminetopia.utils.ChatUtils;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("color")
public class ColorAddCommand extends BaseCommand {

    @Subcommand("add")
    @Syntax("<speler> <type> <kleur> [<minuten>]")
    @CommandCompletion("@players @colorTypes @colorIds @range:0-1440")
    @CommandPermission("openminetopia.color.add")
    @Description("Add a color to a player.")
    public void color(CommandSender player, OfflinePlayer offlinePlayer, OwnableColorType type, String draftColor, @Optional Long expiresAt) {
        if (offlinePlayer == null) {
            ChatUtils.sendMessage(player, MessageConfiguration.message("player_not_found"));
            return;
        }

        ColorModule colorModule = OpenMinetopia.getModuleManager().get(ColorModule.class);

        final String colorId = draftColor.toLowerCase();
        if (!colorModule.getConfiguration().exists(colorId)) {
            ChatUtils.sendMessage(player, MessageConfiguration.message("color_not_found"));
            return;
        }

        if (expiresAt == null) {
            expiresAt = -1L;
        }
        long finalExpiresAt = expiresAt;

        PlayerManager.getInstance().getMinetopiaPlayer(offlinePlayer).whenComplete((targetMinetopiaPlayer, throwable1) -> {
            if (targetMinetopiaPlayer == null) {
                ChatUtils.sendMessage(player, MessageConfiguration.message("player_not_found"));
                return;
            }

            long expiresAtMillis = System.currentTimeMillis() + minutesToMillis(finalExpiresAt);
            if (finalExpiresAt == -1) expiresAtMillis = -1;

            switch (type) {
                case PREFIX:
                    if (targetMinetopiaPlayer.getColors().stream().anyMatch(prefixColor -> prefixColor.getColorId().equalsIgnoreCase(colorId) && prefixColor.getType() == type)) {
                        ChatUtils.sendMessage(player, MessageConfiguration.message("color_prefix_exists"));
                        return;
                    }

                    PrefixColor prefixColor = new PrefixColor(colorId, expiresAtMillis);
                    targetMinetopiaPlayer.addColor(prefixColor);
                    targetMinetopiaPlayer.setActiveColor(prefixColor, OwnableColorType.PREFIX);
                    ChatUtils.sendMessage(player, MessageConfiguration.message("color_prefix_added")
                            .replace("<color>", prefixColor.getColorId()));
                    break;

                case CHAT:
                    if (targetMinetopiaPlayer.getColors().stream().anyMatch(chatColor -> chatColor.getColorId().equalsIgnoreCase(colorId) && chatColor.getType() == type)) {
                        ChatUtils.sendMessage(player, MessageConfiguration.message("color_chat_exists"));
                        return;
                    }

                    ChatColor chatColor = new ChatColor(colorId, expiresAtMillis);
                    targetMinetopiaPlayer.addColor(chatColor);
                    targetMinetopiaPlayer.setActiveColor(chatColor, OwnableColorType.CHAT);
                    ChatUtils.sendMessage(player, MessageConfiguration.message("color_chat_added")
                            .replace("<color>", chatColor.getColorId()));
                    break;
                case NAME:
                    if (targetMinetopiaPlayer.getColors().stream().anyMatch(nameColor -> nameColor.getColorId().equalsIgnoreCase(colorId) && nameColor.getType() == type)) {
                        ChatUtils.sendMessage(player, MessageConfiguration.message("color_name_exists"));
                        return;
                    }

                    NameColor nameColor = new NameColor(colorId, expiresAtMillis);
                    targetMinetopiaPlayer.addColor(nameColor);
                    targetMinetopiaPlayer.setActiveColor(nameColor, OwnableColorType.NAME);
                    ChatUtils.sendMessage(player, MessageConfiguration.message("color_name_added")
                            .replace("<color>", nameColor.getColorId()));
                    break;
                case LEVEL:
                    if (targetMinetopiaPlayer.getColors().stream().anyMatch(levelColor -> levelColor.getColorId().equalsIgnoreCase(colorId) && levelColor.getType() == type)) {
                        ChatUtils.sendMessage(player, MessageConfiguration.message("color_level_exists"));
                        return;
                    }

                    LevelColor levelColor = new LevelColor(colorId, expiresAtMillis);
                    targetMinetopiaPlayer.addColor(levelColor);
                    targetMinetopiaPlayer.setActiveColor(levelColor, OwnableColorType.LEVEL);
                    ChatUtils.sendMessage(player, MessageConfiguration.message("color_level_added")
                            .replace("<color>", levelColor.getColorId()));

                    break;
            }
        });
    }

    private long minutesToMillis(long minutes) {
        return minutes * 60 * 1000L;
    }
}
