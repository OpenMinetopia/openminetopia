package nl.openminetopia.modules.plots.commands.subcommands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import net.kyori.adventure.text.Component;
import nl.openminetopia.api.player.PlayerManager;
import nl.openminetopia.api.player.objects.MinetopiaPlayer;
import nl.openminetopia.configuration.MessageConfiguration;
import nl.openminetopia.modules.plots.PlotModule;
import nl.openminetopia.modules.plots.utils.PlotUtil;
import nl.openminetopia.utils.ChatUtils;
import nl.openminetopia.utils.WorldGuardUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.stream.Collectors;

@CommandAlias("plotinfo|pi")
public class PlotInfoCommand extends BaseCommand {

    @Default
    @Description("Bekijk informatie van een plot.")
    public void plotInfo(Player player) {
        ProtectedRegion region = PlotUtil.getPlot(player.getLocation());

        MinetopiaPlayer minetopiaPlayer = PlayerManager.getInstance().getOnlineMinetopiaPlayer(player);
        if (minetopiaPlayer == null) return;

        if (region == null) {
            ChatUtils.sendFormattedMessage(minetopiaPlayer, MessageConfiguration.message("plot_invalid_location"));
            return;
        }

        if (region.getFlag(PlotModule.PLOT_FLAG) == null) {
            ChatUtils.sendFormattedMessage(minetopiaPlayer, MessageConfiguration.message("plot_not_valid"));
            return;
        }

        String owners = region.getOwners().getUniqueIds().stream()
                .map(ownerId -> Bukkit.getOfflinePlayer(ownerId).getName())
                .collect(Collectors.joining(", "));

        String members = region.getMembers().getUniqueIds().stream()
                .map(memberId -> Bukkit.getOfflinePlayer(memberId).getName())
                .collect(Collectors.joining(", "));

        ChatUtils.sendFormattedMessage(minetopiaPlayer, MessageConfiguration.message("plot_info_header"));
        ChatUtils.sendFormattedMessage(minetopiaPlayer, MessageConfiguration.message("plot_info_title")
                .replace("<plot>", region.getId()));
        player.sendMessage(Component.empty());
        ChatUtils.sendFormattedMessage(minetopiaPlayer, MessageConfiguration.message("plot_info_owners")
                .replace("<owners>", (region.getOwners().size() > 0 ? owners : "Geen.")));
        ChatUtils.sendFormattedMessage(minetopiaPlayer, MessageConfiguration.message("plot_info_members")
                .replace("<members>", (region.getMembers().size() > 0 ? members : "Geen.")));

        if (region.getFlag(PlotModule.PLOT_DESCRIPTION) != null) {
            String description = region.getFlag(PlotModule.PLOT_DESCRIPTION);
            if (description != null && !description.isEmpty())
                ChatUtils.sendFormattedMessage(minetopiaPlayer, MessageConfiguration.message("plot_info_description")
                        .replace("<description>", description));
        }

        ChatUtils.sendFormattedMessage(minetopiaPlayer, MessageConfiguration.message("plot_info_footer"));
    }
}
