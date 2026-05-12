package nl.openminetopia.modules.color.objects;

import lombok.Getter;
import lombok.Setter;
import nl.openminetopia.modules.color.enums.OwnableColorType;

@Getter
@Setter
public class ChatColor extends OwnableColor {

    public ChatColor(int id, String colorId, long expiresAt) {
        super(OwnableColorType.CHAT, id, colorId, expiresAt);
    }

    public ChatColor(String colorId, long expiresAt) {
        super(OwnableColorType.CHAT, colorId, expiresAt);
    }

}
