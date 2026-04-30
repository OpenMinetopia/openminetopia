package nl.openminetopia.modules.items.menus;

import dev.triumphteam.gui.builder.item.PaperItemBuilder;
import dev.triumphteam.gui.guis.GuiItem;
import nl.openminetopia.configuration.MessageConfiguration;
import nl.openminetopia.modules.items.configuration.objects.ItemCategory;
import nl.openminetopia.utils.ChatUtils;
import nl.openminetopia.utils.menu.PaginatedMenu;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class ItemsMenu extends PaginatedMenu {
    public ItemsMenu(ItemCategory category) {
        super("<gold>" + category.name() + " Items", 6, 45);
        gui.disableAllInteractions();
        gui.setItem(47, this.previousPageItem());
        gui.setItem(51, this.nextPageItem());

        category.items().forEach(item -> {
            GuiItem itemBuilder = PaperItemBuilder.from(item.build().clone())
                    .name(ChatUtils.color("<gold>" + item.name()))
                    .asGuiItem(event -> {
                Player player = (Player) event.getWhoClicked();
                player.getInventory().addItem(item.build());
            });
            gui.addItem(itemBuilder);
        });

        gui.setItem(49, PaperItemBuilder.from(Material.LADDER).name(MessageConfiguration.component("go_back")).asGuiItem(event -> {
            new ItemCategoriesMenu().open((Player) event.getWhoClicked());
        }));
    }
}
