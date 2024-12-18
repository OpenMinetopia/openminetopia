package nl.openminetopia.modules.prefix.commands.subcommands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import nl.openminetopia.api.player.PlayerManager;
import nl.openminetopia.configuration.MessageConfiguration;
import nl.openminetopia.modules.prefix.objects.Prefix;
import nl.openminetopia.utils.ChatUtils;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

@CommandAlias("prefix")
public class PrefixRemoveCommand extends BaseCommand {

    @Subcommand("remove")
    @Syntax("<player> <prefix>")
    @CommandPermission("openminetopia.prefix.remove")
    @CommandCompletion("@players @playerPrefixes")
    @Description("Remove a prefix from a player.")
    public void removePrefix(Player player, OfflinePlayer offlinePlayer, String prefixName) {
        PlayerManager.getInstance().getMinetopiaPlayerAsync(player, minetopiaPlayer -> {
            if (minetopiaPlayer == null) return;

            if (offlinePlayer == null) {
                ChatUtils.sendFormattedMessage(minetopiaPlayer, MessageConfiguration.message("player_not_found"));
                return;
            }

            PlayerManager.getInstance().getMinetopiaPlayerAsync(offlinePlayer, targetMinetopiaPlayer -> {
                if (targetMinetopiaPlayer == null) {
                    ChatUtils.sendFormattedMessage(minetopiaPlayer, MessageConfiguration.message("player_not_found"));
                    return;
                }

                for (Prefix prefix : targetMinetopiaPlayer.getPrefixes()) {
                    if (prefix.getPrefix().equalsIgnoreCase(prefixName)) {
                        ChatUtils.sendFormattedMessage(minetopiaPlayer, MessageConfiguration.message("prefix_removed")
                                .replace("<player>", (offlinePlayer.getName() == null ? "null" : offlinePlayer.getName()))
                                .replace("<prefix>", prefix.getPrefix()));
                        targetMinetopiaPlayer.removePrefix(prefix);
                        return;
                    }
                }
                ChatUtils.sendFormattedMessage(minetopiaPlayer, MessageConfiguration.message("prefix_not_found")
                        .replace("<player>", (offlinePlayer.getName() == null ? "null" : offlinePlayer.getName()))
                        .replace("<prefix>", prefixName));
            }, Throwable::printStackTrace);
        }, Throwable::printStackTrace);
    }
}
