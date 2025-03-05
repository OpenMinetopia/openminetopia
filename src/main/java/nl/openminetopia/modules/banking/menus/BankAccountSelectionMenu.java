package nl.openminetopia.modules.banking.menus;

import com.jazzkuh.inventorylib.objects.PaginatedMenu;
import com.jazzkuh.inventorylib.objects.icon.Icon;
import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.configuration.MessageConfiguration;
import nl.openminetopia.modules.banking.BankingModule;
import nl.openminetopia.modules.banking.enums.AccountType;
import nl.openminetopia.modules.banking.models.BankAccountModel;
import nl.openminetopia.utils.ChatUtils;
import nl.openminetopia.utils.item.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;

public class BankAccountSelectionMenu extends PaginatedMenu {

    public BankAccountSelectionMenu(Player player, AccountType type) {
        super(ChatUtils.color(type.getColor() + type.getName()), 4);
        this.registerPageSlotsBetween(0, 27);
        BankingModule bankingModule = OpenMinetopia.getModuleManager().get(BankingModule.class);

        this.addSpecialIcon(new Icon(31, new ItemBuilder(Material.OAK_SIGN)
                .setName(MessageConfiguration.message("go_back"))
                .toItemStack(),
                event -> {
                    event.setCancelled(true);
                    new BankTypeSelectionMenu(player).open(player);
                }
        ));

        Collection<BankAccountModel> accountModels = bankingModule.getAccountsFromPlayer(player.getUniqueId())
                .stream().filter(account -> account.getType() == type)
                .toList();

        for (BankAccountModel accountModel : accountModels) {
            ItemStack accountStack = new ItemBuilder(type.getMaterial())
                    .setName(type.getColor() + accountModel.getName())
                    .addLoreLine("<dark_gray><i>" + type.getName())
                    .addLoreLine("")
                    .addLoreLine("<gray>Klik om te bekijken.")
                    .toItemStack();

            Icon accountIcon = new Icon(accountStack, event -> {
                event.setCancelled(true);
                new BankContentsMenu(player, accountModel, false).open(player);
            });

            this.addItem(accountIcon);
        }


    }

    @Override
    public Icon getPreviousPageItem() {
        ItemStack previousStack = new ItemBuilder(Material.ARROW)
                .setName(MessageConfiguration.message("previous_page"))
                .toItemStack();
        return new Icon(29, previousStack, e -> e.setCancelled(true));
    }

    @Override
    public Icon getNextPageItem() {
        ItemStack previousStack = new ItemBuilder(Material.ARROW)
                .setName(MessageConfiguration.message("next_page"))
                .toItemStack();
        return new Icon(33, previousStack, e -> e.setCancelled(true));
    }
}
