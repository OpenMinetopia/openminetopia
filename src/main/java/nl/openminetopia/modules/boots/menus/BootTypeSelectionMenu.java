package nl.openminetopia.modules.boots.menus;

import dev.triumphteam.gui.builder.item.PaperItemBuilder;
import nl.openminetopia.modules.boots.enums.BootType;
import nl.openminetopia.utils.ChatUtils;
import nl.openminetopia.utils.menu.Menu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class BootTypeSelectionMenu extends Menu {

    public BootTypeSelectionMenu() {
        super("<gold>Boots Type Selectie", 3);
        
        // Disable all interactions to prevent items from being taken
        gui.disableAllInteractions();
        
        // Fill borders with red stained glass panes
        fillBorders();
        
        // Layout: Red stained -> Leather -> Red stained -> Chainmail -> Red stained -> Iron -> Red stained -> Gold -> Red stained -> Diamond
        int[] slots = {10, 11, 12, 13, 14, 15, 16, 17, 18, 19}; // All middle row positions
        Material[] materials = {Material.RED_STAINED_GLASS_PANE, Material.LEATHER_BOOTS, Material.RED_STAINED_GLASS_PANE, Material.CHAINMAIL_BOOTS,
                               Material.RED_STAINED_GLASS_PANE, Material.IRON_BOOTS, Material.RED_STAINED_GLASS_PANE, Material.GOLDEN_BOOTS, 
                               Material.RED_STAINED_GLASS_PANE, Material.DIAMOND_BOOTS};
        BootType[] bootTypes = {null, BootType.LEATHER, null, null, null, BootType.IRON, null, BootType.GOLD, null, BootType.DIAMOND};
        
        for (int i = 0; i < slots.length; i++) {
            Material material = materials[i];
            BootType bootType = bootTypes[i];
            
            if (bootType != null) {
                // This is a selectable boot type
                ItemStack bootItem = new ItemStack(material);
                gui.setItem(slots[i], PaperItemBuilder.from(bootItem)
                        .name(ChatUtils.color("<gold>" + bootType.getDisplayName()))
                        .lore(ChatUtils.color("<gray>Klik om dit bootstype te selecteren"))
                        .asGuiItem(event -> {
                            Player player = (Player) event.getWhoClicked();
                            new BootEffectSelectionMenu(bootType).open(player);
                        }));
            } else {
                // This is a red stained glass pane
                gui.setItem(slots[i], PaperItemBuilder.from(new ItemStack(material))
                        .name(ChatUtils.color("<red>"))
                        .asGuiItem(event -> {
                            // Do nothing - just prevent item giving
                        }));
            }
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
