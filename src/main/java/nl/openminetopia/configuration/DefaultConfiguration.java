package nl.openminetopia.configuration;

import lombok.Getter;
import lombok.SneakyThrows;
import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.modules.data.types.DatabaseType;
import nl.openminetopia.utils.ConfigurateConfig;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.spongepowered.configurate.ConfigurationNode;

import java.io.File;
import java.util.*;

@Getter
public class DefaultConfiguration extends ConfigurateConfig {

    /**
     * Metrics configuration
     */
    private final boolean metricsEnabled;

    /**
     * Database configuration
     */
    private final DatabaseType databaseType;
    private final String host;
    private final int port;
    private final String databaseName;
    private final String username;
    private final String password;

    /**
     * Chat configuration
     */
    private final String chatFormat;
    private final boolean chatEnabled;
    private final boolean chatRadiusEnabled;
    private final int chatRadiusRange;
    private final boolean notifyWhenNobodyInRange;

    /**
     * Scoreboard configuration
     */
    private final boolean scoreboardEnabled;
    private final List<String> scoreboardLines;

    /**
     * Default settings configuration
     */
    private final String defaultPrefix;
    private final String defaultPrefixColor;
    private final int defaultLevel;
    private final String defaultLevelColor;

    private final String defaultNameColor;
    private final String defaultChatColor;

    /**
     * Teleporter configuration
     */
    private final List<String> displayLines;

    /**
     * Detection Gate configuration
     */
    private final boolean detectionGateEnabled;
    private final int detectionBlocksReplacementRange;
    private final int detectionCooldown;
    private final Material detectionPressurePlate;
    private final Material detectionActivationBlock;
    private final List<ItemStack> detectionMaterials;
    private final Map<Material, Material> detectionSafeBlocks;
    private final Map<Material, Material> detectionFlaggedBlocks;

    /**
     * Plot configuration
     */
    private final List<String> commandsOnPlotCreate;

    /**
     * Emergency command configuration
     */
    private final int emergencyCooldown;

    /**
     * Balaclava configuration
     */
    private final List<String> balaclavaItems;

    /**
     * Handcuff configuration
     */
    private final List<String> handcuffItems;
    private final List<String> handcuffEffects;
    private final boolean handcuffCanDropItems;
    private final boolean handcuffCanPickupItems;
    private final boolean handcuffCanOpenInventory;
    private final boolean handcuffCanRunAway;
    private final boolean handcuffCanPvP;
    private final boolean handcuffCanChangeSlots;
    private final boolean handcuffShowTitle;

    /**
     * Head configuration
     */
    private final List<String> headWhitelist;

    /**
     * Bodysearch configuration
     */
    private final int bodysearchRange;

