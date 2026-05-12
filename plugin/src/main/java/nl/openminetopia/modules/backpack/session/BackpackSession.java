package nl.openminetopia.modules.backpack.session;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import nl.openminetopia.modules.backpack.menus.BackpackMenu;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class BackpackSession {

    private final UUID viewer;
    private final UUID backpackId;
    private final ItemStack stackRef;
    private final int rows;
    private BackpackMenu menu;

    public void attachMenu(BackpackMenu menu) {
        this.menu = menu;
    }
}