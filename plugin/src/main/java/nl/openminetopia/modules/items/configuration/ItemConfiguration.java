package nl.openminetopia.modules.items.configuration;

import lombok.SneakyThrows;
import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.modules.items.ItemsModule;
import nl.openminetopia.modules.items.configuration.objects.CustomItem;
import nl.openminetopia.modules.items.configuration.objects.ItemCategory;
import nl.openminetopia.utils.config.ConfigurateConfig;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.EquipmentSlot;
import org.spongepowered.configurate.ConfigurationNode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ItemConfiguration extends ConfigurateConfig {

    @SneakyThrows
    public ItemConfiguration(File file, String fileName) {
        super(file, fileName, "", false);

        ItemsModule module = OpenMinetopia.getModuleManager().get(ItemsModule.class);

        String namespace = rootNode.node("namespace").getString();
        if (namespace == null || namespace.isEmpty()) {
            OpenMinetopia.getInstance().getLogger().warning("Namespace is not defined in the configuration file: " + fileName);
            return;
        }

        Optional<ItemCategory> categoryOpt = module.getCategoriesConfiguration().category(namespace);
        if (categoryOpt.isEmpty()) {
            OpenMinetopia.getInstance().getLogger().warning("No category found with namespace: " + namespace);
            return;
        }
        ItemCategory category = categoryOpt.get();

        ConfigurationNode itemsNode = rootNode.node("items");
        List<CustomItem> items = new ArrayList<>();

        itemsNode.childrenMap().forEach((key, value) -> {
            if (!(key instanceof String identifier)) {
                OpenMinetopia.getInstance().getLogger().warning("Item identifier is not a string: " + key);
                return;
            }

            final String itemName = value.node("name").getString();
            if (itemName == null || itemName.isEmpty()) {
                OpenMinetopia.getInstance().getLogger().warning("Item " + identifier + " has no name defined");
                return;
            }

            final String materialName = value.node("material").getString();
            if (materialName == null || materialName.isEmpty()) {
                OpenMinetopia.getInstance().getLogger().warning("Item " + itemName + " has no material defined");
                return;
            }

            final Material material = Material.getMaterial(materialName.toUpperCase());
            if (material == null) {
                OpenMinetopia.getInstance().getLogger().warning("Item " + itemName + " has an invalid material: " + materialName);
                return;
            }

            final String itemModel = value.node("item_model").getString("");
            final int customModelData = value.node("custom_model_data").getInt(-1);

            ConfigurationNode equippableNode = value.node("equippable");

            Optional<EquipmentSlot> equippableSlot = Optional.ofNullable(equippableNode.node("slot").getString())
                    .map(s -> {
                        try {
                            return EquipmentSlot.valueOf(s.toUpperCase());
                        } catch (IllegalArgumentException e) {
                            OpenMinetopia.getInstance().getLogger().warning("Invalid equippable slot for item " + identifier + ": " + s);
                            return null;
                        }
                    });

            Optional<NamespacedKey> equippableAssetId = Optional.ofNullable(equippableNode.node("asset_id").getString())
                    .map(s -> {
                        String[] parts = s.split(":");
                        if (parts.length != 2) {
                            OpenMinetopia.getInstance().getLogger().warning("Equippable asset_id is not in the format 'namespace:id': " + s + " for item " + identifier);
                            return null;
                        }
                        return new NamespacedKey(parts[0], parts[1]);
                    });

            items.add(new CustomItem(
                    category,
                    identifier,
                    material,
                    itemModel,
                    itemName,
                    customModelData,
                    equippableSlot.orElse(null),
                    equippableAssetId.orElse(null)
            ));
        });

        // Update the category with the new items
        List<CustomItem> updatedItems = category.items();
        if (updatedItems == null) updatedItems = new ArrayList<>();
        updatedItems.addAll(items);

        ItemCategory updatedCategory = category.items(updatedItems);
        module.getCategoriesConfiguration().getCategories().put(namespace, updatedCategory);
    }
}