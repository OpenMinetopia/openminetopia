package nl.openminetopia.modules.items.configuration.objects;

import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Optional;

public record ItemCategory(String name, String namespace, ItemStack icon, List<CustomItem> items) {

    public ItemCategory {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        if (namespace == null || namespace.isEmpty()) {
            throw new IllegalArgumentException("Namespace cannot be null or empty");
        }
    }

    public ItemCategory items(List<CustomItem> items) {
        return new ItemCategory(name, namespace, icon, items);
    }

    public Optional<CustomItem> item(String identifier) {
        return items.stream()
                .filter(item -> item.identifier().equals(identifier))
                .findFirst();
    }
}