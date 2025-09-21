package nl.openminetopia.modules.teleporter.tasks;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;
import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.modules.teleporter.utils.TeleporterCooldownManager;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Handles the countdown and teleportation for teleporters
 */
public class TeleporterCountdownTask extends BukkitRunnable {
    
    private final Player player;
    private final Location destination;
    private final int cooldownSeconds;
    private int currentCountdown;
    
    public TeleporterCountdownTask(Player player, Location destination, int cooldownSeconds) {
        this.player = player;
        this.destination = destination;
        this.cooldownSeconds = cooldownSeconds;
        this.currentCountdown = cooldownSeconds;
    }
    
    @Override
    public void run() {
        // Check if player is still online
        if (!player.isOnline()) {
            this.cancel();
            return;
        }
        
        // Check if player moved (optional - you might want to add this check)
        // For now, we'll just continue with the countdown
        
        if (currentCountdown > 0) {
            // Show countdown title
            showCountdownTitle(currentCountdown);
            currentCountdown--;
        } else {
            // Teleport the player
            teleportPlayer();
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
        
        // Create the title
        Title title = Title.title(mainTitle, subtitle);
        
        // Show the title to the player
        player.showTitle(title);
    }
    
    private void teleportPlayer() {
        // Set cooldown to prevent immediate re-teleportation
        TeleporterCooldownManager.getInstance().setCooldown(player, cooldownSeconds);
        
        // Teleport the player
        player.teleport(destination);
        
        // Show a brief success message (optional)
        Component successMessage = Component.text("Teleportatie voltooid!")
                .color(NamedTextColor.GREEN);
        player.sendMessage(successMessage);
    }
    
    /**
     * Start the countdown task
     */
    public void start() {
        if (cooldownSeconds <= 0) {
            // No cooldown, teleport immediately
            teleportPlayer();
        } else {
            // Start countdown
            this.runTaskTimer(OpenMinetopia.getInstance(), 0L, 20L); // Run every second
        }
    }
}
