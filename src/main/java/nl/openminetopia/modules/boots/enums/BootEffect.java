package nl.openminetopia.modules.boots.enums;

import org.bukkit.Material;

public enum BootEffect {
    SPEED(Material.POTION, "Speed", "Geeft snelheid boost"),
    ICE(Material.ICE, "Ice", "Geeft Frost Walker effect"),
    BLUB(Material.WATER_BUCKET, "Blub", "Geeft sneller zwemmen");

    private final Material icon;
    private final String displayName;
    private final String description;

    BootEffect(Material icon, String displayName, String description) {
        this.icon = icon;
        this.displayName = displayName;
        this.description = description;
    }

    public Material getIcon() {
        return icon;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }
}
