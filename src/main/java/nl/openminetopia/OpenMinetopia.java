package nl.openminetopia;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.MessageType;
import co.aikar.commands.PaperCommandManager;
import com.jazzkuh.modulemanager.spigot.SpigotModuleManager;
import com.jeff_media.customblockdata.CustomBlockData;
import io.vertx.core.Vertx;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import nl.openminetopia.configuration.DefaultConfiguration;
import nl.openminetopia.configuration.MessageConfiguration;
import nl.openminetopia.registry.CommandComponentRegistry;
import nl.openminetopia.utils.input.ChatInputHandler;
import nl.openminetopia.utils.language.I18n;
import nl.openminetopia.utils.placeholderapi.OpenMinetopiaExpansion;
import nl.openminetopia.utils.wrappers.listeners.CitzensNpcClickListener;
import nl.openminetopia.utils.wrappers.listeners.FancyNpcClickListener;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

@Getter @Setter
public final class OpenMinetopia extends JavaPlugin {

    @Getter
    private static OpenMinetopia instance;

    @Getter @Setter(AccessLevel.PRIVATE)
    private static SpigotModuleManager<@NotNull OpenMinetopia> moduleManager;

    @Getter @Setter
    private static PaperCommandManager commandManager;

    @Getter @Setter
    private static DefaultConfiguration defaultConfiguration;

    @Getter
    private I18n i18n;

    @Getter @Setter
    private static MessageConfiguration messageConfiguration;

    @Getter
    private static ChatInputHandler chatInputHandler;

    private boolean npcSupport = false;

    private boolean labymodSupport = false;

    private Vertx vertx;

    public OpenMinetopia() {
        instance = this;
        moduleManager = new SpigotModuleManager<>(this, getComponentLogger());
    }

    @Override
    public void onEnable() {
        commandManager = new PaperCommandManager(this);
        moduleManager.setDebug(false);

        defaultConfiguration = new DefaultConfiguration(getDataFolder());
        defaultConfiguration.saveConfiguration();

        Locale defaultLocale = Locale.forLanguageTag(getConfig().getString("default-locale", "en-US"));
        i18n = new I18n(this, defaultLocale);

        messageConfiguration = new MessageConfiguration(getDataFolder());
        messageConfiguration.saveConfiguration();

        if (defaultConfiguration.isMetricsEnabled()) {
            Metrics metrics = new Metrics(this, 23547);
            metrics.addCustomChart(new SimplePie("storage", () -> defaultConfiguration.getDatabaseType().toString()));
        }

        commandManager.enableUnstableAPI("help");
        commandManager.setFormat(MessageType.HELP, 1, ChatColor.GOLD);
        commandManager.setFormat(MessageType.HELP, 2, ChatColor.YELLOW);
        commandManager.setFormat(MessageType.HELP, 3, ChatColor.GRAY);

        chatInputHandler = new ChatInputHandler();
        Bukkit.getPluginManager().registerEvents(chatInputHandler, this);

        CustomBlockData.registerListener(this);

        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new OpenMinetopiaExpansion().register();
            getLogger().info("Registered PlaceholderAPI expansion.");
        }

        // Registering of NPC wrapper listeners
        if (Bukkit.getPluginManager().isPluginEnabled("Citizens")) {
            getLogger().info("Initializing Citizens support.");
            Bukkit.getPluginManager().registerEvents(new CitzensNpcClickListener(), this);
            npcSupport = true;
        }

        if (Bukkit.getPluginManager().isPluginEnabled("FancyNpcs")) {
            getLogger().info("Initializing FancyNpcs support.");
            Bukkit.getPluginManager().registerEvents(new FancyNpcClickListener(), this);
            npcSupport = true;
        }

        if (Bukkit.getPluginManager().isPluginEnabled("LabyModServerAPI")) {
            getLogger().info("Initializing LabyModServerAPI support.");
            labymodSupport = true;
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