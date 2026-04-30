package nl.openminetopia.modules.backpack.menus;

import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.modules.backpack.item.BackpackItem;
import nl.openminetopia.modules.backpack.session.BackpackSession;
import nl.openminetopia.modules.backpack.session.BackpackSessionManager;
import nl.openminetopia.utils.menu.Menu;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class BackpackMenu extends Menu {

    private final BackpackSessionManager manager;
    private final BackpackSession session;
    private final ItemStack[] initialContents;

    public BackpackMenu(BackpackSessionManager manager, BackpackSession session, ItemStack[] contents) {
        super("<dark_gray>Rugzak", session.getRows());
        this.manager = manager;
        this.session = session;
        this.initialContents = contents;

        gui.enableAllInteractions();

        gui.setDefaultClickAction(event -> {
            ItemStack moving = event.getCurrentItem();
            ItemStack cursor = event.getCursor();

            if (BackpackItem.isBackpack(moving) || BackpackItem.isBackpack(cursor)) {
                event.setCancelled(true);
                return;
            }

            manager.scheduleSave(session);
        });

        gui.setDragAction(event -> {
            ItemStack dragged = event.getOldCursor();
            if (BackpackItem.isBackpack(dragged)) {
                event.setCancelled(true);
                return;
            }
            manager.scheduleSave(session);
        });

        gui.setCloseGuiAction(event -> {
            manager.saveSnapshot(session);
            manager.close(session);
        });
    }

    @Override
    public void open(Player player) {
        Bukkit.getScheduler().runTask(OpenMinetopia.getInstance(), () -> {
            gui.open(player);
            int size = gui.getInventory().getSize();
            for (int i = 0; i < initialContents.length && i < size; i++) {
                ItemStack stack = initialContents[i];
                if (stack == null) continue;
                gui.getInventory().setItem(i, stack);
            }
        });
    }
}