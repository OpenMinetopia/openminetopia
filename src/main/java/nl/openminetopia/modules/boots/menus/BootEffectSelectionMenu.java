package nl.openminetopia.modules.boots.menus;

import dev.triumphteam.gui.builder.item.PaperItemBuilder;
import nl.openminetopia.modules.boots.enums.BootEffect;
import nl.openminetopia.modules.boots.enums.BootType;
import nl.openminetopia.utils.ChatUtils;
import nl.openminetopia.utils.menu.Menu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class BootEffectSelectionMenu extends Menu {

    public BootEffectSelectionMenu(BootType bootType) {
        super("<gold>Boots Effect Selectie - " + bootType.getDisplayName(), 3);
        
        // Disable all interactions to prevent items from being taken
        gui.disableAllInteractions();
        
        // Fill borders with red stained glass panes
        fillBorders();
        
        // Layout: 9-10: Red stained, 11: Potion of swiftness, 12: Glass, 13: Ice, 14: Glass, 15: Water bucket, 16-17: Glass
        
        // Define the effects and their corresponding slots
        BootEffect[] effects = {BootEffect.SPEED, BootEffect.ICE, BootEffect.BLUB};
        int[] slots = {11, 13, 15};
        
        // Slots 9-10: Red Stained Glass Panes
        gui.setItem(9, PaperItemBuilder.from(new ItemStack(Material.RED_STAINED_GLASS_PANE))
                .name(ChatUtils.color("<red>Glass"))
                .asGuiItem(event -> {
                    // Do nothing - just prevent item giving
                }));
        
        gui.setItem(10, PaperItemBuilder.from(new ItemStack(Material.RED_STAINED_GLASS_PANE))
                .name(ChatUtils.color("<red>Glass"))
                .asGuiItem(event -> {
                    // Do nothing - just prevent item giving
                }));
        
        // Slot 12: Red Stained Glass Pane
        gui.setItem(12, PaperItemBuilder.from(new ItemStack(Material.RED_STAINED_GLASS_PANE))
                .name(ChatUtils.color("<red>Glass"))
                .asGuiItem(event -> {
                    // Do nothing - just prevent item giving
                }));
        
        // Slot 14: Red Stained Glass Pane
        gui.setItem(14, PaperItemBuilder.from(new ItemStack(Material.RED_STAINED_GLASS_PANE))
                .name(ChatUtils.color("<red>Glass"))
                .asGuiItem(event -> {
                    // Do nothing - just prevent item giving
                }));
        
        // Slots 16-17: Red Stained Glass Panes
        gui.setItem(16, PaperItemBuilder.from(new ItemStack(Material.RED_STAINED_GLASS_PANE))
                .name(ChatUtils.color("<red>Glass"))
                .asGuiItem(event -> {
                    // Do nothing - just prevent item giving
                }));
        
        gui.setItem(17, PaperItemBuilder.from(new ItemStack(Material.RED_STAINED_GLASS_PANE))
                .name(ChatUtils.color("<red>Glass"))
                .asGuiItem(event -> {
                    // Do nothing - just prevent item giving
                }));
        
        // Add the boot effects dynamically
        for (int i = 0; i < effects.length; i++) {
            BootEffect effect = effects[i];
            
            gui.setItem(slots[i], PaperItemBuilder.from(effect.getIcon())
                    .name(ChatUtils.color("<gold>" + effect.getDisplayName()))
                    .lore(ChatUtils.color("<gray>" + effect.getDescription()))
                    .asGuiItem(event -> {
                        Player player = (Player) event.getWhoClicked();
                        new BootMultiplierSelectionMenu(bootType, effect).open(player);
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
