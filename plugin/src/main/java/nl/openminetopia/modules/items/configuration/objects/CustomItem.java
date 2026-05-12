package nl.openminetopia.modules.items.configuration.objects;

import nl.openminetopia.utils.item.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public record CustomItem(ItemCategory itemCategory,
                         String identifier,
                         Material material,
                         String itemModel,
                         String name,
                         Integer customModelData,
                         EquipmentSlot equippableSlot,
                         NamespacedKey equippableAssetId
) {

    public CustomItem {
        if (identifier == null || identifier.isEmpty()) {
            throw new IllegalArgumentException("Identifier cannot be null or empty");
        }
        if (material == null || material.isEmpty()) {
            throw new IllegalArgumentException("Material cannot be null or empty");
        }
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
    }

    public ItemStack build() {
        ItemBuilder builder = new ItemBuilder(material).setName(name);

        if (itemModel != null && !itemModel.isEmpty()) builder.setItemModel(itemModel);
        if (customModelData != null && customModelData >= 0) builder.setCustomModelData(customModelData);
        if (equippableSlot != null) builder.setEquippableSlot(equippableSlot);
        if (equippableAssetId != null) builder.setEquippableModel(equippableAssetId);

        return builder.toItemStack();
    }

    public NamespacedKey namespacedKey() {
        return new NamespacedKey(itemCategory.namespace(), identifier);
    }
}