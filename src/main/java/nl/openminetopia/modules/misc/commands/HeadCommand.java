package nl.openminetopia.modules.misc.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.configuration.DefaultConfiguration;
import nl.openminetopia.configuration.MessageConfiguration;
import nl.openminetopia.modules.misc.utils.MiscUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@CommandAlias("head")
public class HeadCommand extends BaseCommand {

    @Default
    public void head(Player player) {
        ItemStack item = player.getInventory().getItemInMainHand().clone();

        if (item.getType() == Material.AIR || !MiscUtils.isValidHeadItem(item)) {
            player.sendMessage(MessageConfiguration.component("head_item_not_allowed"));
            return;
        }

        // only remove 1 item from the stack
        player.getInventory().getItemInMainHand().setAmount(player.getInventory().getItemInMainHand().getAmount() - 1);
        if (player.getInventory().getHelmet() != null) player.getInventory().addItem(player.getInventory().getHelmet());
        player.getInventory().setHelmet(item);

        player.sendMessage(MessageConfiguration.component("head_item_equipped"));
    }

    @Subcommand("add")
    @CommandPermission("openminetopia.head.add")
    @Description("Voeg een item toe aan de head whitelist.")
    public void add(Player player) {
        ItemStack item = player.getInventory().getItemInMainHand();

        if (item.getType() != Material.AIR && MiscUtils.isValidHeadItem(item)) {
            player.sendMessage(MessageConfiguration.component("head_item_already_whitelisted"));
            return;
        }

        DefaultConfiguration configuration = OpenMinetopia.getDefaultConfiguration();
        configuration.addToHeadWhitelist(item);

        player.sendMessage(MessageConfiguration.component("head_item_added_whitelist"));
    }

    @Subcommand("remove")
    @CommandPermission("openminetopia.head.remove")
    @Description("Verwijder een item van de head whitelist.")
    public void remove(Player player) {
        ItemStack item = player.getInventory().getItemInMainHand();

        if (item.getType() != Material.AIR && !MiscUtils.isValidHeadItem(item)) {
            player.sendMessage(MessageConfiguration.component("head_item_not_whitelisted"));
            return;
        }

        DefaultConfiguration configuration = OpenMinetopia.getDefaultConfiguration();
        configuration.removeFromHeadWhitelist(item);

        player.sendMessage(MessageConfiguration.component("head_item_removed_whitelist"));
    }
}
