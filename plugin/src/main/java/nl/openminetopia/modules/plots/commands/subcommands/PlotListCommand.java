package nl.openminetopia.modules.plots.commands.subcommands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import co.aikar.commands.annotation.Optional;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import nl.openminetopia.api.player.PlayerManager;
import nl.openminetopia.api.player.objects.MinetopiaPlayer;
import nl.openminetopia.modules.plots.PlotModule;
import nl.openminetopia.utils.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.*;
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

    @Subcommand("plist")
    @CommandPermission("openminetopia.plot.plist")
    @CommandCompletion("@players")
    public void playerListCommand(Player player, OfflinePlayer offlinePlayer, @Optional Integer page) {
        World world = BukkitAdapter.adapt(player.getWorld());

        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionManager manager = container.get(world);
        MinetopiaPlayer minetopiaPlayer = PlayerManager.getInstance().getOnlineMinetopiaPlayer(player);
        if (minetopiaPlayer == null) return;

        if (offlinePlayer == null) {
            player.sendMessage(ChatUtils.format(minetopiaPlayer, "<red>Deze speler is niet gevonden."));
            return;
        }

        if (manager == null) {
            player.sendMessage(ChatUtils.format(minetopiaPlayer, "<red>Er ging iets mis met het ophalen van de regio's."));
            return;
        }

        Map<String, String> regionNames = new HashMap<>();
        manager.getRegions().entrySet().stream()
                .filter(entry -> entry.getValue().getFlag(PlotModule.PLOT_FLAG) != null)
                .filter(entry -> entry.getValue().getOwners().contains(offlinePlayer.getUniqueId()) || entry.getValue().getMembers().contains(offlinePlayer.getUniqueId()))
                .forEach(entry -> regionNames.put(entry.getKey(), entry.getValue().getOwners().contains(offlinePlayer.getUniqueId()) ? "Owner" : "Member"));

        int pageSize = 15;
        int totalPages = (int) Math.ceil(regionNames.size() / (double) pageSize);
        int finalPage = page == null || page < 1 || page > totalPages ? 1 : page;

        List<Map.Entry<String, String>> entries = new ArrayList<>(regionNames.entrySet());
        int startIndex = (finalPage - 1) * pageSize;
        int endIndex = Math.min(startIndex + pageSize, entries.size());

        for (int i = startIndex; i < endIndex; i++) {
            Map.Entry<String, String> entry = entries.get(i);
            player.sendMessage(ChatUtils.format(minetopiaPlayer, "<dark_aqua> - <aqua>" + entry.getKey() + " <gray>(" + entry.getValue() + ")"));
        }

        player.sendMessage(ChatUtils.format(minetopiaPlayer, "<dark_aqua>Pagina <aqua>" + finalPage + "<dark_aqua>/<aqua>" + totalPages + "<dark_aqua>. Totaal: <aqua>" + regionNames.size() + " <dark_aqua>regio's."));
    }
}