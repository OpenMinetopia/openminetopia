package nl.openminetopia.modules.plots.commands.subcommands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.api.player.PlayerManager;
import nl.openminetopia.api.player.objects.MinetopiaPlayer;
import nl.openminetopia.configuration.MessageConfiguration;
import nl.openminetopia.modules.plots.PlotModule;
import nl.openminetopia.utils.ChatUtils;
import nl.openminetopia.utils.WorldGuardUtils;
import org.bukkit.entity.Player;

@CommandAlias("plot|p")
public class PlotTransferCommand extends BaseCommand {

    @Subcommand("transfer")
    @CommandPermission("openminetopia.plot.tranfer")
    @Description("Zet de mogelijkheid om een plot over te dragen uit (portal related).")
    public void plotTransfer(Player player, Boolean transferable) {
        ProtectedRegion region = WorldGuardUtils.getProtectedRegion(player.getLocation(), priority -> priority >= 0);
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

        if (!transferable) {
            region.setFlag(PlotModule.PLOT_TRANSFER, StateFlag.State.DENY);
            ChatUtils.sendMessage(player, MessageConfiguration.message("plot_set_untranferable")
                    .replace("<plot_id>", region.getId()));
            return;
        }

        region.setFlag(PlotModule.PLOT_TRANSFER, StateFlag.State.ALLOW);
        ChatUtils.sendMessage(player, MessageConfiguration.message("plot_set_tranferable")
                .replace("<plot_id>", region.getId()));
    }
}
