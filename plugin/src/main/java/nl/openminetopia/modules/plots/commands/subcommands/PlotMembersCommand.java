package nl.openminetopia.modules.plots.commands.subcommands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.destroystokyo.paper.profile.PlayerProfile;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import nl.openminetopia.configuration.MessageConfiguration;
import nl.openminetopia.modules.plots.PlotModule;
import nl.openminetopia.modules.plots.utils.PlotUtil;
import nl.openminetopia.utils.ChatUtils;
import nl.openminetopia.utils.WorldGuardUtils;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

@CommandAlias("plot|p")
public class PlotMembersCommand extends BaseCommand {

    @Subcommand("addmember")
    @Description("Voegt een speler toe aan een plot.")
    @CommandCompletion("@players @plotName")
    @Syntax("<speler> <region>")
    public void addPlotMember(Player player, OfflinePlayer offlinePlayer, @Optional String regionName) {
        ProtectedRegion region = PlotUtil.getPlot(player.getLocation());
        if (regionName != null) {
            region = PlotUtil.getPlot(player.getWorld(), regionName);
        }

        if (offlinePlayer == null) {
            ChatUtils.sendMessage(player, MessageConfiguration.message("player_not_found")
                    .replace("<player>", offlinePlayer.getName() != null ? offlinePlayer.getName() : "Onbekend"));
            return;
        }

        if (region == null) {
            ChatUtils.sendMessage(player, MessageConfiguration.message("plot_invalid_location")
                    .replace("<player>", offlinePlayer.getName() != null ? offlinePlayer.getName() : "Onbekend"));
            return;
        }

        if (region.getFlag(PlotModule.PLOT_FLAG) == null) {
            ChatUtils.sendMessage(player, MessageConfiguration.message("plot_not_valid")
                    .replace("<player>", offlinePlayer.getName() != null ? offlinePlayer.getName() : "Onbekend"));
            return;
        }

        if (!region.getOwners().contains(player.getUniqueId()) && !player.hasPermission("openminetopia.plot.removemember")) {
            ChatUtils.sendMessage(player, MessageConfiguration.message("plot_not_owner")
                    .replace("<player>", offlinePlayer.getName() != null ? offlinePlayer.getName() : "Onbekend"));
            return;
        }

        if (region.getMembers().contains(offlinePlayer.getUniqueId()) || region.getOwners().contains(offlinePlayer.getUniqueId())) {
            ChatUtils.sendMessage(player, MessageConfiguration.message("plot_member_already")
                    .replace("<player>", offlinePlayer.getName() != null ? offlinePlayer.getName() : "Onbekend"));
            return;
        }

        region.getMembers().addPlayer(offlinePlayer.getUniqueId());
        ChatUtils.sendMessage(player, MessageConfiguration.message("plot_member_added")
                .replace("<player>", offlinePlayer.getName() != null ? offlinePlayer.getName() : "Onbekend"));
    }

    @Subcommand("removemember")
    @Description("Verwijdert een speler van een plot.")
    @CommandCompletion("@players @plotName")
    @Syntax("<speler> <region>")
    public void removePlotMember(Player player, OfflinePlayer offlinePlayer, @Optional String regionName) {
        ProtectedRegion region = WorldGuardUtils.getProtectedRegion(player.getLocation(), priority -> priority >= 0);
        if (regionName != null) {
            region = WorldGuardUtils.getProtectedRegions(player.getWorld(), p -> p >= 0)
                    .stream()
                    .filter(r -> r.getId().equalsIgnoreCase(regionName))
                    .findFirst()
                    .orElse(null);
        }

        PlayerProfile profile = offlinePlayer.getPlayerProfile();

        if (region == null) {
            ChatUtils.sendMessage(player, MessageConfiguration.message("plot_invalid_location")
                    .replace("<player>", profile.getName() != null ? profile.getName() : "Onbekend"));
            return;
        }

        if (region.getFlag(PlotModule.PLOT_FLAG) == null) {
            ChatUtils.sendMessage(player, MessageConfiguration.message("plot_not_valid")
                    .replace("<player>", profile.getName() != null ? profile.getName() : "Onbekend"));
            return;
        }

        if (!region.getOwners().contains(player.getUniqueId()) && !player.hasPermission("openminetopia.plot.removemember")) {
            ChatUtils.sendMessage(player, MessageConfiguration.message("plot_not_owner")
                    .replace("<player>", profile.getName() != null ? profile.getName() : "Onbekend"));
            return;
        }

        if (!region.getMembers().contains(profile.getId())) {
            ChatUtils.sendMessage(player, MessageConfiguration.message("plot_member_not_added")
                    .replace("<player>", profile.getName() != null ? profile.getName() : "Onbekend"));
            return;
        }

        region.getMembers().removePlayer(profile.getId());
        ChatUtils.sendMessage(player, MessageConfiguration.message("plot_member_removed")
                .replace("<player>", profile.getName() != null ? profile.getName() : "Onbekend"));
    }
}