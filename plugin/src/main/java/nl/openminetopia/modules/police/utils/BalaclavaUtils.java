package nl.openminetopia.modules.police.utils;

import lombok.experimental.UtilityClass;
import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.utils.ChatUtils;
import nl.openminetopia.utils.item.ItemUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@UtilityClass
public class BalaclavaUtils {

    public void obfuscate(Player player, boolean obfuscate) {
        if (obfuscate) {
            player.displayName(ChatUtils.color("<obf>Balaclava</obf><reset>"));
            return;
        }
        player.displayName(ChatUtils.color(player.getName()));
    }

    public boolean isBalaclavaItem(ItemStack head) {
        return ItemUtils.isSimilarToAny(head, OpenMinetopia.getDefaultConfiguration().getBalaclavaItems());
    }

    public boolean isWearingBalaclava(Player player) {
        return isBalaclavaItem(player.getInventory().getHelmet());
    }
}
