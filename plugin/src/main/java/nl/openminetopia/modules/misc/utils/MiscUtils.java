package nl.openminetopia.modules.misc.utils;

import lombok.experimental.UtilityClass;
import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.modules.misc.objects.PvPItem;
import nl.openminetopia.utils.item.ItemUtils;
import org.bukkit.inventory.ItemStack;

@UtilityClass
public class MiscUtils {

    public boolean isValidHeadItem(ItemStack head) {
        return ItemUtils.isSimilarToAny(head, OpenMinetopia.getDefaultConfiguration().getHeadWhitelist());
    }

    public PvPItem getPvPItem(ItemStack item) {
        for (PvPItem pvpItem : OpenMinetopia.getDefaultConfiguration().getPvpItems()) {
            if (pvpItem.isSimilar(item)) return pvpItem;
        }
        return null;
    }
}
