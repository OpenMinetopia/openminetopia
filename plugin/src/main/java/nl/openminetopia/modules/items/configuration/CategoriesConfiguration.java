package nl.openminetopia.modules.items.configuration;

import lombok.Getter;
import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.modules.items.configuration.objects.ItemCategory;
import nl.openminetopia.utils.config.ConfigurateConfig;
import nl.openminetopia.utils.item.ItemBuilder;
import org.bukkit.Material;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Getter
public class CategoriesConfiguration extends ConfigurateConfig {

    private final Map<String, ItemCategory> categories = new HashMap<>();

    public CategoriesConfiguration(File file) {
        super(file, "categories.yml", "default/items/categories.yml", false);

        rootNode.childrenMap().forEach((key, value) -> {
            if (!(key instanceof String id)) {
                OpenMinetopia.getInstance().getLogger().warning("Category namespace is not a string: " + key);
                return;
            }

            /* --- category-info */
            String name = value.node("name").getString();
            String namespace = value.node("namespace").getString();

            String iconMaterialName = value.node("icon").node("material").getString("STONE");
            Material iconMaterial = Material.getMaterial(iconMaterialName.toUpperCase());
            if (iconMaterial == null) iconMaterial = Material.STONE;

            ItemBuilder iconItem = new ItemBuilder(iconMaterial);
            String iconItemModel = value.node("icon").node("item_model").getString();
            if (iconItemModel != null) iconItem.setItemModel(iconItemModel);

            int iconItemCustomModelData = value.node("icon").node("custom_model_data").getInt(-1);
            if (iconItemCustomModelData >= 0) iconItem.setCustomModelData(iconItemCustomModelData);

            if (name == null || name.isEmpty()) {
                OpenMinetopia.getInstance().getLogger().warning("Category name is not defined for: " + key);
                return;
            }

            if (namespace == null || namespace.isEmpty()) {
                OpenMinetopia.getInstance().getLogger().warning("Category namespace is not defined for: " + id);
                return;
            }

            ItemCategory itemCategory = new ItemCategory(name, namespace, iconItem.toItemStack(), new ArrayList<>());
            categories.put(namespace, itemCategory);
        });
    }

    public Optional<ItemCategory> category(String namespace) {
        if (!categories.containsKey(namespace)) return Optional.empty();
        return Optional.of(categories.get(namespace));
    }
}
