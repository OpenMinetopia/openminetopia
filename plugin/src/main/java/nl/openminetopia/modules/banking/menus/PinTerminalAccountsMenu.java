package nl.openminetopia.modules.banking.menus;

import dev.triumphteam.gui.guis.GuiItem;
import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.modules.banking.BankingModule;
import nl.openminetopia.modules.banking.enums.AccountType;
import nl.openminetopia.modules.banking.models.BankAccountModel;
import nl.openminetopia.utils.ChatUtils;
import nl.openminetopia.utils.item.ItemBuilder;
import nl.openminetopia.utils.menu.PaginatedMenu;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;

public class PinTerminalAccountsMenu extends PaginatedMenu {

    public PinTerminalAccountsMenu(Player player) {
        super("<gold>Selecteer een rekening", 6);

        gui.disableAllInteractions();
        BankingModule bankingModule = OpenMinetopia.getModuleManager().get(BankingModule.class);

        gui.setItem(6, 3, this.previousPageItem());
        gui.setItem(6, 7, this.nextPageItem());

        gui.setItem(6, 5, new GuiItem(new ItemBuilder(Material.OAK_SIGN)
                .setName(ChatUtils.color("<red>Annuleer"))
                .toItemStack(), event -> event.getWhoClicked().closeInventory()));

        Collection<BankAccountModel> accountModels = bankingModule.getAccountsFromPlayer(player.getUniqueId())
                .stream().filter(account -> account.getType() == AccountType.COMPANY || account.getType() == AccountType.GOVERNMENT)
                .toList();

        for (BankAccountModel accountModel : accountModels) {
            AccountType type = accountModel.getType();
            ItemBuilder accountBuilder = new ItemBuilder(type.getMaterial())
                    .setName(type.getColor() + accountModel.getName())
                    .addLoreLine("<dark_gray><i>" + type.getName())
                    .addLoreLine("")
                    .addLoreLine("<gray>Linker-muis om te selecteren.");

            ItemStack accountStack = accountBuilder.toItemStack();

            GuiItem accountItem = new GuiItem(accountStack, event -> {
                event.setCancelled(true);

                Player clicker = (Player) event.getWhoClicked();
                clicker.closeInventory();
                player.sendMessage(ChatUtils.color("<gold>Typ het bedrag in dat je wilt ontvangen"));
                OpenMinetopia.getChatInputHandler().waitForInput(clicker, amountInput -> {
                    double amount;
                    try {
                        amount = Double.parseDouble(amountInput);
                    } catch (NumberFormatException e) {
                        ChatUtils.sendMessage(player, "<red>Ongeldig bedrag ingevoerd. Probeer het opnieuw.");
                        return;
                    }

                    if (amount <= 0) {
                        ChatUtils.sendMessage(player, "<red>Het bedrag moet groter zijn dan nul. Probeer het opnieuw.");
                        return;
                    }

                    player.sendMessage(ChatUtils.color("<gold>Typ nu de naam van de speler naar wie je het verzoek wilt sturen"));
                    OpenMinetopia.getChatInputHandler().waitForInput(clicker, senderInput -> {
                        String senderName = senderInput.trim();
                        Player sender = Bukkit.getPlayer(senderName);
                        if (sender == null) {
                            ChatUtils.sendMessage(player, "<red>Speler niet gevonden. Probeer het opnieuw.");
                            return;
                        }
                        if (sender.getUniqueId().equals(clicker.getUniqueId())) {
                            ChatUtils.sendMessage(player, "<red>Je kunt geen pinverzoek naar jezelf sturen. Probeer het opnieuw.");
                            return;
                        }
                        bankingModule.getPinTerminalManager().startTransaction(sender, clicker, amount, accountModel);
                    });
                });
            });
            gui.addItem(accountItem);
        }
    }
}
