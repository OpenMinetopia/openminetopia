package nl.openminetopia.modules.prefix.commands.subcommands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import nl.openminetopia.api.player.PlayerManager;
import nl.openminetopia.api.player.objects.MinetopiaPlayer;
import nl.openminetopia.modules.data.storm.StormDatabase;
import nl.openminetopia.modules.data.storm.models.PrefixModel;
import nl.openminetopia.modules.data.utils.StormUtils;
import nl.openminetopia.modules.prefix.objects.Prefix;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

@CommandAlias("prefix")
public class PrefixAddCommand extends BaseCommand {

    /**
     * Add a prefix to a player.
     * @param expiresAt The time in minutes when the prefix expires.
     */

    @Subcommand("add")
    @Syntax("<player> <expiresAt> <prefix>")
    @CommandCompletion("@players")
    @CommandPermission("openminetopia.prefix.add")
    @Description("Add a prefix to a player.")
    public static void addPrefix(Player player, OfflinePlayer offlinePlayer, Integer expiresAt, String prefix) {
        if (offlinePlayer.getPlayer() == null) {
            player.sendMessage("This player does not exist.");
            return;
        }

        MinetopiaPlayer minetopiaPlayer = PlayerManager.getInstance().getMinetopiaPlayer(offlinePlayer.getPlayer());
        if (minetopiaPlayer == null) return;
        player.sendMessage("Added the prefix to the player.");

        long expiresAtMillis = System.currentTimeMillis() + (expiresAt * 60 * 1000);
        Prefix prefix1 = new Prefix(prefix, expiresAtMillis);
        minetopiaPlayer.addPrefix(prefix1);
    }
}