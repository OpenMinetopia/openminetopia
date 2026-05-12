package nl.openminetopia.modules.color.menus;

import dev.triumphteam.gui.guis.GuiItem;
import nl.openminetopia.api.player.objects.MinetopiaPlayer;
import nl.openminetopia.modules.color.enums.OwnableColorType;
import nl.openminetopia.utils.item.ItemBuilder;
import nl.openminetopia.utils.menu.Menu;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class ColorTypeMenu extends Menu {
    public ColorTypeMenu(Player player, OfflinePlayer target, MinetopiaPlayer minetopiaPlayer) {
        super("<black>Kies een kleurtype", 3);

        gui.disableAllInteractions();

        gui.setItem(10, new GuiItem(new ItemBuilder(Material.NAME_TAG).setName(OwnableColorType.PREFIX.getDisplayName()).toItemStack(),
                e -> new ColorSelectMenu(player, target, minetopiaPlayer, OwnableColorType.PREFIX).open(player)));

        gui.setItem(12, new GuiItem(new ItemBuilder(Material.WRITABLE_BOOK).setName(OwnableColorType.CHAT.getDisplayName()).toItemStack(),
                e -> new ColorSelectMenu(player, target, minetopiaPlayer, OwnableColorType.CHAT).open(player)));

        gui.setItem(14, new GuiItem(new ItemBuilder(Material.OAK_HANGING_SIGN).setName(OwnableColorType.NAME.getDisplayName()).toItemStack(),
                e -> new ColorSelectMenu(player, target, minetopiaPlayer, OwnableColorType.NAME).open(player)));

        gui.setItem(16, new GuiItem(new ItemBuilder(Material.EXPERIENCE_BOTTLE).setName(OwnableColorType.LEVEL.getDisplayName()).toItemStack(),
                e -> new ColorSelectMenu(player, target, minetopiaPlayer, OwnableColorType.LEVEL).open(player)));
    }
}
