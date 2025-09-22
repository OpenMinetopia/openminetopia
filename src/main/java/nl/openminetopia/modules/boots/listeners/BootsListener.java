package nl.openminetopia.modules.boots.listeners;

import nl.openminetopia.modules.boots.enums.BootEffect;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class BootsListener implements Listener {
    
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        ItemStack boots = player.getInventory().getBoots();
        
        if (boots == null || boots.getType() == Material.AIR) {
            return;
        }
        
        if (!isCustomBoots(boots)) {
            return;
        }
        
        BootEffect effect = getBootsEffect(boots);
        double multiplier = getBootsMultiplier(boots);
        
        if (effect == null) {
            return;
        }
        
        switch (effect) {
            case SPEED:
                applySpeedEffect(player, multiplier);
                break;
            case BLUB:
                applySwimEffect(player, multiplier);
                break;
            case ICE:
                // Frost Walker is handled by enchantments
                break;
        }
    }
    
    private boolean isCustomBoots(ItemStack boots) {
        ItemMeta meta = boots.getItemMeta();
        if (meta == null) return false;
        
        return meta.getPersistentDataContainer().has(
            new NamespacedKey("openmt", "custom_boots"), 
            PersistentDataType.BOOLEAN
        );
    }
    
    private BootEffect getBootsEffect(ItemStack boots) {
        ItemMeta meta = boots.getItemMeta();
        if (meta == null) return null;
        
        String effectName = meta.getPersistentDataContainer().get(
            new NamespacedKey("openmt", "boots_effect"), 
            PersistentDataType.STRING
        );
        
        if (effectName == null) return null;
        
        try {
            return BootEffect.valueOf(effectName);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
    
    private double getBootsMultiplier(ItemStack boots) {
        ItemMeta meta = boots.getItemMeta();
        if (meta == null) return 0.0;
        
        Double multiplier = meta.getPersistentDataContainer().get(
            new NamespacedKey("openmt", "boots_multiplier"), 
            PersistentDataType.DOUBLE
        );
        
        return multiplier != null ? multiplier : 0.0;
    }
    
    private void applySpeedEffect(Player player, double multiplier) {
        // Convert multiplier to speed level (multiplier * 10 for reasonable speed boost)
        int speedLevel = (int) (multiplier * 10);
        if (speedLevel > 0) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 40, speedLevel - 1, false, false));
        }
    }
    
    private void applySwimEffect(Player player, double multiplier) {
        // Apply dolphin's grace for swimming speed
        if (player.isSwimming()) {
            int dolphinLevel = (int) (multiplier * 10);
            if (dolphinLevel > 0) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.DOLPHINS_GRACE, 40, dolphinLevel - 1, false, false));
            }
        }
    }
}
