package nl.openminetopia.modules.teleporter.utils;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Manages teleporter cooldowns for players
 */
public class TeleporterCooldownManager {
    
    private static TeleporterCooldownManager instance;
    private final Map<UUID, Long> cooldowns = new HashMap<>();
    
    private TeleporterCooldownManager() {}
    
    public static TeleporterCooldownManager getInstance() {
        if (instance == null) {
            instance = new TeleporterCooldownManager();
        }
        return instance;
    }
    
    /**
     * Check if a player is currently on cooldown
     * @param player The player to check
     * @return true if the player is on cooldown, false otherwise
     */
    public boolean isOnCooldown(Player player) {
        UUID playerId = player.getUniqueId();
        if (!cooldowns.containsKey(playerId)) {
            return false;
        }
        
        long cooldownEnd = cooldowns.get(playerId);
        long currentTime = System.currentTimeMillis();
        
        if (currentTime >= cooldownEnd) {
            cooldowns.remove(playerId);
            return false;
        }
        
        return true;
    }
    
    /**
     * Get the remaining cooldown time in seconds
     * @param player The player to check
     * @return The remaining cooldown time in seconds, or 0 if not on cooldown
     */
    public int getRemainingCooldown(Player player) {
        UUID playerId = player.getUniqueId();
        if (!cooldowns.containsKey(playerId)) {
            return 0;
        }
        
        long cooldownEnd = cooldowns.get(playerId);
        long currentTime = System.currentTimeMillis();
        
        if (currentTime >= cooldownEnd) {
            cooldowns.remove(playerId);
            return 0;
        }
        
        return (int) Math.ceil((cooldownEnd - currentTime) / 1000.0);
    }
    
    /**
     * Set a cooldown for a player
     * @param player The player to set cooldown for
     * @param seconds The cooldown duration in seconds
     */
    public void setCooldown(Player player, int seconds) {
        UUID playerId = player.getUniqueId();
        long cooldownEnd = System.currentTimeMillis() + (seconds * 1000L);
        cooldowns.put(playerId, cooldownEnd);
    }
    
    /**
     * Remove cooldown for a player
     * @param player The player to remove cooldown for
     */
    public void removeCooldown(Player player) {
        cooldowns.remove(player.getUniqueId());
    }
    
    /**
     * Clear all cooldowns
     */
    public void clearAllCooldowns() {
        cooldowns.clear();
    }
}
