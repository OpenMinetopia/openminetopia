package nl.openminetopia.modules.color.commands.subcommands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.api.player.PlayerManager;
import nl.openminetopia.api.player.objects.MinetopiaPlayer;
import nl.openminetopia.configuration.MessageConfiguration;
import nl.openminetopia.modules.color.ColorModule;
import nl.openminetopia.utils.ChatUtils;
import org.bukkit.entity.Player;

@CommandAlias("color")
public class ColorCreateCommand extends BaseCommand {

    @Subcommand("create")
    @Syntax("<identifier> <display_name> <prefix_color>")
    @CommandPermission("openminetopia.color.create")
    @Description("Add a new color to the configuration.")
    public void create(Player player, String identifier, String displayName, String prefixColor) {
        MinetopiaPlayer minetopiaPlayer = PlayerManager.getInstance().getOnlineMinetopiaPlayer(player);
        if (minetopiaPlayer == null) return;
        ColorModule colorModule = OpenMinetopia.getModuleManager().get(ColorModule.class);
        colorModule.getConfiguration().createColor(identifier, displayName, prefixColor);
        ChatUtils.sendFormattedMessage(minetopiaPlayer, MessageConfiguration.message("color_created")
                .replace("<color>", displayName)
                .replace("<identifier>", identifier));
    }
}
