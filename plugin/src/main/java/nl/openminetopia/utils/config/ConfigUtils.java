package nl.openminetopia.utils.config;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.utils.VersionUtil;
import nl.openminetopia.utils.item.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.spongepowered.configurate.ConfigurationNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@UtilityClass
public class ConfigUtils {

    @SneakyThrows
    public List<ItemStack> loadItemMappings(ConfigurationNode itemsNode, List<ItemStack> defaultItems) {
        if (itemsNode.isNull()) {
            List<Map<Object, Object>> itemList = new ArrayList<>();
            for (ItemStack itemStack : defaultItems) {
                itemList.add(serializeItemStack(itemStack));
            }
            itemsNode.set(itemList);
        }

        List<ItemStack> itemList = new ArrayList<>();
        for (ConfigurationNode val : itemsNode.childrenList()) {
            ItemStack item = deserializeItemStack(val);
            if (item != null) {
                itemList.add(item);
            }
        }

        return itemList;
    }

    public Map<Object, Object> serializeItemStack(ItemStack itemStack) {
        Map<Object, Object> itemMap = new HashMap<>();
        try {
            itemMap.put("type", itemStack.getType().name());

            int customModelData = itemStack.getItemMeta().hasCustomModelData() ? itemStack.getItemMeta().getCustomModelData() : -1;
            itemMap.put("custom-model-data", customModelData);

            Damageable damageable = (Damageable) itemStack.getItemMeta();
            int damage = damageable.hasDamage() ? damageable.getDamage() : -1;
            if (damage != -1) itemMap.put("damage", damage);

            if (VersionUtil.isCompatible("1.21.4") && itemStack.getItemMeta().hasItemModel() && itemStack.getItemMeta().getItemModel() != null) {
                itemMap.put("item-model", itemStack.getItemMeta().getItemModel().toString());
            }
        } catch (Exception e) {
            OpenMinetopia.getInstance().getLogger().warning("Failed to serialize item: " + itemStack.getType().name());
        }
        return itemMap;
    }

    public ItemStack deserializeItemStack(ConfigurationNode val) {
        String typeName = val.node("type").getString();
        if (typeName == null) return null;

        Material itemMaterial = Material.matchMaterial(typeName);
        if (itemMaterial == null) return null;

        int customModelData = val.node("custom-model-data").getInt(-1);
        int damage = val.node("damage").getInt(-1);
        String itemModel = val.node("item-model").getString("");

        ItemBuilder itemBuilder = new ItemBuilder(itemMaterial);

        if (customModelData != -1) itemBuilder.setCustomModelData(customModelData);
        if (damage != -1) itemBuilder.setDamage(damage);
        if (VersionUtil.isCompatible("1.21.4") && !itemModel.isEmpty()) itemBuilder.setItemModel(itemModel);

        return itemBuilder.toItemStack();
    }

    @SneakyThrows
    public List<PotionEffect> loadEffectMappings(ConfigurationNode effectsNode, List<PotionEffect> defaultEffects) {
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
