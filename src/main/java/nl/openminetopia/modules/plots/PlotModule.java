package nl.openminetopia.modules.plots;

import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.StringFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.jazzkuh.modulemanager.spigot.SpigotModule;
import com.jazzkuh.modulemanager.spigot.SpigotModuleManager;
import nl.openminetopia.OpenMinetopia;
import org.jetbrains.annotations.NotNull;
import nl.openminetopia.modules.plots.commands.PlotCommand;
import nl.openminetopia.modules.plots.commands.subcommands.*;
import nl.openminetopia.utils.WorldGuardUtils;

public class PlotModule extends SpigotModule<@NotNull OpenMinetopia> {

    public PlotModule(SpigotModuleManager<@NotNull OpenMinetopia> moduleManager) {
        super(moduleManager);
    }

    @Override
    public void onEnable() {
        registerComponent(new PlotInfoCommand());

        registerComponent(new PlotCommand());
        registerComponent(new PlotMembersCommand());
        registerComponent(new PlotOwnersCommand());
        registerComponent(new PlotClearCommand());
        registerComponent(new PlotCreateCommand());
        registerComponent(new PlotDeleteCommand());
        registerComponent(new PlotDescriptionCommand());
        registerComponent(new PlotListCommand());
        registerComponent(new PlotTeleportCommand());
        registerComponent(new PlotTransferCommand());

        OpenMinetopia.getCommandManager().getCommandCompletions().registerCompletion("plotName", context ->
                WorldGuardUtils.getProtectedRegions(priority -> priority >= 0).stream()
                        .filter(region -> region.getFlag(PLOT_FLAG) != null)
                        .map(ProtectedRegion::getId).toList());
    }



    @Override
    public void onLoad() {
        loadFlags();
    }

    public static StateFlag PLOT_FLAG = new StateFlag("openmt-plot", true);
    public static StringFlag PLOT_DESCRIPTION = new StringFlag("openmt-description");
    public static StateFlag PLOT_TRANSFER = new StateFlag("openmt-transfer", true);

    public void loadFlags() {
        FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();
        try {
            registry.register(PLOT_FLAG);
            registry.register(PLOT_DESCRIPTION);
            registry.register(PLOT_TRANSFER);
        } catch (FlagConflictException e) {
            PLOT_FLAG = (StateFlag) registry.get("openmt-plot");
            PLOT_DESCRIPTION = (StringFlag) registry.get("openmt-description");
            PLOT_TRANSFER = (StateFlag) registry.get("openmt-transfer");
        }
    }
}
