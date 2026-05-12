package nl.openminetopia.modules.banking.menus;

import dev.triumphteam.gui.guis.GuiItem;
import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.configuration.MessageConfiguration;
import nl.openminetopia.modules.banking.BankingModule;
import nl.openminetopia.modules.banking.models.BankAccountModel;
import nl.openminetopia.modules.transactions.TransactionsModule;
import nl.openminetopia.modules.transactions.enums.TransactionType;
import nl.openminetopia.modules.transactions.objects.TransactionModel;
import nl.openminetopia.utils.ChatUtils;
import nl.openminetopia.utils.PersistentDataUtil;
import nl.openminetopia.utils.item.ItemBuilder;
import nl.openminetopia.utils.menu.PaginatedMenu;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class BankTransactionsMenu extends PaginatedMenu {

    private final BankingModule bankingModule = OpenMinetopia.getModuleManager().get(BankingModule.class);
    private final SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    private final BankAccountModel accountModel;

    public BankTransactionsMenu(Player player, BankAccountModel accountModel) {
        super(accountModel.getType().getColor() + accountModel.getName() + " <reset>| <red>Transactions", 4, 27);
        this.accountModel = accountModel;
        gui.disableAllInteractions();
        gui.setItem(29, this.previousPageItem());
        gui.setItem(33, this.nextPageItem());

        gui.setItem(13, new GuiItem(new ItemBuilder(Material.IRON_BLOCK).setName("<red>Transacties inladen.").toItemStack()));
        createBackButton();

        TransactionsModule transactionsModule = OpenMinetopia.getModuleManager().get(TransactionsModule.class);
        transactionsModule.getAccountTransactions(TransactionsModule.LookupType.BANK_ACCOUNT, accountModel.getUniqueId()).whenComplete(((transactionModels, throwable) -> {
            if (throwable != null) {
                player.closeInventory();
                ChatUtils.sendMessage(player, "<red>Er is iets misgegaan.");
                return;
            }

            player.sendMessage(ChatUtils.color("<gold>Er zijn in totaal <red>" + transactionModels.size() + " <gold>transacties ingeladen."));
            gui.clearPageItems();

            List<TransactionModel> sortedTransactions = transactionModels.stream()
                    .sorted(Comparator.comparing(TransactionModel::getTime).reversed())
                    .toList();

            for (TransactionModel sortedTransaction : sortedTransactions) {
                TransactionType type = sortedTransaction.getType();
                ItemBuilder transactionBuilder = new ItemBuilder(type.getMaterial());
                transactionBuilder.setName("<yellow>" + type.getName());
                transactionBuilder.addLoreLine("");
                transactionBuilder.addLoreLine("<gold>Datum: <red>" + format.format(new Date(sortedTransaction.getTime())));
                transactionBuilder.addLoreLine("<gold>Bedrag: <red>" + bankingModule.format(sortedTransaction.getAmount()));
                transactionBuilder.addLoreLine("<gold>Door: <red>" + sortedTransaction.getUsername());
                gui.addItem(new GuiItem(PersistentDataUtil.set(transactionBuilder.toItemStack(), sortedTransaction.getTime(), "time")));
            }

            createBackButton();
            gui.update();
        }));
    }

    private void createBackButton() {
        gui.setItem(31, new GuiItem(new ItemBuilder(Material.OAK_SIGN)
                .setName(MessageConfiguration.message("go_back"))
                .toItemStack(), event -> {
            event.setCancelled(true);
            Player player = (Player) event.getWhoClicked();
            new BankAccountSelectionMenu(player, accountModel.getType()).open(player);
        }));
    }
}
