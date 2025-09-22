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
        
        // Fill borders with red stained glass panes
        fillBorders();
        
        // Add boot types in the middle row
        int[] slots = {10, 12, 14, 16}; // Middle row positions
        BootType[] bootTypes = {BootType.LEATHER, BootType.IRON, BootType.GOLD, BootType.DIAMOND};
        
        for (int i = 0; i < bootTypes.length; i++) {
            BootType bootType = bootTypes[i];
            ItemStack bootItem = new ItemStack(bootType.getMaterial());
            
            gui.setItem(slots[i], PaperItemBuilder.from(bootItem)
                    .name(ChatUtils.color("<gold>" + bootType.getDisplayName()))
                    .lore(ChatUtils.color("<gray>Klik om dit bootstype te selecteren"))
                    .asGuiItem(event -> {
                        Player player = (Player) event.getWhoClicked();
                        new BootEffectSelectionMenu(bootType).open(player);
                    }));
        }
    }
    
    private void fillBorders() {
        ItemStack redPane = new ItemStack(Material.RED_STAINED_GLASS_PANE);
        
        // Fill all border slots
        for (int i = 0; i < 9; i++) {
            gui.setItem(i, PaperItemBuilder.from(redPane).asGuiItem()); // Top row
            gui.setItem(i + 18, PaperItemBuilder.from(redPane).asGuiItem()); // Bottom row
        }
        
        // Fill left and right columns
        for (int i = 9; i < 18; i += 9) {
            gui.setItem(i, PaperItemBuilder.from(redPane).asGuiItem()); // Left column
            gui.setItem(i + 8, PaperItemBuilder.from(redPane).asGuiItem()); // Right column
        }
    }
}
