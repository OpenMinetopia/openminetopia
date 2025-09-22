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
        
        // Layout: Leather -> Glass -> Chainmail -> Glass -> Iron -> Glass -> Gold -> Glass -> Diamond
        // Middle row positions (row 2, slots 9-17)
        
        // Set up the middle row items
        // Slot 9: Leather Boots
        gui.setItem(9, PaperItemBuilder.from(new ItemStack(Material.LEATHER_BOOTS))
                .name(ChatUtils.color("<gold>" + BootType.LEATHER.getDisplayName()))
                .lore(ChatUtils.color("<gray>Klik om dit bootstype te selecteren"))
                .asGuiItem(event -> {
                    Player player = (Player) event.getWhoClicked();
                    new BootEffectSelectionMenu(BootType.LEATHER).open(player);
                }));
        
        // Slot 10: Red Stained Glass Pane
        gui.setItem(10, PaperItemBuilder.from(new ItemStack(Material.RED_STAINED_GLASS_PANE))
                .name(ChatUtils.color("<red>Glass"))
                .asGuiItem(event -> {
                    // Do nothing - just prevent item giving
                }));
        
        // Slot 11: Chainmail Boots
        gui.setItem(11, PaperItemBuilder.from(new ItemStack(Material.CHAINMAIL_BOOTS))
                .name(ChatUtils.color("<gold>" + BootType.CHAINMAIL.getDisplayName()))
                .lore(ChatUtils.color("<gray>Klik om dit bootstype te selecteren"))
                .asGuiItem(event -> {
                    Player player = (Player) event.getWhoClicked();
                    new BootEffectSelectionMenu(BootType.CHAINMAIL).open(player);
                }));
        
        // Slot 12: Red Stained Glass Pane
        gui.setItem(12, PaperItemBuilder.from(new ItemStack(Material.RED_STAINED_GLASS_PANE))
                .name(ChatUtils.color("<red>Glass"))
                .asGuiItem(event -> {
                    // Do nothing - just prevent item giving
                }));
        
        // Slot 13: Iron Boots
        gui.setItem(13, PaperItemBuilder.from(new ItemStack(Material.IRON_BOOTS))
                .name(ChatUtils.color("<gold>" + BootType.IRON.getDisplayName()))
                .lore(ChatUtils.color("<gray>Klik om dit bootstype te selecteren"))
                .asGuiItem(event -> {
                    Player player = (Player) event.getWhoClicked();
                    new BootEffectSelectionMenu(BootType.IRON).open(player);
                }));
        
        // Slot 14: Red Stained Glass Pane
        gui.setItem(14, PaperItemBuilder.from(new ItemStack(Material.RED_STAINED_GLASS_PANE))
                .name(ChatUtils.color("<red>Glass"))
                .asGuiItem(event -> {
                    // Do nothing - just prevent item giving
                }));
        
        // Slot 15: Gold Boots
        gui.setItem(15, PaperItemBuilder.from(new ItemStack(Material.GOLDEN_BOOTS))
                .name(ChatUtils.color("<gold>" + BootType.GOLD.getDisplayName()))
                .lore(ChatUtils.color("<gray>Klik om dit bootstype te selecteren"))
                .asGuiItem(event -> {
                    Player player = (Player) event.getWhoClicked();
                    new BootEffectSelectionMenu(BootType.GOLD).open(player);
                }));
        
        // Slot 16: Red Stained Glass Pane
        gui.setItem(16, PaperItemBuilder.from(new ItemStack(Material.RED_STAINED_GLASS_PANE))
                .name(ChatUtils.color("<red>Glass"))
                .asGuiItem(event -> {
                    // Do nothing - just prevent item giving
                }));
        
        // Slot 17: Diamond Boots
        gui.setItem(17, PaperItemBuilder.from(new ItemStack(Material.DIAMOND_BOOTS))
                .name(ChatUtils.color("<gold>" + BootType.DIAMOND.getDisplayName()))
                .lore(ChatUtils.color("<gray>Klik om dit bootstype te selecteren"))
                .asGuiItem(event -> {
                    Player player = (Player) event.getWhoClicked();
                    new BootEffectSelectionMenu(BootType.DIAMOND).open(player);
                }));
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
