package nl.openminetopia.modules.banking.menus;

import com.jazzkuh.inventorylib.objects.Menu;
import com.jazzkuh.inventorylib.objects.icon.Icon;
import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.configuration.MessageConfiguration;
import nl.openminetopia.modules.banking.BankingModule;
import nl.openminetopia.modules.banking.enums.AccountType;
import nl.openminetopia.modules.banking.models.BankAccountModel;
import nl.openminetopia.utils.item.ItemBuilder;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;

public class BankTypeSelectionMenu extends Menu {

    public BankTypeSelectionMenu(Player player) {
        super(MessageConfiguration.component("banking_select_account_type"), 3);

        for (AccountType type : AccountType.values()) {
            ItemStack iconStack = new ItemBuilder(type.getMaterial())
                    .setName(type.getName())
                    .toItemStack();

            this.addItem(new Icon(type.getSlot(), iconStack, true, (event) -> {
                event.setCancelled(true);

                BankingModule bankingModule = OpenMinetopia.getModuleManager().get(BankingModule.class);
                Collection<BankAccountModel> accountModels = bankingModule.getAccountsFromPlayer(player.getUniqueId())
                        .stream().filter(account -> account.getType() == type)
                        .toList();

                if (accountModels.isEmpty()) {
                    player.sendMessage(MessageConfiguration.component("banking_no_accounts_in_category"));
                    return;
                }

                new BankAccountSelectionMenu(player, type).open(player);
            }));
        }

    }
}
