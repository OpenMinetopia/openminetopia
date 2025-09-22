package nl.openminetopia.modules.boots.listeners;

import nl.openminetopia.modules.boots.enums.BootEffect;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BootsListener implements Listener {
    
    private final Map<UUID, BukkitTask> activeTasks = new HashMap<>();
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        startBootsTask(player);
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        stopBootsTask(player);
    }
    
    private void startBootsTask(Player player) {
        BukkitTask task = new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.isOnline()) {
                    cancel();
                    return;
                }
                
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
                        // Speed is handled by attribute modifiers on the item itself
                        break;
                    case BLUB:
                        // Blub is handled by depth strider enchantment
                        break;
                    case ICE:
                        // Frost Walker is handled by enchantments
                        break;
                }
            }
        }.runTaskTimer(org.bukkit.Bukkit.getPluginManager().getPlugin("OpenMinetopia"), 0L, 20L); // Every second
        
        activeTasks.put(player.getUniqueId(), task);
    }
    
    private void stopBootsTask(Player player) {
        BukkitTask task = activeTasks.remove(player.getUniqueId());
        if (task != null) {
            task.cancel();
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
