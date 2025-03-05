package nl.openminetopia;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.MessageType;
import co.aikar.commands.PaperCommandManager;
import com.jazzkuh.inventorylib.loader.InventoryLoader;
import com.jazzkuh.inventorylib.objects.Menu;
import com.jazzkuh.modulemanager.spigot.SpigotModuleManager;
import com.jeff_media.customblockdata.CustomBlockData;
import io.vertx.core.Vertx;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import nl.openminetopia.configuration.*;
import nl.openminetopia.registry.CommandComponentRegistry;
import nl.openminetopia.utils.ChatUtils;
import nl.openminetopia.utils.placeholderapi.OpenMinetopiaExpansion;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public final class OpenMinetopia extends JavaPlugin {

    @Getter
    private static OpenMinetopia instance;

    @Getter
    @Setter(AccessLevel.PRIVATE)
    private static SpigotModuleManager<@NotNull OpenMinetopia> moduleManager;

    @Getter
    @Setter
    private static PaperCommandManager commandManager;

    @Getter
    @Setter
    private static DefaultConfiguration defaultConfiguration;

    @Getter
    @Setter
    private static MessageConfiguration messageConfiguration;

    @Getter
    @Setter
    private static LevelCheckConfiguration levelcheckConfiguration;

    @Getter
    @Setter
    private static ColorsConfiguration colorsConfiguration;

    @Getter
    @Setter
    private static BankingConfiguration bankingConfiguration;

    @Getter
    @Setter
    private static FitnessConfiguration fitnessConfiguration;

    private Vertx vertx;

    public OpenMinetopia() {
        instance = this;
        moduleManager = new SpigotModuleManager<>(this, getComponentLogger());
    }

    @Override
    public void onEnable() {
        commandManager = new PaperCommandManager(this);
        moduleManager.debug(false);

        defaultConfiguration = new DefaultConfiguration(getDataFolder());
        defaultConfiguration.saveConfiguration();

        messageConfiguration = new MessageConfiguration(getDataFolder());
        messageConfiguration.saveConfiguration();

        if (defaultConfiguration.isMetricsEnabled()) {
            Metrics metrics = new Metrics(this, 23547);
            metrics.addCustomChart(new SimplePie("storage", () -> defaultConfiguration.getDatabaseType().toString()));
        }

        levelcheckConfiguration = new LevelCheckConfiguration(getDataFolder());
        levelcheckConfiguration.saveConfiguration();

        colorsConfiguration = new ColorsConfiguration(getDataFolder());
        colorsConfiguration.saveConfiguration();

        bankingConfiguration = new BankingConfiguration(getDataFolder());
        bankingConfiguration.saveConfiguration();

        fitnessConfiguration = new FitnessConfiguration(getDataFolder());
        fitnessConfiguration.saveConfiguration();

        commandManager.enableUnstableAPI("help");
        commandManager.setFormat(MessageType.HELP, 1, ChatColor.GOLD);
        commandManager.setFormat(MessageType.HELP, 2, ChatColor.YELLOW);
        commandManager.setFormat(MessageType.HELP, 3, ChatColor.GRAY);

        CustomBlockData.registerListener(this);
        Menu.init(this);
        InventoryLoader.setFormattingProvider(message -> ChatUtils.color("<red>" + message));

        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new OpenMinetopiaExpansion().register();
            getLogger().info("Registered PlaceholderAPI expansion.");
        }

        moduleManager.getComponentRegistry().registerComponentHandler(
                BaseCommand.class,
                new CommandComponentRegistry(commandManager)
        );
        moduleManager.enable();
    }

    @Override
    public void onDisable() {
        moduleManager.disable();
    }

    @Override
    public void onLoad() {
        moduleManager.scanModules(this.getClass());
        moduleManager.load();
    }

    public Vertx getOrCreateVertx() {
        if (vertx == null) {
            vertx = Vertx.vertx();
        }
        return vertx;
    }
}