package nl.openminetopia.modules.banking.menus;

import dev.triumphteam.gui.guis.GuiItem;
import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.configuration.MessageConfiguration;
import nl.openminetopia.modules.banking.BankingModule;
import nl.openminetopia.modules.banking.enums.AccountType;
import nl.openminetopia.modules.banking.models.BankAccountModel;
import nl.openminetopia.utils.item.ItemBuilder;
import nl.openminetopia.utils.menu.Menu;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;

public class BankTypeSelectionMenu extends Menu {

    public BankTypeSelectionMenu(Player player) {
        super(MessageConfiguration.message("banking_select_account_type"), 3);

        gui.disableAllInteractions();

        for (AccountType type : AccountType.values()) {
            ItemStack iconStack = new ItemBuilder(type.getMaterial())
                    .setName(type.getName())
                    .toItemStack();

            gui.setItem(type.getSlot(), new GuiItem(iconStack, event -> {
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
