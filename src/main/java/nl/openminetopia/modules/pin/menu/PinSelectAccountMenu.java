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
        super(ChatUtils.color("Selecteer rekening."));
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
                    ChatUtils.sendMessage(player, "speler is niet langer online.");
                    return;
                }

                target.sendMessage(ChatUtils.color("Je hebt een betaalverzoek ontvangen van " + player.getName() + " voor " + session.getAmount() + ", type 'ja' om te accepteren of 'annuleren' om het verzoek te weigeren"));
            }));
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

    @Override
    public void onClose(InventoryCloseEvent event) {
        pinModule.getPinSessions().remove(session);
        player.sendMessage(ChatUtils.color("Geannuleerd."));
    }
}
