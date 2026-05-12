package nl.openminetopia.modules.plots.commands.subcommands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import nl.openminetopia.api.player.PlayerManager;
import nl.openminetopia.api.player.objects.MinetopiaPlayer;
import nl.openminetopia.configuration.MessageConfiguration;
import nl.openminetopia.utils.ChatUtils;
import org.bukkit.entity.Player;

@CommandAlias("plot|p")
public class PlotDeleteCommand extends BaseCommand {

    @Subcommand("delete")
    @CommandPermission("openminetopia.plot.delete")
    @Syntax("<naam>")
    @CommandCompletion("@plotName")
    @Description("Verwijder een plot.")
    public void deletePlot(Player player, String name) {
        World world = BukkitAdapter.adapt(player.getWorld());

        RegionContainer regionContainer = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionManager regionManager = regionContainer.get(world);

        MinetopiaPlayer minetopiaPlayer = PlayerManager.getInstance().getOnlineMinetopiaPlayer(player);

        if (minetopiaPlayer == null) return;

        if (regionManager == null) {
            player.sendMessage(MessageConfiguration.component("plot_region_retrieval_error"));
            return;
        }

        ProtectedRegion region = regionManager.getRegion(name);

        if (region == null) {
            player.sendMessage(MessageConfiguration.component("plot_not_found"));
            return;
        }

        ChatUtils.sendMessage(player, MessageConfiguration.message("plot_deletion_success")
                .replace("<plot>", region.getId()));

        regionManager.removeRegion(region.getId());
    }
}
