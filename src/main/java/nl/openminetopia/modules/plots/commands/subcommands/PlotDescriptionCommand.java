package nl.openminetopia.modules.plots.commands.subcommands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import nl.openminetopia.api.player.PlayerManager;
import nl.openminetopia.api.player.objects.MinetopiaPlayer;
import nl.openminetopia.configuration.MessageConfiguration;
import nl.openminetopia.modules.plots.PlotModule;
import nl.openminetopia.modules.plots.utils.PlotUtil;
import nl.openminetopia.utils.ChatUtils;
import nl.openminetopia.utils.WorldGuardUtils;
import org.bukkit.entity.Player;

@CommandAlias("plot|p")
public class PlotDescriptionCommand extends BaseCommand {

    @Subcommand("description")
    @CommandPermission("openminetopia.plot.description")
    @Syntax("<beschrijving> <region>")
    @CommandCompletion("@plotName")
    @Description("Zet een beschrijving van een plot.")
    public void plotDescription(Player player, String description, @Optional String regionName) {
        ProtectedRegion region = PlotUtil.getPlot(player.getLocation());
        if (regionName != null) region = PlotUtil.getPlot(player.getWorld(), regionName);

        MinetopiaPlayer minetopiaPlayer = PlayerManager.getInstance().getOnlineMinetopiaPlayer(player);
        if (minetopiaPlayer == null) return;

        if (region == null) {
            player.sendMessage(MessageConfiguration.component("plot_invalid_location"));
            return;
        }

        if (region.getFlag(PlotModule.PLOT_FLAG) == null) {
            player.sendMessage(MessageConfiguration.component("plot_not_valid"));
            return;
        }

        if (description.isBlank() || description.equalsIgnoreCase("remove") ||
                description.equalsIgnoreCase("delete") || description.equalsIgnoreCase("null")) {
            region.setFlag(PlotModule.PLOT_DESCRIPTION, null);
            ChatUtils.sendMessage(player, MessageConfiguration.message("plot_description_removed")
                    .replace("<plot>", region.getId()));
            return;
        }

        region.setFlag(PlotModule.PLOT_DESCRIPTION, description);
        ChatUtils.sendMessage(player, MessageConfiguration.message("plot_description_updated")
                .replace("<description>", description)
                .replace("<plot>", region.getId()));
    }
}
