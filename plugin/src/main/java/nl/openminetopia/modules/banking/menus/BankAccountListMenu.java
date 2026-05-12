package nl.openminetopia.modules.banking.menus;

import dev.triumphteam.gui.guis.GuiItem;
import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.configuration.MessageConfiguration;
import nl.openminetopia.modules.banking.BankingModule;
import nl.openminetopia.modules.banking.models.BankAccountModel;
import nl.openminetopia.utils.item.ItemBuilder;
import nl.openminetopia.utils.menu.PaginatedMenu;

public class BankAccountListMenu extends PaginatedMenu {

    public BankAccountListMenu() {
        super(MessageConfiguration.message("banking_all_accounts"), 6);
        gui.disableAllInteractions();
        gui.setItem(51, this.nextPageItem());
        gui.setItem(47, this.previousPageItem());

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

            GuiItem accountItem = new GuiItem(accountStack.toItemStack());
            gui.addItem(accountItem);
        }
    }
}
