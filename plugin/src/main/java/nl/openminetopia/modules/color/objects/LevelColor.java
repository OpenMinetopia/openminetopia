package nl.openminetopia.modules.color.objects;

import lombok.Getter;
import lombok.Setter;
import nl.openminetopia.modules.color.enums.OwnableColorType;

@Getter
@Setter
public class LevelColor extends OwnableColor {

    public LevelColor(int id, String colorId, long expiresAt) {
        super(OwnableColorType.LEVEL, id, colorId, expiresAt);
    }

    public LevelColor(String colorId, long expiresAt) {
        super(OwnableColorType.LEVEL, colorId, expiresAt);
    }

}
