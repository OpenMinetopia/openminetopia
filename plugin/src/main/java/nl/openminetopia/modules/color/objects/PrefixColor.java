package nl.openminetopia.modules.color.objects;

import lombok.Getter;
import lombok.Setter;
import nl.openminetopia.modules.color.enums.OwnableColorType;

@Getter
@Setter
public class PrefixColor extends OwnableColor {

    public PrefixColor(int id, String colorId, long expiresAt) {
        super(OwnableColorType.PREFIX, id, colorId, expiresAt);
    }

    public PrefixColor(String colorId, long expiresAt) {
        super(OwnableColorType.PREFIX, colorId, expiresAt);
    }

}
