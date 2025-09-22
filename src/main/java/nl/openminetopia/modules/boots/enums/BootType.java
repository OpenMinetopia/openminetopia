package nl.openminetopia.modules.boots.enums;

import org.bukkit.Material;

public enum BootType {
    LEATHER(Material.LEATHER_BOOTS, "Leather Boots"),
    CHAINMAIL(Material.CHAINMAIL_BOOTS, "Chainmail Boots"),
    IRON(Material.IRON_BOOTS, "Iron Boots"),
    GOLD(Material.GOLDEN_BOOTS, "Gold Boots"),
    DIAMOND(Material.DIAMOND_BOOTS, "Diamond Boots");

    private final Material material;
    private final String displayName;

    BootType(Material material, String displayName) {
        this.material = material;
        this.displayName = displayName;
    }

    public Material getMaterial() {
        return material;
    }

    public String getDisplayName() {
        return displayName;
    }
}