    @SneakyThrows
    public DefaultConfiguration(File file) {
        super(file, "config.yml", "");
        /*
         * Metrics configuration
         */
        this.metricsEnabled = rootNode.node("metrics", "enabled").getBoolean(true);

        /*
         * Database configuration
         */
        this.databaseType = DatabaseType.valueOf(rootNode.node("database", "type").getString("sqlite").toUpperCase());
        this.host = rootNode.node("database", "host").getString("localhost");
        this.port = rootNode.node("database", "port").getInt(3306);
        this.databaseName = rootNode.node("database", "name").getString("openminetopia");
        this.username = rootNode.node("database", "username").getString("root");
        this.password = rootNode.node("database", "password").getString("password");


        /*
         * Default settings configuration
         */
        this.defaultPrefix = rootNode.node("default", "prefix").getString("Zwerver");
        this.defaultPrefixColor = rootNode.node("default", "prefixColor").getString("<gray>");
        this.defaultLevel = rootNode.node("default", "level").getInt(1);
        this.defaultLevelColor = rootNode.node("default", "levelColor").getString("<gray>");

        this.defaultNameColor = rootNode.node("default", "nameColor").getString("<white>");
        this.defaultChatColor = rootNode.node("default", "chatColor").getString("<white>");

        /*
         * Chat configuration
         */
        this.chatFormat = rootNode.node("chat", "format").getString("<dark_gray>[<level_color>Level <level><reset><dark_gray>] <dark_gray>[<prefix_color><prefix><reset><dark_gray>] <name_color><player><reset>: <chat_color><message>");
        this.chatEnabled = rootNode.node("chat", "enabled").getBoolean(true);
        this.chatRadiusEnabled = rootNode.node("chat", "radius", "enabled").getBoolean(true);
        this.chatRadiusRange = rootNode.node("chat", "radius", "range").getInt(20);
        this.notifyWhenNobodyInRange = rootNode.node("chat", "radius", "notifyWhenNobodyInRange").getBoolean(false);

        /*
         * Scoreboard configuration
         */
        this.scoreboardEnabled = rootNode.node("scoreboard", "enabled").getBoolean(true);
        this.scoreboardLines = rootNode.node("scoreboard", "lines").getList(String.class, List.of(
                "<world_title>",
                "<world_color>Temperatuur:",
                "<temperature>°C",
                " ",
                "<world_color>Level:",
                "<level> -> <calculated_level> (<levelups><white>)",
                " ",
                "<world_color>Fitheid:",
                "<fitness>/<max_fitness>"
        ));

        /*
         * Teleporter configuration
         */
        this.displayLines = rootNode.node("teleporter", "lines").getList(String.class, List.of(
                "<gold>Teleporter",
                "<grey><x>;<y>;<z>;<world>"
        ));

        /*
         * Detection Gate configuration
         */
        this.detectionGateEnabled = rootNode.node("detection-gate", "enabled").getBoolean(true);
        this.detectionBlocksReplacementRange = rootNode.node("detection-gate", "blocks", "replacement-range").getInt(5);
        this.detectionCooldown = rootNode.node("detection-gate", "cooldown").getInt(3);
        this.detectionPressurePlate = Material.matchMaterial(rootNode.node("detection-gate", "blocks", "pressure-plate-type").getString(Material.LIGHT_WEIGHTED_PRESSURE_PLATE.toString()));
        this.detectionActivationBlock = Material.matchMaterial(rootNode.node("detection-gate", "blocks", "activation-block").getString(Material.IRON_BLOCK.toString()));
        this.detectionMaterials = new ArrayList<>();
        rootNode.node("detection-gate", "flagged-materials").getList(String.class, List.of(
                Material.SUGAR.toString(),
                Material.IRON_HOE.toString(),
                Material.STICK.toString(),
                Material.WOODEN_SWORD.toString(),
                Material.SPIDER_EYE.toString(),
                Material.FERMENTED_SPIDER_EYE.toString(),
                Material.SNOWBALL.toString(),
                Material.ARROW.toString(),
                Material.BOW.toString(),
                Material.ROTTEN_FLESH.toString(),
                Material.STONE_HOE.toString(),
                Material.POISONOUS_POTATO.toString()
        )).forEach(materialString -> {
            String[] parts = materialString.split(":");

            Material material = Material.matchMaterial(parts[0]);
            int customModelData = materialString.split(":").length > 1 ? Integer.parseInt(parts[1]) : 0;
            if (material != null) {
                ItemStack itemStack = new ItemStack(material);
                if (customModelData != 0) {
                    ItemMeta itemMeta = itemStack.getItemMeta();
                    if (itemMeta != null) {
                        itemMeta.setCustomModelData(customModelData);
                        itemStack.setItemMeta(itemMeta);
                    }
                }
                this.detectionMaterials.add(itemStack);
            }
        });

        ConfigurationNode safeBlocksNode = rootNode.node("detection-gate", "flag-blocks", "safe");
        if (safeBlocksNode.isNull()) {
            Map<String, String> safeBlocks = new HashMap<>();
            safeBlocks.put(Material.BLACK_WOOL.toString(), Material.LIME_WOOL.toString());
            safeBlocks.put(Material.BLACK_CONCRETE.toString(), Material.LIME_CONCRETE.toString());
            safeBlocks.put(Material.BLACK_TERRACOTTA.toString(), Material.LIME_TERRACOTTA.toString());
            safeBlocks.put(Material.BLACK_STAINED_GLASS.toString(), Material.LIME_STAINED_GLASS.toString());
            OpenMinetopia.getInstance().getLogger().info("loading new blocks.");
            safeBlocks.forEach((key, value) -> {
                safeBlocksNode.node(key).getString(value);
            });
        }

        this.detectionSafeBlocks = new HashMap<>();
        safeBlocksNode.childrenMap().forEach((key, val) -> {
            Material keyMaterial = Material.matchMaterial(key.toString());
            Material valueMaterial = Material.matchMaterial(val.getString());
            if (keyMaterial != null && valueMaterial != null) {
                this.detectionSafeBlocks.put(keyMaterial, valueMaterial);
            }
        });

        ConfigurationNode flaggedBlocksNode = rootNode.node("detection-gate", "flag-blocks", "flagged");
        if (flaggedBlocksNode.isNull()) {
            Map<String, String> flaggedBlocks = new HashMap<>();
            flaggedBlocks.put(Material.BLACK_WOOL.toString(), Material.RED_WOOL.toString());
            flaggedBlocks.put(Material.BLACK_CONCRETE.toString(), Material.RED_CONCRETE.toString());
            flaggedBlocks.put(Material.BLACK_TERRACOTTA.toString(), Material.RED_TERRACOTTA.toString());
            flaggedBlocks.put(Material.BLACK_STAINED_GLASS.toString(), Material.RED_STAINED_GLASS.toString());
            flaggedBlocks.forEach((key, value) -> {
                flaggedBlocksNode.node(key).getString(value);
            });
        }

        this.detectionFlaggedBlocks = new HashMap<>();
        flaggedBlocksNode.childrenMap().forEach((key, val) -> {
            Material keyMaterial = Material.matchMaterial(key.toString());
            Material valueMaterial = Material.matchMaterial(val.getString());
            if (keyMaterial != null && valueMaterial != null) {
                this.detectionFlaggedBlocks.put(keyMaterial, valueMaterial);
            }
        });

        /*
         * Plot configuration
         */
        this.commandsOnPlotCreate = rootNode.node("plot", "commandsOnCreate").getList(String.class, List.of(
                "rg flag <plot> -w <world> interact -g NON_MEMBERS DENY",
                "rg flag <plot> -w <world> chest-access -g NON_MEMBERS DENY",
                "rg flag <plot> -w <world> USE -g MEMBERS ALLOW",
                "rg flag <plot> -w <world> INTERACT -g MEMBERS ALLOW",
                "rg flag <plot> -w <world> PVP ALLOW"
        ));

        /*
         * Emergency command configuration
         */
        this.emergencyCooldown = rootNode.node("emergency", "cooldown").getInt(300);

        /*
         * Balaclava configuration
         */
        this.balaclavaItems = rootNode.node("bivak", "items").getList(String.class, List.of(
                "CLAY_BALL")
        );

        this.handcuffItems = rootNode.node("handcuffs", "items").getList(String.class, List.of(
                "GRAY_DYE")
        );
        this.handcuffEffects = rootNode.node("handcuffs", "effects").getList(String.class, List.of(
                "BLINDNESS:2",
                "MINING_FATIGUE:1",
                "SLOWNESS:4"
        ));
        this.handcuffCanDropItems = rootNode.node("handcuffs", "canDropItems").getBoolean(false);
        this.handcuffCanPickupItems = rootNode.node("handcuffs", "canPickupItems").getBoolean(false);
        this.handcuffCanOpenInventory = rootNode.node("handcuffs", "canOpenInventory").getBoolean(false);
        this.handcuffCanRunAway = rootNode.node("handcuffs", "canRunAway").getBoolean(false);
        this.handcuffCanPvP = rootNode.node("handcuffs", "canPvP").getBoolean(false);
        this.handcuffCanChangeSlots = rootNode.node("handcuffs", "canChangeSlots").getBoolean(false);
        this.handcuffShowTitle = rootNode.node("handcuffs", "showTitle").getBoolean(true);

        /*
         * Head configuration
         */
        this.headWhitelist = rootNode.node("head", "whitelist").getList(String.class, List.of(
                "CLAY_BALL",
                "BEDROCK",
                "SPONGE",
                "IRON_ORE",
                "COAL_ORE",
                "LAPIS_ORE",
                "DIAMOND_ORE",
                "REDSTONE_ORE",
                "SOUL_SAND",
                "NETHERRACK",
                "NETHER_BRICK",
                "END_STONE",
                "NETHER_QUARTZ_ORE",
                "EMERALD_ORE",
                "PRISMARINE",
                "RED_SANDSTONE",
                "INK_SAC",
                "MAGMA_CREAM",
                "NETHER_WART",
                "PRISMARINE_SHARD",
                "PRISMARINE_CRYSTALS",
                "CARROT_ON_A_STICK",
                "SHEARS",
                "GLASS",
                "STAINED_GLASS",
                "DIAMOND_HOE:89",
                "GREEN_DYE"
        ));

        /*
         * Bodysearch configuration
         */
        this.bodysearchRange = rootNode.node("bodysearch", "range").getInt(10);
    }

    @SneakyThrows
    public void addToHeadWhitelist(String item) {
        this.headWhitelist.add(item);
        rootNode.node("head", "whitelist").set(headWhitelist);
        saveConfiguration();
    }

    @SneakyThrows
    public void removeFromHeadWhitelist(String item) {
        this.headWhitelist.remove(item);
        rootNode.node("head", "whitelist").set(headWhitelist);
        saveConfiguration();
    }
}