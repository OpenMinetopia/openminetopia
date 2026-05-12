package nl.openminetopia.modules.staff.admintool.listeners;

import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.api.player.PlayerManager;
import nl.openminetopia.api.player.objects.MinetopiaPlayer;
import nl.openminetopia.modules.banking.BankingModule;
import nl.openminetopia.modules.banking.models.BankAccountModel;
import nl.openminetopia.modules.staff.admintool.menus.AdminToolMenu;
import nl.openminetopia.utils.PersistentDataUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerEntityInteractListener implements Listener {

    @EventHandler
    public void playerInteract(PlayerInteractEntityEvent event) {
        ItemStack item = event.getPlayer().getInventory().getItemInMainHand();

        if (!(event.getRightClicked() instanceof Player target)) return;
        if (item.getType() != Material.NETHER_STAR) return;
        if (PersistentDataUtil.get(item, "openmt.admintool") == null) return;
        if (!event.getPlayer().hasPermission("openminetopia.admintool")) return;

        MinetopiaPlayer minetopiaPlayer = PlayerManager.getInstance().getOnlineMinetopiaPlayer(target);
        if (minetopiaPlayer == null) return;

        BankingModule bankingModule = OpenMinetopia.getModuleManager().get(BankingModule.class);
        BankAccountModel bankAccountModel = bankingModule.getAccountById(target.getUniqueId());

        new AdminToolMenu(event.getPlayer(), target, minetopiaPlayer, bankAccountModel).open(event.getPlayer());
    }
}
