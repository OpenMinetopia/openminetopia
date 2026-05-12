package nl.openminetopia.modules.color.enums;

import lombok.Getter;
import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.configuration.DefaultConfiguration;
import nl.openminetopia.configuration.MessageConfiguration;
import nl.openminetopia.modules.color.objects.*;

@Getter
public enum OwnableColorType {
    PREFIX(MessageConfiguration.message("color_prefix_display_name"), OpenMinetopia.getDefaultConfiguration().getDefaultPrefixColor()),
    CHAT(MessageConfiguration.message("color_chat_display_name"), OpenMinetopia.getDefaultConfiguration().getDefaultChatColor()),
    NAME(MessageConfiguration.message("color_name_display_name"), OpenMinetopia.getDefaultConfiguration().getDefaultNameColor()),
    LEVEL(MessageConfiguration.message("color_level_display_name"), OpenMinetopia.getDefaultConfiguration().getDefaultLevelColor());

    private final String displayName;
    private final String defaultColor;

    OwnableColorType(String displayName, String defaultColor) {
        this.displayName = displayName;
        this.defaultColor = defaultColor;
    }

    public Class<? extends OwnableColor> correspondingClass() {
        return switch (this) {
            case PREFIX -> PrefixColor.class;
            case CHAT -> ChatColor.class;
            case NAME -> NameColor.class;
            case LEVEL -> LevelColor.class;
        };
    }

    public OwnableColor defaultColor() {
        DefaultConfiguration configuration = OpenMinetopia.getDefaultConfiguration();
        return switch (this) {
            case PREFIX -> new PrefixColor(-1, configuration.getDefaultPrefixColor(), -1);
            case NAME -> new NameColor(-1, configuration.getDefaultNameColor(), -1);
            case CHAT -> new ChatColor(-1, configuration.getDefaultChatColor(), -1);
            case LEVEL -> new LevelColor(-1, configuration.getDefaultLevelColor(), -1);
        };
    }
}