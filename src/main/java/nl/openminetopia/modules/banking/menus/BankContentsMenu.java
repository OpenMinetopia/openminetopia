package nl.openminetopia.modules.banking.menus;

import com.jazzkuh.inventorylib.objects.Menu;
import com.jazzkuh.inventorylib.objects.icon.Icon;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.api.player.PlayerManager;
import nl.openminetopia.api.player.objects.MinetopiaPlayer;
import nl.openminetopia.configuration.MessageConfiguration;
import nl.openminetopia.modules.banking.BankingModule;
import nl.openminetopia.modules.banking.enums.AccountPermission;
import nl.openminetopia.modules.banking.models.BankAccountModel;
import nl.openminetopia.utils.ChatUtils;
import nl.openminetopia.utils.PersistentDataUtil;
import nl.openminetopia.utils.item.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

@Getter
public class BankContentsMenu extends Menu {

    private static final BankingModule bankingModule = OpenMinetopia.getModuleManager().get(BankingModule.class);

    private final Player player;
    private final BankAccountModel accountModel;
    private final boolean asAdmin;

    public BankContentsMenu(Player player, BankAccountModel accountModel, boolean asAdmin) {
        super(ChatUtils.color(accountModel.getType().getColor() + accountModel.getName() + "<reset> | <red>" + OpenMinetopia.getModuleManager().get(BankingModule.class).format(accountModel.getBalance())), 6);
        this.player = player;
        this.accountModel = accountModel;
        this.asAdmin = asAdmin;

        List<BankNote> bankNotes = OpenMinetopia.getBankingConfiguration().getBankNotes();

        for (int i = 36; i < 45; i++) {
            this.addItem(new Icon(i, new ItemBuilder(Material.PURPLE_STAINED_GLASS_PANE).toItemStack()));
        }

        int i = 45;
        for (BankNote bankNote : bankNotes) {
            this.addItem(new Icon(i, bankNote.toMenuItem(1), true, event -> withdrawMoney(bankNote, 1)));
            i++;
        }

        double remainingBalance = accountModel.getBalance();
        int slot = 0;

        for (BankNote bankNote : bankNotes) {
            int stackCount = (int) (remainingBalance / (bankNote.getValue() * 64));

            if (stackCount > 0) {
                for (int j = 0; j < stackCount; j++) {
                    this.addItem(new Icon(slot, bankNote.toMenuItem(64), true, event -> withdrawMoney(bankNote, 64)));
                    slot++;
                    if (slot >= 36) {
                        break;
                    }
                }

                remainingBalance -= stackCount * 64 * bankNote.getValue();
            }

            int remainingItems = (int) (remainingBalance / bankNote.getValue());
            if (remainingItems > 0 && slot < 36) {
                this.addItem(new Icon(slot, bankNote.toMenuItem(remainingItems), true, event -> withdrawMoney(bankNote, remainingItems)));
                remainingBalance -= remainingItems * bankNote.getValue();
                slot++;
            }

            if (slot >= 36) {
                break;
            }
        }

    }

    @Override
    @SuppressWarnings("all") // warning check for nothing
    public void onClick(InventoryClickEvent event) {
        if (event.getCurrentItem() == null) return;
        if (event.getCurrentItem().getType() == Material.AIR) return;
        ItemStack item = event.getCurrentItem();

        if (!PersistentDataUtil.contains(item, "bank_note_value")) return;
        if (PersistentDataUtil.getDouble(item, "bank_note_value") == null) return;

        MinetopiaPlayer minetopiaPlayer = PlayerManager.getInstance().getOnlineMinetopiaPlayer(player);

        if (!isAsAdmin() && !accountModel.hasPermission(player.getUniqueId(), AccountPermission.DEPOSIT)) {
            ChatUtils.sendFormattedMessage(minetopiaPlayer, MessageConfiguration.message("banking_no_deposit_permission"));
            return;
        }

        double noteValue = PersistentDataUtil.getDouble(item, "bank_note_value");
        double totalValue = noteValue * item.getAmount();

        item.setAmount(0);
        accountModel.setBalance(accountModel.getBalance() + totalValue);
        ChatUtils.sendFormattedMessage(minetopiaPlayer, MessageConfiguration.message("banking_deposit_message")
                .replace("<deposit_value>", bankingModule.format(totalValue)));
        new BankContentsMenu(player, accountModel, isAsAdmin()).open(player);
    }

    private void withdrawMoney(BankNote note, int amount) {
        double balance = accountModel.getBalance();
        double totalValue = note.getValue() * amount;

        MinetopiaPlayer minetopiaPlayer = PlayerManager.getInstance().getOnlineMinetopiaPlayer(player);

        if (balance < totalValue) {
            ChatUtils.sendFormattedMessage(minetopiaPlayer, MessageConfiguration.message("banking_not_enough_money"));
            return;
        }

        if (!isAsAdmin() && !accountModel.hasPermission(player.getUniqueId(), AccountPermission.WITHDRAW)) {
            ChatUtils.sendFormattedMessage(minetopiaPlayer, MessageConfiguration.message("banking_no_withdraw_permission"));
            return;
        }

        accountModel.setBalance(balance - totalValue);

        player.getInventory().addItem(note.toNote(amount));
        ChatUtils.sendFormattedMessage(minetopiaPlayer, MessageConfiguration.message("banking_withdraw_message")
                .replace("<withdraw_value>", bankingModule.format(totalValue)));
        new BankContentsMenu(player, accountModel, isAsAdmin()).open(player);
    }

    @Getter
    @RequiredArgsConstructor
    public static final class BankNote {
        private final Material material;
        private final double value;

        private ItemStack toMenuItem(int amount) {
            return new ItemBuilder(material, amount)
                    .setName("<gold>" + bankingModule.format(amount * value))
                    .addLoreLine("<yellow>Klik om op te nemen.")
                    .toItemStack();
        }

        private ItemStack toNote(int amount) {
            return new ItemBuilder(material, amount)
                    .setName("<gold>" + bankingModule.format(value))
                    .addLoreLine(MessageConfiguration.component("banking_note_lore1"))
                    .addLoreLine(MessageConfiguration.component("banking_note_lore2"))
                    .setNBT("bank_note_value", value)
                    .toItemStack();
        }
    }
}
