package nl.openminetopia.utils.menu;

import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.ScrollingGui;
import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.utils.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ScrollingMenu {

    public final ScrollingGui gui;

    public ScrollingMenu(String title, int rows, int pageSize) {
        this.gui = Gui.scrolling()
                .title(ChatUtils.color(title))
                .rows(rows)
                .pageSize(pageSize)
                .create();
    }

    public ScrollingMenu(String title, int rows) {
        this.gui = Gui.scrolling()
                .title(ChatUtils.color(title))
                .rows(rows)
                .create();
    }

    public void open(Player player) {
        Bukkit.getScheduler().runTask(OpenMinetopia.getInstance(), () -> gui.open(player));
    }
}
