package nl.openminetopia.configuration;

import lombok.Getter;
import lombok.SneakyThrows;
import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.modules.data.types.DatabaseType;
import nl.openminetopia.utils.ConfigurateConfig;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.spongepowered.configurate.ConfigurationNode;
import nl.openminetopia.utils.item.*;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private final String databaseHost;
    private final int databasePort;
    private final String databaseName;
    private final String databaseUsername;
    private final String databasePassword;

    /**
     * Rest API configuration
     */
    private final boolean restApiEnabled;
    private final int restApiPort;
    private final String restApiKey;

    /**
     * Portal configuration
     */
    private final boolean portalEnabled;
    private final String portalUrl;
    private final String portalToken;

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
    private final List<String> teleporterDisplayLines;

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
    private final List<String> plotCommandsOnCreate;

    /**
     * Emergency command configuration
     */
    private final int emergencyCooldown;

    /**
     * Balaclava configuration
     */
    private final List<ItemStack> balaclavaItems;

    /**
     * Handcuff configuration
     */
    private final List<ItemStack> handcuffItems;
    private final List<PotionEffect> handcuffEffects;
    private final boolean handcuffCanDropItems;
    private final boolean handcuffCanPickupItems;
    private final boolean handcuffCanOpenInventory;
    private final boolean handcuffCanRunAway;
    private final boolean handcuffCanPvP;
    private final boolean handcuffCanChangeSlots;
    private final boolean handcuffShowTitle;

    /**
     * Pepperspray configuration
     */
    private final List<ItemStack> peppersprayItems;
    private final boolean peppersprayUsagesEnabled;
    private final int peppersprayMaxUsages;
    private final int peppersprayEffectsDuration;
    private final List<PotionEffect> peppersprayEffects;

    /**
     * Nightvision goggles configuration
     */
    private final List<ItemStack> nightvisionItems;
    private final List<PotionEffect> nightvisionEffects;


    /**
     * Taser configuration
     */
    private final List<ItemStack> taserItems;
    private final boolean taserUsagesEnabled;
    private final int taserMaxUsages;
    private final int taserCooldown;
    private final boolean taserFreezeEnabled;
    private final int taserFreezeDuration;
    private final int taserEffectsDuration;
    private final List<PotionEffect> taserEffects;

    /**
     * Head configuration
     */
    private final List<ItemStack> headWhitelist;

    /**
     * Bodysearch configuration
     */
    private final int bodysearchRange;

    /**
     * Walkie-talkie configuration
     */
    private final List<ItemStack> walkieTalkieItems;
    private final boolean walkieTalkieEmergencyCooldownEnabled;
    private final int walkieTalkieEmergencyCooldownSeconds;

    /**
     * Trashcan configuration
     */
    private final boolean trashcanEnabled;
    private final List<Material> trashcanBlocks;
    private final boolean trashcanUseDropperInventory;

    /**
     * Time Configuration
     */
    private final boolean syncTime;
    private final String syncTimeZone;

    @SneakyThrows
    public DefaultConfiguration(File file) {
        super(file, "config.yml", "", false);
        /*
         * Metrics configuration
         */
        this.metricsEnabled = rootNode.node("metrics", "enabled").getBoolean(true);

        /*
         * Database configuration
         */
        this.databaseType = DatabaseType.valueOf(rootNode.node("database", "type").getString("sqlite").toUpperCase());
        this.databaseHost = rootNode.node("database", "host").getString("localhost");
        this.databasePort = rootNode.node("database", "port").getInt(3306);
        this.databaseName = rootNode.node("database", "name").getString("openminetopia");
        this.databaseUsername = rootNode.node("database", "username").getString("root");
        this.databasePassword = rootNode.node("database", "password").getString("password");

        /*
         * Rest API configuration
         */
        this.restApiEnabled = rootNode.node("rest-api", "enabled").getBoolean(false);
        this.restApiPort = rootNode.node("rest-api", "port").getInt(4567);
        this.restApiKey = rootNode.node("rest-api", "api-key").getString("CHANGE-ME");

        /*
         * Portal configuration
         */
        this.portalEnabled = rootNode.node("portal", "enabled").getBoolean(false);
        this.portalUrl = rootNode.node("portal", "url").getString("portal.openminetopia.nl");
        this.portalToken = rootNode.node("portal", "token").getString("CHANGE-ME");

        /*
         * Default settings configuration
         */
        this.defaultPrefix = rootNode.node("default", "prefix").getString("Zwerver");
        this.defaultPrefixColor = rootNode.node("default", "prefix-color").getString("<gray>");
        this.defaultLevel = rootNode.node("default", "level").getInt(1);
        this.defaultLevelColor = rootNode.node("default", "level-color").getString("<gray>");

        this.defaultNameColor = rootNode.node("default", "name-color").getString("<white>");
        this.defaultChatColor = rootNode.node("default", "chat-color").getString("<white>");

        /*
         * Chat configuration
         */
        this.chatFormat = rootNode.node("chat", "format").getString("<dark_gray>[<level_color>Level <level><reset><dark_gray>] <dark_gray>[<prefix_color><prefix><reset><dark_gray>] <name_color><player><reset>: <chat_color><message>");
        this.chatEnabled = rootNode.node("chat", "enabled").getBoolean(true);
        this.chatRadiusEnabled = rootNode.node("chat", "radius", "enabled").getBoolean(true);
        this.chatRadiusRange = rootNode.node("chat", "radius", "range").getInt(20);
        this.notifyWhenNobodyInRange = rootNode.node("chat", "radius", "notify-when-nobody-in-range").getBoolean(false);

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
        this.teleporterDisplayLines = rootNode.node("teleporter", "lines").getList(String.class, List.of(
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
        this.detectionMaterials = loadItemMappings(rootNode.node("detection-gate", "flagged-materials"), List.of(
                new ItemStack(Material.SUGAR),
                new ItemStack(Material.IRON_HOE),
                new ItemStack(Material.STICK),
                new ItemStack(Material.WOODEN_SWORD),
                new ItemStack(Material.SPIDER_EYE),
                new ItemStack(Material.FERMENTED_SPIDER_EYE),
                new ItemStack(Material.SNOWBALL),
                new ItemStack(Material.ARROW),
                new ItemStack(Material.BOW),
                new ItemStack(Material.ROTTEN_FLESH),
                new ItemStack(Material.STONE_HOE),
                new ItemStack(Material.POISONOUS_POTATO)
        ));

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
            if (key.toString() == null || val.getString() == null) return;
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
            if (key.toString() == null || val.getString() == null) return;
            Material keyMaterial = Material.matchMaterial(key.toString());
            Material valueMaterial = Material.matchMaterial(val.getString());
            if (keyMaterial != null && valueMaterial != null) {
                this.detectionFlaggedBlocks.put(keyMaterial, valueMaterial);
            }
        });

        /*
         * Plot configuration
         */
        this.plotCommandsOnCreate = rootNode.node("plot", "commands-on-create").getList(String.class, List.of(
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
        this.balaclavaItems = loadItemMappings(rootNode.node("balaclava", "items"), List.of(
                new ItemStack(Material.CLAY_BALL)
        ));

        /*
         * Handcuff configuration
         */
        this.handcuffItems = loadItemMappings(rootNode.node("handcuff", "items"), List.of(
                new ItemStack(Material.GRAY_DYE)
        ));
        this.handcuffEffects = loadEffectMappings(rootNode.node("handcuffs", "effects"), List.of(
                new PotionEffect(PotionEffectType.BLINDNESS, 0, 2),
                new PotionEffect(PotionEffectType.MINING_FATIGUE, 0, 1),
                new PotionEffect(PotionEffectType.SLOWNESS, 0, 4)
        ));
        this.handcuffCanDropItems = rootNode.node("handcuffs", "can-drop-items").getBoolean(false);
        this.handcuffCanPickupItems = rootNode.node("handcuffs", "can-pickup-items").getBoolean(false);
        this.handcuffCanOpenInventory = rootNode.node("handcuffs", "can-open-inventory").getBoolean(false);
        this.handcuffCanRunAway = rootNode.node("handcuffs", "can-run-away").getBoolean(false);
        this.handcuffCanPvP = rootNode.node("handcuffs", "can-pvp").getBoolean(false);
        this.handcuffCanChangeSlots = rootNode.node("handcuffs", "can-change-slots").getBoolean(false);
        this.handcuffShowTitle = rootNode.node("handcuffs", "show-title").getBoolean(true);

        /*
         * Pepperspray configuration
         */
        this.peppersprayItems = loadItemMappings(rootNode.node("pepperspray", "items"), List.of(
                new ItemStack(Material.RED_DYE)
        ));
        this.peppersprayUsagesEnabled = rootNode.node("pepperspray", "usages-enabled").getBoolean(true);
        this.peppersprayMaxUsages = rootNode.node("pepperspray", "max-usages").getInt(10);
        this.peppersprayEffectsDuration = rootNode.node("pepperspray", "effects-duration").getInt(5);
        this.peppersprayEffects = loadEffectMappings(rootNode.node("pepperspray", "effects"), List.of(
                new PotionEffect(PotionEffectType.BLINDNESS, -1, 0)
        ));

        /*
         * Nightvision goggles configuration
         */
        this.nightvisionItems = loadItemMappings(rootNode.node("nightvision", "items"), List.of(
                new ItemStack(Material.GREEN_DYE)
        ));
        this.nightvisionEffects = loadEffectMappings(rootNode.node("nightvision", "effects"), List.of(
                new PotionEffect(PotionEffectType.NIGHT_VISION, -1, 0)
        ));

        /*
         * Taser configuration
         */
        this.taserItems = loadItemMappings(rootNode.node("taser", "items"), List.of(
                new ItemStack(Material.LIGHT_BLUE_DYE)
        ));
        this.taserUsagesEnabled = rootNode.node("taser", "usages-enabled").getBoolean(true);
        this.taserMaxUsages = rootNode.node("taser", "max-usages").getInt(10);
        this.taserCooldown = rootNode.node("taser", "cooldown").getInt(3);
        this.taserFreezeEnabled = rootNode.node("taser", "freeze", "enabled").getBoolean(true);
        this.taserFreezeDuration = rootNode.node("taser", "freeze", "duration").getInt(3);
        this.taserEffectsDuration = rootNode.node("taser", "effects-duration").getInt(5);
        this.taserEffects = loadEffectMappings(rootNode.node("taser", "effects"), List.of(
                new PotionEffect(PotionEffectType.BLINDNESS, -1, 0),
                new PotionEffect(PotionEffectType.SLOWNESS, -1, 0)
        ));

        /*
         * Head configuration
         */
        this.headWhitelist = loadItemMappings(rootNode.node("head", "whitelist"), List.of(
                new ItemStack(Material.CLAY_BALL),
                new ItemStack(Material.BEDROCK),
                new ItemStack(Material.SPONGE),
                new ItemStack(Material.IRON_ORE),
                new ItemStack(Material.COAL_ORE),
                new ItemStack(Material.LAPIS_ORE),
                new ItemStack(Material.DIAMOND_ORE),
                new ItemStack(Material.REDSTONE_ORE),
                new ItemStack(Material.SOUL_SAND),
                new ItemStack(Material.NETHERRACK),
                new ItemStack(Material.NETHER_BRICK),
                new ItemStack(Material.END_STONE),
                new ItemStack(Material.NETHER_QUARTZ_ORE),
                new ItemStack(Material.EMERALD_ORE),
                new ItemStack(Material.PRISMARINE),
                new ItemStack(Material.RED_SANDSTONE),
                new ItemStack(Material.INK_SAC),
                new ItemStack(Material.MAGMA_CREAM),
                new ItemStack(Material.NETHER_WART),
                new ItemStack(Material.PRISMARINE_SHARD),
                new ItemStack(Material.PRISMARINE_CRYSTALS),
                new ItemStack(Material.CARROT_ON_A_STICK),
                new ItemStack(Material.SHEARS),
                new ItemStack(Material.GLASS),
                new ItemStack(Material.BLACK_STAINED_GLASS),
                new ItemBuilder(Material.DIAMOND_HOE).setCustomModelData(89).toItemStack(),
                new ItemStack(Material.GREEN_DYE)
        ));

        /*
         * Bodysearch configuration
         */
        this.bodysearchRange = rootNode.node("bodysearch", "range").getInt(10);

        /*
         * Walkie-talkie configuration
         */
        this.walkieTalkieItems = loadItemMappings(rootNode.node("walkie", "items"), List.of(
                new ItemStack(Material.PINK_DYE)
        ));
        this.walkieTalkieEmergencyCooldownEnabled = rootNode.node("walkietalkie", "emergency-button", "cooldown-enabled").getBoolean(true);
        this.walkieTalkieEmergencyCooldownSeconds = rootNode.node("walkietalkie", "emergency-button", "cooldown-seconds").getInt(60);

        /*
         * Trashcan configuration
         */
        this.trashcanEnabled = rootNode.node("trashcan", "enabled").getBoolean(true);
        this.trashcanBlocks = rootNode.node("trashcan", "blocks").getList(String.class, List.of(
                "DROPPER"
        )).stream().map(Material::matchMaterial).toList();
        this.trashcanUseDropperInventory = rootNode.node("trashcan", "use-dropper-inventory").getBoolean(false);

        /*
         * Timesync configuration
         */
        this.syncTime = rootNode.node("timesync", "enabled").getBoolean(true);
        this.syncTimeZone = rootNode.node("timesync", "timezone").getString("Europe/Amsterdam");
    }

    @SneakyThrows
    public void addToHeadWhitelist(ItemStack item) {
        // Check if the item is valid
        if (item == null || item.getType() == Material.AIR) return;

        // Check for item metadata and custom model data
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            // Check for custom model data and item damage
            int customModelData = meta.hasCustomModelData() ? meta.getCustomModelData() : -1;
            Damageable damageable = (Damageable) meta;
            int itemDamage = damageable.getDamage();

            // Check if the item is already in the whitelist
            NamespacedKey itemModel = meta.hasItemModel() ? meta.getItemModel() : null;

            if (customModelData != -1) rootNode.node("head", "whitelist", item.getType().toString(), "custom-model-data").set(customModelData);
            if (itemDamage != 0) rootNode.node("head", "whitelist", item.getType().toString(), "damage").set(itemDamage);
            if (itemModel != null) rootNode.node("head", "whitelist", item.getType().toString(), "item-model").set(itemModel);
        }
        // Add to whitelist and save configuration
        this.headWhitelist.add(item);
        saveConfiguration();
    }


    @SneakyThrows
    public void removeFromHeadWhitelist(ItemStack item) {
        for (ItemStack itemStack : this.headWhitelist) {
            if (ItemUtils.isValidItem(item, headWhitelist)) {
                this.headWhitelist.remove(itemStack);
                break;
            }
        }
        rootNode.node("head", "whitelist", item.getType().name()).set(null);
        saveConfiguration();
    }

    @SneakyThrows
    private List<ItemStack> loadItemMappings(ConfigurationNode itemsNode, List<ItemStack> defaultItems) {
        if (itemsNode.isNull()) {
            defaultItems.forEach((itemStack) -> {
                ConfigurationNode itemNode = itemsNode.node(itemStack.getType().name());
                try {
                    int customModelData = itemStack.getItemMeta().hasCustomModelData() ? itemStack.getItemMeta().getCustomModelData() : -1;
                    if (customModelData != -1) itemNode.node("custom-model-data").set(customModelData);

                    Damageable damageable = (Damageable) itemStack.getItemMeta();
                    int damage = damageable.hasDamage() ? damageable.getDamage() : -1;
                    if (damage != -1) itemNode.node("damage").set(damage);

                    if (itemStack.getItemMeta().hasItemModel() && itemStack.getItemMeta().getItemModel() != null) {
                        itemNode.node("item-model").set(itemStack.getItemMeta().getItemModel());
                    }
                } catch (Exception e) {
                    OpenMinetopia.getInstance().getLogger().warning("Failed to load item: " + itemStack.getType().name());
                }
            });
        }

        List<ItemStack> itemList = new ArrayList<>();
        itemsNode.childrenMap().forEach((key, val) -> {
            Material itemMaterial = Material.matchMaterial(key.toString());
            if (itemMaterial == null) return;
            int customModelData = val.node("custom-model-data").getInt(-1);
            int damage = val.node("damage").getInt(-1);
            String itemModel = val.node("item-model").getString("");

            ItemBuilder itemBuilder = new ItemBuilder(itemMaterial);

            if (customModelData != -1) itemBuilder.setCustomModelData(customModelData);
            if (damage != -1) itemBuilder.setDamage(damage);
            if (!itemModel.isEmpty()) itemBuilder.setItemModel(itemModel);

            itemList.add(itemBuilder.toItemStack());
        });

        return itemList;
    }

    @SneakyThrows
    private List<PotionEffect> loadEffectMappings(ConfigurationNode effectsNode, List<PotionEffect> defaultEffects) {
        if (effectsNode.isNull()) {
            defaultEffects.forEach((effect) -> {
                NamespacedKey effectKey = effect.getType().getKey();

                PotionEffectType potionEffectType = Registry.EFFECT.get(effectKey);
                if (potionEffectType == null) {
                    OpenMinetopia.getInstance().getLogger().warning("Invalid potion effect: " + effectKey);
                    return;
                }

                String effectName = potionEffectType.getKey().getKey().toLowerCase();
                ConfigurationNode effectNode = effectsNode.node(effectName);

                int amplifier = effect.getAmplifier();
                int duration = effect.getDuration();

                try {
                    effectNode.node("amplifier").set(amplifier);
                    effectNode.node("duration").set(duration);
                } catch (Exception e) {
                    OpenMinetopia.getInstance().getLogger().warning("Failed to load effect: " + effectName);
                }
            });
        }

        List<PotionEffect> potionEffects = new ArrayList<>();

        effectsNode.childrenMap().forEach((key, val) -> {
            String effectName = key.toString().toLowerCase();
            PotionEffectType potionEffectType = Registry.EFFECT.get(NamespacedKey.minecraft(effectName));

            if (potionEffectType == null) {
                OpenMinetopia.getInstance().getLogger().warning("Invalid potion effect: " + effectName);
                return;
            }

            int amplifier = val.node("amplifier").getInt(0);
            int duration = val.node("duration").getInt(600); // Default duration if not specified
            
            potionEffects.add(new PotionEffect(potionEffectType, duration, amplifier));
        });

        return potionEffects;
    }
}