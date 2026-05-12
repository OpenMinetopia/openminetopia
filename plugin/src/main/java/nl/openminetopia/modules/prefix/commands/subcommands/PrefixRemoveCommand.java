package nl.openminetopia.modules.prefix.commands.subcommands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import nl.openminetopia.api.player.PlayerManager;
import nl.openminetopia.configuration.MessageConfiguration;
import nl.openminetopia.modules.prefix.objects.Prefix;
import nl.openminetopia.utils.ChatUtils;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("prefix")
public class PrefixRemoveCommand extends BaseCommand {

    @Subcommand("remove")
    @Syntax("<player> <prefix>")
    @CommandPermission("openminetopia.prefix.remove")
    @CommandCompletion("@players @playerPrefixes")
    @Description("Verwijder een prefix van een speler.")
    public void removePrefix(CommandSender sender, OfflinePlayer offlinePlayer, String prefixName) {
        if (offlinePlayer == null) {
            ChatUtils.sendMessage(sender, MessageConfiguration.message("player_not_found"));
            return;
        }

        PlayerManager.getInstance().getMinetopiaPlayer(offlinePlayer).whenComplete((targetMinetopiaPlayer, throwable1) -> {
            if (targetMinetopiaPlayer == null) {
                ChatUtils.sendMessage(sender, MessageConfiguration.message("player_not_found"));
                return;
            }

            for (Prefix prefix : targetMinetopiaPlayer.getPrefixes()) {
                if (prefix.getPrefix().equalsIgnoreCase(prefixName)) {
                    ChatUtils.sendMessage(sender, MessageConfiguration.message("prefix_removed")
                            .replace("<player>", (offlinePlayer.getName() == null ? "null" : offlinePlayer.getName()))
                            .replace("<prefix>", prefix.getPrefix()));
                    targetMinetopiaPlayer.removePrefix(prefix);
                    return;
                }
            }
            ChatUtils.sendMessage(sender, MessageConfiguration.message("prefix_not_found")
                    .replace("<player>", (offlinePlayer.getName() == null ? "null" : offlinePlayer.getName()))
                    .replace("<prefix>", prefixName));
        });
    }
}
