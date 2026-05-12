package nl.openminetopia.modules.banking.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nl.openminetopia.configuration.MessageConfiguration;
import org.bukkit.Material;

@Getter
@AllArgsConstructor
public enum AccountType {
    PRIVATE("<gold>", MessageConfiguration.message("banking_private_account"), Material.GOLD_BLOCK,  14),
    COMPANY("<aqua>", MessageConfiguration.message("banking_company_account"), Material.DIAMOND_BLOCK, 16),
    SAVINGS("<red>", MessageConfiguration.message("banking_savings_account"), Material.REDSTONE_BLOCK, 12),
    GOVERNMENT("<dark_green>", MessageConfiguration.message("banking_government_account"), Material.EMERALD_BLOCK, 10);

    private final String color;
    private final String name;
    private final Material material;
    private final int slot;

}