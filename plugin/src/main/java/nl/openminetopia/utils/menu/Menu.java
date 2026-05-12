package nl.openminetopia.utils.menu;

import dev.triumphteam.gui.guis.Gui;
import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.utils.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Menu {

    public final Gui gui;

    public Menu(String title, int rows) {
        this.gui = Gui.gui()
                .title(ChatUtils.color(title))
                .rows(rows)
                .create();
    }

    public void open(Player player) {
        Bukkit.getScheduler().runTask(OpenMinetopia.getInstance(), () -> gui.open(player));
    }
}
