package nl.openminetopia.modules.misc.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.configuration.DefaultConfiguration;
import nl.openminetopia.modules.misc.utils.MiscUtils;
import nl.openminetopia.utils.ChatUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@CommandAlias("head")
public class HeadCommand extends BaseCommand {

    @Default
    public void head(Player player) {
        ItemStack item = player.getInventory().getItemInMainHand().clone();

        if (item.getType() == Material.AIR || !MiscUtils.isValidHeadItem(item)) {
            player.sendMessage(ChatUtils.color("<red>Je kan dit item niet op je hoofd dragen!"));
            return;
        }

        // only remove 1 item from the stack
        player.getInventory().getItemInMainHand().setAmount(player.getInventory().getItemInMainHand().getAmount() - 1);
        if (player.getInventory().getHelmet() != null) player.getInventory().addItem(player.getInventory().getHelmet());
        player.getInventory().setHelmet(item);

        player.sendMessage(ChatUtils.color("<green>Je hebt het item op je hoofd gezet!"));
    }

    @Subcommand("add")
    @CommandPermission("openminetopia.head.add")
    @Description("Voeg een item toe aan de head whitelist.")
    public void add(Player player) {
        ItemStack item = player.getInventory().getItemInMainHand();

        if (item.getType() != Material.AIR && MiscUtils.isValidHeadItem(item)) {
            player.sendMessage(ChatUtils.color("<red>Dit item staat al op de head whitelist!"));
            return;
        }

        DefaultConfiguration configuration = OpenMinetopia.getDefaultConfiguration();
        configuration.addToHeadWhitelist(item);

        player.sendMessage(ChatUtils.color("<green>Je hebt het item toegevoegd aan de head whitelist!"));
    }

    @Subcommand("remove")
    @CommandPermission("openminetopia.head.remove")
    @Description("Verwijder een item van de head whitelist.")
    public void remove(Player player) {
        ItemStack item = player.getInventory().getItemInMainHand();

        if (item.getType() != Material.AIR && !MiscUtils.isValidHeadItem(item)) {
            player.sendMessage(ChatUtils.color("<red>Dit item staat niet op de head whitelist!"));
            return;
        }

        DefaultConfiguration configuration = OpenMinetopia.getDefaultConfiguration();
        configuration.removeFromHeadWhitelist(item);

        player.sendMessage(ChatUtils.color("<green>Je hebt het item verwijderd van de head whitelist!"));
    }
}
