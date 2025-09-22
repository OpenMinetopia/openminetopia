package nl.openminetopia.modules.boots.menus;

import dev.triumphteam.gui.builder.item.PaperItemBuilder;
import nl.openminetopia.modules.boots.enums.BootEffect;
import nl.openminetopia.modules.boots.enums.BootType;
import nl.openminetopia.modules.boots.objects.CustomBoots;
import nl.openminetopia.utils.ChatUtils;
import nl.openminetopia.utils.menu.Menu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class BootMultiplierSelectionMenu extends Menu {

    public BootMultiplierSelectionMenu(BootType bootType, BootEffect effect) {
        super("<gold>Boots Multiplier - " + bootType.getDisplayName() + " (" + effect.getDisplayName() + ")", 3);
        
        // Disable all interactions to prevent items from being taken
        gui.disableAllInteractions();
        
        // Fill borders with red stained glass panes
        fillBorders();
        
        // Add multiplier options based on effect type
        if (effect == BootEffect.SPEED) {
            addSpeedMultipliers(bootType, effect);
        } else if (effect == BootEffect.ICE) {
            addIceMultipliers(bootType, effect);
        } else if (effect == BootEffect.BLUB) {
            addBlubMultipliers(bootType, effect);
        }
    }
    
    private void addSpeedMultipliers(BootType bootType, BootEffect effect) {
        double[] multipliers = {0.1, 0.2, 0.3, 0.4, 0.5};
        int[] slots = {9, 11, 13, 15, 17};
        
        for (int i = 0; i < multipliers.length; i++) {
            double multiplier = multipliers[i];
            ItemStack chainmailBoots = new ItemStack(Material.CHAINMAIL_BOOTS);
            
            gui.setItem(slots[i], PaperItemBuilder.from(chainmailBoots)
                    .name(ChatUtils.color("<gold>Speed Multiplier: " + multiplier))
                    .lore(ChatUtils.color("<gray>Klik om deze boots te maken"))
                    .asGuiItem(event -> {
                        Player player = (Player) event.getWhoClicked();
                        CustomBoots customBoots = new CustomBoots(bootType, effect, multiplier);
                        player.getInventory().addItem(customBoots.createBoots());
                        
                        // Create proper message with boot type and multiplier
                        String bootTypeName = "Speedboots " + multiplier;
                        player.sendMessage(ChatUtils.color("<green>Je hebt " + bootTypeName + " ontvangen!"));
                        player.closeInventory();
                    }));
        }
    }
    
    private void addIceMultipliers(BootType bootType, BootEffect effect) {
        int[] levels = {1, 2};
        int[] slots = {11, 13};
        
        for (int i = 0; i < levels.length; i++) {
            int level = levels[i];
            ItemStack chainmailBoots = new ItemStack(Material.CHAINMAIL_BOOTS);
            
            gui.setItem(slots[i], PaperItemBuilder.from(chainmailBoots)
                    .name(ChatUtils.color("<gold>Frost Walker Level: " + level))
                    .lore(ChatUtils.color("<gray>Klik om deze boots te maken"))
                    .asGuiItem(event -> {
                        Player player = (Player) event.getWhoClicked();
                        CustomBoots customBoots = new CustomBoots(bootType, effect, level);
                        player.getInventory().addItem(customBoots.createBoots());
                        
                        // Create proper message with boot type and multiplier
                        String bootTypeName = "Ice Boots " + level;
                        player.sendMessage(ChatUtils.color("<green>Je hebt " + bootTypeName + " ontvangen!"));
                        player.closeInventory();
                    }));
        }
    }
    
    private void addBlubMultipliers(BootType bootType, BootEffect effect) {
        // For blub boots, we'll use the same speed multipliers but with different description
        double[] multipliers = {0.1, 0.2, 0.3, 0.4, 0.5};
        int[] slots = {9, 11, 13, 15, 17};
        
        for (int i = 0; i < multipliers.length; i++) {
            double multiplier = multipliers[i];
            ItemStack chainmailBoots = new ItemStack(Material.CHAINMAIL_BOOTS);
            
            gui.setItem(slots[i], PaperItemBuilder.from(chainmailBoots)
                    .name(ChatUtils.color("<gold>Swim Speed Multiplier: " + multiplier))
                    .lore(ChatUtils.color("<gray>Klik om deze boots te maken"))
                    .asGuiItem(event -> {
                        Player player = (Player) event.getWhoClicked();
                        CustomBoots customBoots = new CustomBoots(bootType, effect, multiplier);
                        player.getInventory().addItem(customBoots.createBoots());
                        
                        // Create proper message with boot type and multiplier
                        String bootTypeName = "Blubboots " + multiplier;
                        player.sendMessage(ChatUtils.color("<green>Je hebt " + bootTypeName + " ontvangen!"));
                        player.closeInventory();
                    }));
        }
    }
    
    private void fillBorders() {
        ItemStack redPane = new ItemStack(Material.RED_STAINED_GLASS_PANE);
        
        // Fill all border slots
        for (int i = 0; i < 9; i++) {
            gui.setItem(i, PaperItemBuilder.from(redPane).asGuiItem(event -> {
                // Do nothing - prevent item giving
            })); // Top row
            gui.setItem(i + 18, PaperItemBuilder.from(redPane).asGuiItem(event -> {
                // Do nothing - prevent item giving
            })); // Bottom row
        }
        
        // Fill left and right columns
        for (int i = 9; i < 18; i += 9) {
            gui.setItem(i, PaperItemBuilder.from(redPane).asGuiItem(event -> {
                // Do nothing - prevent item giving
            })); // Left column
            gui.setItem(i + 8, PaperItemBuilder.from(redPane).asGuiItem(event -> {
                // Do nothing - prevent item giving
            })); // Right column
        }
    }
}
