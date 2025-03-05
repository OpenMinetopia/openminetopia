package nl.openminetopia.modules.plots.commands.subcommands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.Subcommand;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.api.player.PlayerManager;
import nl.openminetopia.api.player.objects.MinetopiaPlayer;
import nl.openminetopia.modules.plots.PlotModule;
import nl.openminetopia.utils.ChatUtils;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@CommandAlias("plot|p")
public class PlotListCommand extends BaseCommand {

    @Subcommand("list")
    @CommandPermission("openminetopia.plot.list")
    public void listCommand(Player player, @Optional Integer page) {
        World world = BukkitAdapter.adapt(player.getWorld());

        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionManager manager = container.get(world);
        MinetopiaPlayer minetopiaPlayer = PlayerManager.getInstance().getOnlineMinetopiaPlayer(player);
        if (minetopiaPlayer == null) return;

        if (manager == null) {
            player.sendMessage(ChatUtils.format(minetopiaPlayer, "<red>Er ging iets mis met het ophalen van de regio's."));
            return;
        }

        // Create a new map with only regions that have the PLOT_FLAG set
        Map<String, ProtectedRegion> filteredRegions = manager.getRegions().entrySet().stream()
                .filter(entry -> entry.getValue().getFlag(PlotModule.PLOT_FLAG) != null)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        Collection<String> regionNames = filteredRegions.keySet();


        int pageSize = 15;
        int totalPages = (int) Math.ceil(regionNames.size() / (double) pageSize);

        int finalPage = page == null || page < 1 || page > totalPages ? 1 : page;

        int startIndex = (finalPage - 1) * pageSize;
        int endIndex = Math.min(startIndex + pageSize, regionNames.size());

        List<String> regionList = new ArrayList<>(regionNames);
        for (int i = startIndex; i < endIndex; i++) {
            player.sendMessage(ChatUtils.format(minetopiaPlayer, "<dark_aqua> - <aqua>" + regionList.get(i)));
        }

        player.sendMessage(ChatUtils.format(minetopiaPlayer, "<dark_aqua>Pagina <aqua>" + finalPage + "<dark_aqua>/<aqua>" + totalPages + "<dark_aqua>. Totaal: <aqua>" + regionNames.size() + " <dark_aqua>regio's."));
    }
}