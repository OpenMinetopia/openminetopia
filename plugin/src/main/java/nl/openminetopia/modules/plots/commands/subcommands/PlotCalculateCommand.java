package nl.openminetopia.modules.plots.commands.subcommands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.BukkitPlayer;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.api.player.PlayerManager;
import nl.openminetopia.api.player.objects.MinetopiaPlayer;
import nl.openminetopia.configuration.MessageConfiguration;
import nl.openminetopia.modules.plots.PlotModule;
import nl.openminetopia.modules.plots.configuration.PlotCalculateConfiguration;
import nl.openminetopia.modules.plots.utils.PlotUtil;
import nl.openminetopia.utils.ChatUtils;
import nl.openminetopia.utils.WorldGuardUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

@CommandAlias("plot|p")
public class PlotCalculateCommand extends BaseCommand {

    @Subcommand("calculate")
    @CommandPermission("openminetopia.plot.calculate")
    @Description("Bereken de prijs van een plot.")
    @CommandCompletion("@plotName")
    public void plotCalculate(Player player, @Optional String plotName) {
        ProtectedRegion region = WorldGuardUtils.getProtectedRegions(priority -> priority >= 0).stream()
                        .filter(protectedRegion -> protectedRegion.getId().equals(plotName))
                        .findFirst().orElse(null);

        MinetopiaPlayer minetopiaPlayer = PlayerManager.getInstance().getOnlineMinetopiaPlayer(player);
        if (minetopiaPlayer == null) return;

        if (plotName == null) region = PlotUtil.getPlot(player.getLocation());

        if (region == null) {
            player.sendMessage(MessageConfiguration.component("plot_invalid_location"));
            return;
        }

        Location minLocation;
        Location maxLocation;
        if (region != null) {
            minLocation = new Location(player.getWorld(), region.getMinimumPoint().x(), region.getMinimumPoint().y(), region.getMinimumPoint().z());
            maxLocation = new Location(player.getWorld(), region.getMaximumPoint().x(), region.getMaximumPoint().y(), region.getMaximumPoint().z());
        } else {
            try {
                BukkitPlayer bukkitPlayer = BukkitAdapter.adapt(player);
                Region selection = WorldEdit.getInstance().getSessionManager().get(bukkitPlayer).getSelection(bukkitPlayer.getWorld());
                BlockVector3 min = selection.getMinimumPoint();
                BlockVector3 max = selection.getMaximumPoint();
                minLocation = new Location(player.getWorld(), min.x(), min.y(), min.z());
                maxLocation = new Location(player.getWorld(), max.x(), max.y(), max.z());
            } catch (IncompleteRegionException e) {
                player.sendMessage(MessageConfiguration.component("plot_no_selection"));
                return;
            }
        }

        double buildingPrice = calculateBuildingPrice(minLocation, maxLocation);
        double buildersPrice = calculateBuildersPrice(minLocation, maxLocation);
        double plotPrice = calculatePlotPrice(minLocation, maxLocation);
        double totalPrice = buildingPrice + buildersPrice;

        ChatUtils.sendFormattedMessage(minetopiaPlayer, MessageConfiguration.message("plot_calculate_summary")
                .replace("<total_price>", String.valueOf(totalPrice))
                .replace("<plot_price>", String.valueOf(plotPrice))
                .replace("<building_price>", String.valueOf(buildingPrice))
                .replace("<builders_price>", String.valueOf(buildersPrice)));
    }

    private double calculatePlotPrice(Location firstLocation, Location secondLocation) {
        int minX = (int) (Math.abs(firstLocation.x() - secondLocation.x()) + 1); // Add 1 to include both ends
        int minZ = (int) (Math.abs(firstLocation.z() - secondLocation.z()) + 1); // Add 1 to include both ends
        PlotModule plotModule = OpenMinetopia.getModuleManager().get(PlotModule.class);
        PlotCalculateConfiguration config = plotModule.getCalculateConfiguration();
        String formula = config.getCalculateFormula().replace("<length>", "l").replace("<width>", "w");
        Expression expression = new ExpressionBuilder(formula).variables("l", "w").build().setVariable("l", minX).setVariable("w", minZ);
        return expression.evaluate();
    }

    private double calculateBuildingPrice(Location firstLocation, Location secondLocation) {
        PlotCalculateConfiguration config = OpenMinetopia.getModuleManager().get(PlotModule.class).getCalculateConfiguration();

        double buildingPrice = 0.0;

        for (int x = firstLocation.getBlockX(); x <= secondLocation.getBlockX(); x++) {
            for (int y = firstLocation.getBlockY(); y <= secondLocation.getBlockY(); y++) {
                for (int z = firstLocation.getBlockZ(); z <= secondLocation.getBlockZ(); z++) {
                    Block block = firstLocation.getWorld().getBlockAt(x, y, z);
                    Material material = block.getType();
                    buildingPrice += config.getBlockValues().getOrDefault(material, 0.0);
                }
            }
        }

        return buildingPrice;
    }

    private double calculateBuildersPrice(Location firstLocation, Location secondLocation) {
        PlotCalculateConfiguration config = OpenMinetopia.getModuleManager().get(PlotModule.class).getCalculateConfiguration();

        int validBlocks = 0;

        for (int x = firstLocation.getBlockX(); x <= secondLocation.getBlockX(); x++) {
            for (int y = firstLocation.getBlockY(); y <= secondLocation.getBlockY(); y++) {
                for (int z = firstLocation.getBlockZ(); z <= secondLocation.getBlockZ(); z++) {
                    Block block = firstLocation.getWorld().getBlockAt(x, y, z);
                    Material material = block.getType();

                    if (material != Material.AIR && config.getBlockValues().containsKey(material)) {
                        validBlocks++;
                    }
                }
            }
        }

        return validBlocks * config.getBuildersWage();
    }
}