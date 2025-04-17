package nl.openminetopia.modules.pin.menu;

import com.jazzkuh.inventorylib.objects.PaginatedMenu;
import com.jazzkuh.inventorylib.objects.icon.Icon;
import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.configuration.MessageConfiguration;
import nl.openminetopia.modules.banking.BankingModule;
import nl.openminetopia.modules.banking.enums.AccountType;
import nl.openminetopia.modules.banking.models.BankAccountModel;
import nl.openminetopia.modules.pin.PinModule;
import nl.openminetopia.modules.pin.enums.PinState;
import nl.openminetopia.modules.pin.objects.PinSession;
import nl.openminetopia.utils.ChatUtils;
import nl.openminetopia.utils.item.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;

public class PinSelectAccountMenu extends PaginatedMenu {

    private final BankingModule bankingModule = OpenMinetopia.getModuleManager().get(BankingModule.class);
    private final PinModule pinModule = OpenMinetopia.getModuleManager().get(PinModule.class);

    private final PinSession session;

    public PinSelectAccountMenu(Player player, PinSession session) {
        super(ChatUtils.color("Selecteer rekening."), 4);
        this.registerPageSlotsBetween(0, 27);
        this.session = session;

        Collection<BankAccountModel> accountModels = bankingModule.getAccountsFromPlayer(player.getUniqueId());

        for (BankAccountModel accountModel : accountModels) {
            AccountType type = accountModel.getType();

            ItemBuilder accountBuilder = new ItemBuilder(type.getMaterial())
                    .setName(type.getColor() + accountModel.getName())
                    .addLoreLine("<dark_gray><i>" + type.getName())
                    .addLoreLine("")
                    .addLoreLine("<gray>Linker-muis om te selecteren.");

            addItem(new Icon(accountBuilder.toItemStack(), true, (event) -> {
                session.setAccount(accountModel);
                session.setState(PinState.READY);

                Player target = session.getTarget();
                if (!target.isOnline()) {
                    ChatUtils.sendMessage(player, "<red>De betaalverzoek is geannuleerd omdat de speler niet langer online is.");
                    pinModule.getPinSessions().remove(session);
                    return;
                }

                target.sendMessage(ChatUtils.color("<gold>Je hebt een betaalverzoek ontvangen van <red>" + player.getName() + " <gold>voor <red>" + bankingModule.format(session.getAmount()) + "<gold>, klik op de pin console met je bankpas om deze te <red>accepteren<gold.."));
                player.sendMessage(ChatUtils.color("<gold>Je hebt je betaalverzoek verstuurd naar <red>" + player.getName() + " <gold>voor <red>" + bankingModule.format(session.getAmount()) + "<gold>."));
                player.closeInventory();
            }));
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

    @Override
    public void onClose(InventoryCloseEvent event) {
        if (session.getState() == PinState.READY) return;

        pinModule.getPinSessions().remove(session);
        player.sendMessage(ChatUtils.color("<red>Pin sessie geannuleerd."));
    }
}
