package nl.openminetopia.modules.plots.commands.subcommands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.destroystokyo.paper.profile.PlayerProfile;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.configuration.MessageConfiguration;
import nl.openminetopia.utils.ChatUtils;
import nl.openminetopia.utils.WorldGuardUtils;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

@CommandAlias("plot|p")
public class PlotOwnersCommand extends BaseCommand {

    @Subcommand("addowner")
    @Description("Voegt een speler toe aan een plot.")
    @CommandPermission("openminetopia.plot.addowner")
    @CommandCompletion("@players")
    @Syntax("<speler>")
    public void addPlotOwner(Player player, OfflinePlayer offlinePlayer) {
        ProtectedRegion region = WorldGuardUtils.getProtectedRegion(player.getLocation(), priority -> priority >= 0);

        if (offlinePlayer == null) {
            ChatUtils.sendMessage(player, MessageConfiguration.message("player_not_found"));
            return;
        }

        if (region == null) {
            ChatUtils.sendMessage(player, MessageConfiguration.message("plot_invalid_location"));
            return;
        }

        if (region.getFlag(OpenMinetopia.PLOT_FLAG) == null) {
            ChatUtils.sendMessage(player, MessageConfiguration.message("plot_invalid"));
            return;
        }

        if (region.getOwners().contains(offlinePlayer.getUniqueId())) {
            ChatUtils.sendMessage(player, MessageConfiguration.message("plot_owner_already")
                    .replace("<player>", offlinePlayer.getName() != null ? offlinePlayer.getName() : "Onbekend"));
            return;
        }

        if (region.getMembers().contains(offlinePlayer.getUniqueId())) {
            region.getMembers().removePlayer(offlinePlayer.getUniqueId());
        }

        region.getOwners().addPlayer(offlinePlayer.getUniqueId());
        ChatUtils.sendMessage(player, MessageConfiguration.message("plot_owner_added")
                .replace("<player>", offlinePlayer.getName() != null ? offlinePlayer.getName() : "Onbekend"));
    }

    @Subcommand("removeowner")
    @Description("Verwijdert een speler van een plot.")
    @CommandPermission("openminetopia.plot.removeowner")
    @Syntax("<speler>")
    public void removePlotOwner(Player player, OfflinePlayer offlinePlayer) {
        ProtectedRegion region = WorldGuardUtils.getProtectedRegion(player.getLocation(), priority -> priority >= 0);
        PlayerProfile profile = offlinePlayer.getPlayerProfile();
        
        if (region == null) {
            ChatUtils.sendMessage(player, MessageConfiguration.message("plot_invalid_location"));
            return;
        }

        if (region.getFlag(OpenMinetopia.PLOT_FLAG) == null) {
            ChatUtils.sendMessage(player, MessageConfiguration.message("plot_invalid"));
            return;
        }

        if (!region.getOwners().contains(profile.getId())) {
            ChatUtils.sendMessage(player, MessageConfiguration.message("plot_owner_not_added")
                    .replace("<player>", profile.getName() != null ? profile.getName() : "Onbekend"));
            return;
        }

        region.getOwners().removePlayer(profile.getId());
        ChatUtils.sendMessage(player, MessageConfiguration.message("plot_owner_removed")
                .replace("<player>", profile.getName() != null ? profile.getName() : "Onbekend"));
    }
}
