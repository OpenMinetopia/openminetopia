package nl.openminetopia.modules.banking.listeners;

import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.modules.banking.BankingModule;
import nl.openminetopia.modules.banking.configuration.BankingConfiguration;
import nl.openminetopia.modules.banking.enums.AccountType;
import nl.openminetopia.modules.banking.menus.PinTerminalAccountsMenu;
import nl.openminetopia.modules.banking.models.BankAccountModel;
import nl.openminetopia.modules.banking.models.PinTransaction;
import nl.openminetopia.utils.ChatUtils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.List;

public class PinTerminalInteractionListener implements Listener {

    @EventHandler
    public void pinTerminalInteraction(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;
        if (event.getClickedBlock() == null) return;

        Player player = event.getPlayer();
        Block block = event.getClickedBlock();

        Material material = block.getType();

        BankingModule bankingModule = OpenMinetopia.getModuleManager().get(BankingModule.class);
        BankingConfiguration bankingConfiguration = bankingModule.getConfiguration();

        if (!bankingConfiguration.getPinTerminalMaterials().contains(material)) return;
        event.setCancelled(true);
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (bankingModule.getPinTerminalManager().isSenderInTransaction(player)) {
                BankAccountModel senderAccount = bankingModule.getAccountById(player.getUniqueId());
                if (senderAccount == null) return;
                double amount = bankingModule.getPinTerminalManager().getPinTransactions().stream()
                        .filter(t -> t.sender().equals(player))
                        .mapToDouble(PinTransaction::amount)
                        .findFirst().orElse(0);
                if (amount < 0) return;
                PinTransaction transaction = bankingModule.getPinTerminalManager().getPinTransactions().stream()
                        .filter(t -> t.sender().equals(player))
                        .findFirst().orElse(null);
                if (transaction == null) return;
                if (senderAccount.getBalance() < amount) {
                    player.sendMessage(ChatUtils.color("<red>Je hebt niet genoeg saldo op je rekening om deze pintransactie te voltooien."));
                    bankingModule.getPinTerminalManager().cancelTransaction(transaction);
                    return;
                }
                bankingModule.getPinTerminalManager().completeTransaction(transaction);
                return;
            }
            if (bankingModule.getPinTerminalManager().isRecipientInTransaction(player)) {
                player.sendMessage(ChatUtils.color("<red>Je hebt al een lopende pintransactie. Voltooi deze eerst voordat je een nieuwe start."));
                return;
            }
            List<BankAccountModel> accounts = bankingModule.getAccountsFromPlayer(player.getUniqueId())
                    .stream()
                    .filter(account -> account.getType() == AccountType.COMPANY || account.getType() == AccountType.GOVERNMENT)
                    .toList();
            if (accounts.isEmpty()) {
                player.sendMessage(ChatUtils.color("<red>Je hebt geen zakelijke rekeningen om een pintransactie mee te doen."));
                return;
            }
            new PinTerminalAccountsMenu(player).open(player);
        }

        if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
            if (!bankingModule.getPinTerminalManager().isRecipientInTransaction(player)
                    && !bankingModule.getPinTerminalManager().isSenderInTransaction(player)) return;
            PinTransaction transaction = bankingModule.getPinTerminalManager().getPinTransactions().stream()
                    .filter(t -> t.recipient().equals(player) || t.sender().equals(player))
                    .findFirst().orElse(null);
            if (transaction == null) return;
            bankingModule.getPinTerminalManager().cancelTransaction(transaction);
        }
    }
}
