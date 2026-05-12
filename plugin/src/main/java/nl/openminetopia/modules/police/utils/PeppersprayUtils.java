package nl.openminetopia.modules.police.utils;

import lombok.experimental.UtilityClass;
import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.configuration.DefaultConfiguration;
import nl.openminetopia.utils.item.ItemUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@UtilityClass
public class PeppersprayUtils {

    public void applyPeppersprayEffects(Player target) {
        DefaultConfiguration configuration = OpenMinetopia.getDefaultConfiguration();

        ItemUtils.applyEffects(target, configuration.getPeppersprayEffects(), configuration.getPeppersprayEffectsDuration() * 20);
    }

    public boolean isPeppersprayItem(ItemStack item) {
        return ItemUtils.isSimilarToAny(item, OpenMinetopia.getDefaultConfiguration().getPeppersprayItems());
    }
}
