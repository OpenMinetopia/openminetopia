package nl.openminetopia.modules.color.objects;

import lombok.Getter;
import lombok.Setter;
import nl.openminetopia.modules.color.enums.OwnableColorType;

@Getter
@Setter
public class NameColor extends OwnableColor {

    public NameColor(int id, String colorId, long expiresAt) {
        super(OwnableColorType.NAME, id, colorId, expiresAt);
    }

    public NameColor(String colorId, long expiresAt) {
        super(OwnableColorType.NAME, colorId, expiresAt);
    }
}
