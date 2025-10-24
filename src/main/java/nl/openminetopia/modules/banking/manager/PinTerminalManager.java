package nl.openminetopia.modules.banking.manager;

import lombok.Getter;
import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.api.player.PlayerManager;
import nl.openminetopia.api.player.objects.MinetopiaPlayer;
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
        sender.sendMessage(ChatUtils.color("<dark_green>Je hebt een pinverzoek ontvangen van <green>" + recipient.getName() + "</green> voor een bedrag van <green>" + bankingModule.format(amount) + "</green>."));
        sender.sendMessage(ChatUtils.color("<dark_green><green>Rechter-muis</green> op de pin-terminal om te accepteren, <green>linker-muis</green> om te weigeren."));

        recipient.sendMessage(ChatUtils.color("<dark_green>Je hebt een pinverzoek verzonden naar <green>" + sender.getName() + "</green> voor een bedrag van <green>" + bankingModule.format(amount) + "</green>."));

        PinTransaction transaction = new PinTransaction(sender, recipient, amount, recipientAccount);
        pinTransactions.add(transaction);
        return transaction;
    }

    public void cancelTransaction(PinTransaction transaction) {
        transaction.sender().sendMessage(ChatUtils.color("<red>De pintransactie is geannuleerd."));
        transaction.recipient().sendMessage(ChatUtils.color("<red>De pintransactie is geannuleerd."));
        pinTransactions.remove(transaction);
    }

    public void completeTransaction(PinTransaction transaction) {
        MinetopiaPlayer senderPlayer = PlayerManager.getInstance().getOnlineMinetopiaPlayer(transaction.sender());

        BankAccountModel senderAccount = bankingModule.getAccountById(senderPlayer.getBukkit().getUniqueId());
        senderAccount.setBalance(senderAccount.getBalance() - transaction.amount());
        senderAccount.save();
        transaction.sender().sendMessage(ChatUtils.color("<dark_green>Je hebt succesvol <green>" + bankingModule.format(transaction.amount()) + "</green> verzonden via pin naar <green>" + transaction.recipient().getName() + "</green>."));

        BankAccountModel recipientAccount = transaction.account();
        recipientAccount.setBalance(recipientAccount.getBalance() + transaction.amount());
        recipientAccount.save();
        transaction.recipient().sendMessage(ChatUtils.color("<dark_green>Je hebt succesvol <green>" + bankingModule.format(transaction.amount()) + "</green> ontvangen via pin van <green>" + transaction.sender().getName() + "</green>."));

        pinTransactions.remove(transaction);
    }

    public boolean isRecipientInTransaction(Player player) {
        return pinTransactions.stream().anyMatch(transaction -> transaction.recipient().equals(player));
    }

    public boolean isSenderInTransaction(Player player) {
        return pinTransactions.stream().anyMatch(transaction -> transaction.sender().equals(player));
    }
}