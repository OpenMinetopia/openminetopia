package nl.openminetopia.modules.staff.admintool.listeners;

import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.api.player.PlayerManager;
import nl.openminetopia.api.player.objects.MinetopiaPlayer;
import nl.openminetopia.modules.banking.BankingModule;
import nl.openminetopia.modules.banking.models.BankAccountModel;
import nl.openminetopia.modules.staff.admintool.menus.AdminToolMenu;
import nl.openminetopia.utils.PersistentDataUtil;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerInteractListener implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        if (item == null) return;

        if (item.getType() != Material.NETHER_STAR) return;
        if (event.getAction().isRightClick()) return;
        if (PersistentDataUtil.get(item, "openmt.admintool") == null) return;
        if (!event.getPlayer().hasPermission("openminetopia.admintool")) return;

        MinetopiaPlayer minetopiaPlayer = PlayerManager.getInstance().getOnlineMinetopiaPlayer(event.getPlayer());

        if (minetopiaPlayer == null) return;

        BankingModule bankingModule = OpenMinetopia.getModuleManager().get(BankingModule.class);
        BankAccountModel bankAccountModel = bankingModule.getAccountByIdAsync(event.getPlayer().getUniqueId()).join();

        new AdminToolMenu(event.getPlayer(), event.getPlayer(), minetopiaPlayer, bankAccountModel).open(event.getPlayer());
    }
}
