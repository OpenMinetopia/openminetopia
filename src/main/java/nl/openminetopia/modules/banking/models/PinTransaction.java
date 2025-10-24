package nl.openminetopia.modules.banking.models;

import org.bukkit.entity.Player;

public record PinTransaction(Player sender,
                             Player recipient,
                             double amount,
                             BankAccountModel account) {
}
