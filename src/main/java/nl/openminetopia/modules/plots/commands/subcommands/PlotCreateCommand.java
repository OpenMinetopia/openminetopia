package nl.openminetopia.modules.plots.commands.subcommands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.BukkitPlayer;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.api.player.PlayerManager;
import nl.openminetopia.api.player.objects.MinetopiaPlayer;
import nl.openminetopia.api.plots.events.PlotCreateEvent;
import nl.openminetopia.configuration.MessageConfiguration;
import nl.openminetopia.modules.plots.PlotModule;
import nl.openminetopia.utils.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

@CommandAlias("plot|p")
public class PlotCreateCommand extends BaseCommand {

    @Subcommand("create")
    @CommandPermission("openminetopia.plot.create")
    @Syntax("<naam> [topToDown]")
    @Description("Maak een plot aan.")
    public void plotCreate(Player player, String name, @Optional Boolean topToDown) {
        BukkitPlayer bukkitPlayer = BukkitAdapter.adapt(player);
        World bukkitWorld = player.getWorld();

        MinetopiaPlayer minetopiaPlayer = PlayerManager.getInstance().getOnlineMinetopiaPlayer(player);
        if (minetopiaPlayer == null) return;

        boolean doTopToDown = topToDown == null || topToDown;

        try {
            Region region = WorldEdit.getInstance().getSessionManager().get(bukkitPlayer).getSelection(bukkitPlayer.getWorld());
            BlockVector3 max = region.getMaximumPoint();
            BlockVector3 min = region.getMinimumPoint();

            if (doTopToDown) {
                max = region.getMaximumPoint().withY(bukkitWorld.getMaxHeight());
                min = region.getMinimumPoint().withY(bukkitWorld.getMinHeight());
            }

            ProtectedRegion wgRegion = new ProtectedCuboidRegion(name, min, max);
            wgRegion.setFlag(PlotModule.PLOT_FLAG, StateFlag.State.ALLOW);

            RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
            RegionManager manager = container.get(region.getWorld());

            if (manager == null) {
                player.sendMessage(MessageConfiguration.component("plot_creation_error"));
                return;
            }

            manager.addRegion(wgRegion);

            for (String command : OpenMinetopia.getDefaultConfiguration().getPlotCommandsOnCreate()) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command
                        .replace("<world>", bukkitWorld.getName())
                        .replace("<plot>", name)
                );
            }

            PlotCreateEvent event = new PlotCreateEvent(player, wgRegion);
            Bukkit.getPluginManager().callEvent(event);

            ChatUtils.sendFormattedMessage(minetopiaPlayer, MessageConfiguration.message("plot_creation_success")
                    .replace("<plot_id>", name)
            );
        } catch (IncompleteRegionException e) {
            ChatUtils.sendFormattedMessage(minetopiaPlayer, MessageConfiguration.message("plot_no_selection"));
        }
    }
}
