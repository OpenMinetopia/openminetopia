package nl.openminetopia.modules.plots.commands.subcommands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import nl.openminetopia.api.player.PlayerManager;
import nl.openminetopia.api.player.objects.MinetopiaPlayer;
import nl.openminetopia.utils.ChatUtils;
import org.bukkit.Location;
import org.bukkit.entity.Player;

@CommandAlias("plot|p")
public class PlotTeleportCommand extends BaseCommand {

    @Subcommand("tp|teleport")
    @CommandPermission("openminetopia.plot.tp")
    @Syntax("<naam>")
    @CommandCompletion("@plotName")
    @Description("Teleporteer naar een plot.")
    public void tpCommand(Player player, String name) {
        World world = BukkitAdapter.adapt(player.getWorld());

        RegionContainer regionContainer = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionManager regionManager = regionContainer.get(world);

        MinetopiaPlayer minetopiaPlayer = PlayerManager.getInstance().getOnlineMinetopiaPlayer(player);
        if (minetopiaPlayer == null) return;

        if (regionManager == null) {
            player.sendMessage(ChatUtils.format(minetopiaPlayer, "<red>Kon geen regio's ophalen voor deze wereld."));
            return;
        }

        ProtectedRegion region = regionManager.getRegion(name);

        if (region == null) {
            player.sendMessage(ChatUtils.format(minetopiaPlayer, "<red>Geen region gevonden met de naam " + name + "."));
            return;
        }

        BlockVector3 center = region.getMaximumPoint().subtract(region.getMinimumPoint()).divide(2).add(region.getMinimumPoint());
        Location location = new Location(player.getWorld(), center.x(), center.y(), center.z());
        player.teleport(location);
        player.sendMessage(ChatUtils.format(minetopiaPlayer, "<dark_aqua>Je bent naar plot <aqua>" + region.getId() + " <dark_aqua>geteleporteerd."));
    }
}
