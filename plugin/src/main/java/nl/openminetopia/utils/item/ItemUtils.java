package nl.openminetopia.utils.item;

import lombok.experimental.UtilityClass;
import nl.openminetopia.utils.VersionUtil;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

@UtilityClass
public class ItemUtils {

    public void applyEffects(Player target, List<PotionEffect> effects, int duration) {
        for (PotionEffect effect : effects) {
            PotionEffect potionEffect = new PotionEffect(effect.getType(), duration, effect.getAmplifier());
            target.addPotionEffect(potionEffect);
        }
    }

    public void clearEffects(Player target, List<PotionEffect> effects) {
        for (PotionEffect effect : effects) {
            PotionEffectType potionEffectType = effect.getType();
            target.getActivePotionEffects().forEach(activeEffect -> {
                if (activeEffect.getType().equals(potionEffectType)) {
                    target.removePotionEffect(potionEffectType);
                }
            });
        }
    }

    public boolean isSimilarToAny(ItemStack itemToCheck, List<ItemStack> validItems) {
        if (itemToCheck == null || validItems == null || validItems.isEmpty()) return false;

        for (ItemStack item : validItems) {
            if (item == null) continue;

            if (item.getType() != itemToCheck.getType()) continue;

            ItemMeta meta = item.getItemMeta();
            ItemMeta checkMeta = itemToCheck.getItemMeta();

            if (meta == null && checkMeta == null) return true;
            if (meta == null || checkMeta == null) continue;

            if (meta.hasCustomModelData() != checkMeta.hasCustomModelData()) continue;
            if (meta.hasCustomModelData() && meta.getCustomModelData() != checkMeta.getCustomModelData()) continue;

            if (meta instanceof Damageable damageMeta && checkMeta instanceof Damageable checkDamageMeta) {
                if (damageMeta.getDamage() != checkDamageMeta.getDamage()) continue;
            }

            if (VersionUtil.isCompatible("1.21.4")) {
                NamespacedKey modelA = meta.getItemModel();
                NamespacedKey modelB = checkMeta.getItemModel();

                if (modelA != null || modelB != null) {
                    if (modelA == null || modelB == null) continue;
                    if (!modelA.equals(modelB)) continue;
                }
            }
            return true;
        }
        return false;
    }
}
