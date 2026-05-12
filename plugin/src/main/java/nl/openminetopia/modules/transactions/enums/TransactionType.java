package nl.openminetopia.modules.transactions.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;

@Getter
@AllArgsConstructor
public enum TransactionType {

    DEPOSIT(Material.EMERALD, "Deposit"),
    WITHDRAW(Material.REDSTONE, "Withdraw"),
    SET(Material.DIAMOND, "Set");

    private final Material material;
    private final String name;

}
