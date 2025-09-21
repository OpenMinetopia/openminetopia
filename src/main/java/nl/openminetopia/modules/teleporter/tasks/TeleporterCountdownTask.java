package nl.openminetopia.modules.teleporter.tasks;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;
import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.modules.teleporter.utils.TeleporterCooldownManager;
import nl.openminetopia.modules.teleporter.utils.TeleporterTaskManager;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Handles the countdown and teleportation for teleporters
 */
public class TeleporterCountdownTask extends BukkitRunnable {
    
    private final Player player;
    private final Location destination;
    private final Location startLocation;
    private final int cooldownSeconds;
    private int currentCountdown;
    
    public TeleporterCountdownTask(Player player, Location destination, Location startLocation, int cooldownSeconds) {
        this.player = player;
        this.destination = destination;
        this.startLocation = startLocation;
        this.cooldownSeconds = cooldownSeconds;
        this.currentCountdown = cooldownSeconds;
    }
    
    @Override
    public void run() {
        // Check if player is still online
        if (!player.isOnline()) {
            TeleporterTaskManager.getInstance().cancelTask(player);
            this.cancel();
            return;
        }
        
        // Check if player moved away from the teleporter
        if (hasPlayerMoved()) {
            Component cancelMessage = Component.text("Teleportatie geannuleerd - je bent te ver weg!")
                    .color(NamedTextColor.RED);
            player.sendMessage(cancelMessage);
            TeleporterTaskManager.getInstance().cancelTask(player);
            this.cancel();
            return;
        }
        
        if (currentCountdown > 0) {
            // Show countdown title
            showCountdownTitle(currentCountdown);
            currentCountdown--;
        } else {
            // Teleport the player
            teleportPlayer();
            TeleporterTaskManager.getInstance().cancelTask(player);
            this.cancel();
        }
    }
    
    private void showCountdownTitle(int countdown) {
        // Create the main title with cyan color and bold
        Component mainTitle = Component.text("Reizen in " + countdown)
                .color(NamedTextColor.AQUA)
                .decorate(TextDecoration.BOLD);
        
        // Create the subtitle with yellow color and bold
        Component subtitle = Component.text("Beweeg niet!")
                .color(NamedTextColor.YELLOW)
                .decorate(TextDecoration.BOLD);
        
        // Create the title with proper timing to prevent transparency issues
        Title title = Title.title(mainTitle, subtitle, 
                Title.Times.times(java.time.Duration.ofMillis(500), 
                                 java.time.Duration.ofMillis(1000), 
                                 java.time.Duration.ofMillis(200)));
        
        // Show the title to the player
        player.showTitle(title);
    }
    
    private void teleportPlayer() {
        // Set cooldown to prevent immediate re-teleportation
        TeleporterCooldownManager.getInstance().setCooldown(player, cooldownSeconds);
        
        // Teleport the player
        player.teleport(destination);
        
        // No success message to prevent spam
    }
    
    /**
     * Check if the player has moved away from the teleporter
     * @return true if the player has moved too far, false otherwise
     */
    private boolean hasPlayerMoved() {
        Location currentLocation = player.getLocation();
        return currentLocation.distance(startLocation) > 2.0; // Allow 2 blocks of movement
    }
    
    /**
     * Start the countdown task
     */
    public void start() {
        if (cooldownSeconds <= 0) {
            // No cooldown, teleport immediately
            teleportPlayer();
        } else {
            // Register this task with the manager
            TeleporterTaskManager.getInstance().registerTask(player, this);
            // Start countdown
            this.runTaskTimer(OpenMinetopia.getInstance(), 0L, 20L); // Run every second
        }
    }
}
