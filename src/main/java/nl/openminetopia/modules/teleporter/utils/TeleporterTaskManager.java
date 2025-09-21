package nl.openminetopia.modules.teleporter.utils;

import nl.openminetopia.modules.teleporter.tasks.TeleporterCountdownTask;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Manages active teleporter countdown tasks
 */
public class TeleporterTaskManager {
    
    private static TeleporterTaskManager instance;
    private final Map<UUID, TeleporterCountdownTask> activeTasks = new HashMap<>();
    
    private TeleporterTaskManager() {}
    
    public static TeleporterTaskManager getInstance() {
        if (instance == null) {
            instance = new TeleporterTaskManager();
        }
        return instance;
    }
    
    /**
     * Check if a player has an active teleporter task
     * @param player The player to check
     * @return true if the player has an active task, false otherwise
     */
    public boolean hasActiveTask(Player player) {
        return activeTasks.containsKey(player.getUniqueId());
    }
    
    /**
     * Register a new teleporter task for a player
     * @param player The player
     * @param task The teleporter task
     */
    public void registerTask(Player player, TeleporterCountdownTask task) {
        // Cancel any existing task for this player
        cancelTask(player);
        
        activeTasks.put(player.getUniqueId(), task);
    }
    
    /**
     * Cancel and remove a teleporter task for a player
     * @param player The player
     */
    public void cancelTask(Player player) {
        TeleporterCountdownTask task = activeTasks.remove(player.getUniqueId());
        if (task != null && !task.isCancelled()) {
            task.cancel();
        }
    }
    
    /**
     * Get the active task for a player
     * @param player The player
     * @return The active task, or null if none
     */
    public TeleporterCountdownTask getActiveTask(Player player) {
        return activeTasks.get(player.getUniqueId());
    }
    
    /**
     * Clear all active tasks
     */
    public void clearAllTasks() {
        for (TeleporterCountdownTask task : activeTasks.values()) {
            if (!task.isCancelled()) {
                task.cancel();
            }
        }
        activeTasks.clear();
    }
}
