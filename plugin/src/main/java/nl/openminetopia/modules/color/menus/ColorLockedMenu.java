package nl.openminetopia.modules.color.menus;

import dev.triumphteam.gui.guis.GuiItem;
import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.configuration.MessageConfiguration;
import nl.openminetopia.modules.color.ColorModule;
import nl.openminetopia.modules.color.configuration.components.ColorComponent;
import nl.openminetopia.utils.item.ItemBuilder;
import nl.openminetopia.utils.menu.PaginatedMenu;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;

public class ColorLockedMenu extends PaginatedMenu {

    public ColorLockedMenu(Player player, ColorSelectMenu oldMenu) {
        super(oldMenu.getType().getDisplayName() + " <reset><dark_gray>locked menu", 6);

        gui.disableAllInteractions();
        gui.setItem(53, this.nextPageItem());
        gui.setItem(45, this.previousPageItem());

        ColorModule colorModule = OpenMinetopia.getModuleManager().get(ColorModule.class);
        List<ColorComponent> lockedColors = colorModule.getConfiguration().lockedColors(oldMenu.getColors());
        lockedColors.forEach(component -> {
            gui.addItem(new GuiItem(new ItemBuilder(Material.IRON_INGOT).setName(component.displayName()).toItemStack()));
        });

        gui.setItem(49, new GuiItem(new ItemBuilder(Material.LADDER).setName(MessageConfiguration.message("go_back")).toItemStack(),
                e -> oldMenu.open(player)));

    }
}
