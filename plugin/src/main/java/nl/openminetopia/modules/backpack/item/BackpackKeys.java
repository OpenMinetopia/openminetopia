package nl.openminetopia.modules.backpack.item;

import lombok.experimental.UtilityClass;
import nl.openminetopia.OpenMinetopia;
import org.bukkit.NamespacedKey;

@UtilityClass
public class BackpackKeys {

    public static NamespacedKey id() {
        return new NamespacedKey(OpenMinetopia.getInstance(), "backpack_id");
    }

    public static NamespacedKey rows() {
        return new NamespacedKey(OpenMinetopia.getInstance(), "backpack_rows");
    }

    public static NamespacedKey contents() {
        return new NamespacedKey(OpenMinetopia.getInstance(), "backpack_contents");
    }

    public static NamespacedKey title() {
        return new NamespacedKey(OpenMinetopia.getInstance(), "backpack_title");
    }
}