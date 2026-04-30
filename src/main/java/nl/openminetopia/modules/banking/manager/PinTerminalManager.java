package nl.openminetopia.modules.banking.manager;

import lombok.Getter;
import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.api.player.PlayerManager;
import nl.openminetopia.api.player.objects.MinetopiaPlayer;
import nl.openminetopia.configuration.MessageConfiguration;
import nl.openminetopia.modules.banking.BankingModule;
import nl.openminetopia.modules.banking.models.BankAccountModel;
import nl.openminetopia.modules.banking.models.PinTransaction;
import nl.openminetopia.utils.ChatUtils;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@Getter
public class PinTerminalManager {

    private final BankingModule bankingModule = OpenMinetopia.getModuleManager().get(BankingModule.class);
    private final List<PinTransaction> pinTransactions = new ArrayList<>();

    public PinTransaction startTransaction(Player sender, Player recipient, double amount, BankAccountModel recipientAccount) {
        sender.sendMessage(ChatUtils.color(MessageConfiguration.message("banking_pin_request_received")
                .replace("<player>", recipient.getName())
                .replace("<amount>", bankingModule.format(amount))));
        sender.sendMessage(MessageConfiguration.component("banking_pin_request_instructions"));

        recipient.sendMessage(ChatUtils.color(MessageConfiguration.message("banking_pin_request_sent")
                .replace("<player>", sender.getName())
                .replace("<amount>", bankingModule.format(amount))));

        PinTransaction transaction = new PinTransaction(sender, recipient, amount, recipientAccount);
        pinTransactions.add(transaction);
        return transaction;
    }

    public void cancelTransaction(PinTransaction transaction) {
        transaction.sender().sendMessage(MessageConfiguration.component("banking_pin_cancelled"));
        transaction.recipient().sendMessage(MessageConfiguration.component("banking_pin_cancelled"));
        pinTransactions.remove(transaction);
    }

    public void completeTransaction(PinTransaction transaction) {
        MinetopiaPlayer senderPlayer = PlayerManager.getInstance().getOnlineMinetopiaPlayer(transaction.sender());

        BankAccountModel senderAccount = bankingModule.getAccountById(senderPlayer.getBukkit().getUniqueId());
        senderAccount.setBalance(senderAccount.getBalance() - transaction.amount());
        senderAccount.save();
        transaction.sender().sendMessage(ChatUtils.color(MessageConfiguration.message("banking_pin_sent")
                .replace("<amount>", bankingModule.format(transaction.amount()))
                .replace("<player>", transaction.recipient().getName())));

        BankAccountModel recipientAccount = transaction.account();
        recipientAccount.setBalance(recipientAccount.getBalance() + transaction.amount());
        recipientAccount.save();
        transaction.recipient().sendMessage(ChatUtils.color(MessageConfiguration.message("banking_pin_received")
                .replace("<amount>", bankingModule.format(transaction.amount()))
                .replace("<player>", transaction.sender().getName())));

        pinTransactions.remove(transaction);
    }

    public boolean isRecipientInTransaction(Player player) {
        return pinTransactions.stream().anyMatch(transaction -> transaction.recipient().equals(player));
    }

    public boolean isSenderInTransaction(Player player) {
        return pinTransactions.stream().anyMatch(transaction -> transaction.sender().equals(player));
    }
}
