package nl.openminetopia.modules.banking.menus;

import com.jazzkuh.inventorylib.objects.PaginatedMenu;
import com.jazzkuh.inventorylib.objects.icon.Icon;
import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.configuration.MessageConfiguration;
import nl.openminetopia.modules.banking.BankingModule;
import nl.openminetopia.modules.banking.models.BankAccountModel;
import nl.openminetopia.utils.item.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class BankAccountListMenu extends PaginatedMenu {

    public BankAccountListMenu() {
        super(MessageConfiguration.component("banking_all_accounts"), 6);
        this.registerPageSlotsBetween(0, 45);

        BankingModule bankingModule = OpenMinetopia.getModuleManager().get(BankingModule.class);

        for (BankAccountModel accountModel : bankingModule.getBankAccountModels()) {
            ItemBuilder accountStack = new ItemBuilder(accountModel.getType().getMaterial())
                    .setName(accountModel.getType().getColor() + accountModel.getName())
                    .addLoreLine("<dark_gray><i>" + accountModel.getType().getName())
                    .addLoreLine("")
                    .addLoreLine("<gray>Account Permissions:");

            accountModel.getUsers().forEach((user, permission) -> {
                accountStack.addLoreLine(" - <gray>" + user.toString() + " - " + permission);
                // todo: fix username here, maybe save it in the permission table? Offline players have a performance impact.
            });

            Icon accountIcon = new Icon(accountStack.toItemStack(), event -> {
                event.setCancelled(true);
            });

            this.addItem(accountIcon);
        }

    }

    @Override
    public Icon getPreviousPageItem() {
        ItemStack previousStack = new ItemBuilder(Material.ARROW)
                .setName(MessageConfiguration.message("previous_page"))
                .toItemStack();
        return new Icon(47, previousStack, e -> e.setCancelled(true));
    }

    @Override
    public Icon getNextPageItem() {
        ItemStack previousStack = new ItemBuilder(Material.ARROW)
                .setName(MessageConfiguration.message("next_page"))
                .toItemStack();
        return new Icon(51, previousStack, e -> e.setCancelled(true));
    }
}
