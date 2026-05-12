package nl.openminetopia.modules.staff.admintool.commands.subcommands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import nl.openminetopia.utils.PersistentDataUtil;
import nl.openminetopia.utils.item.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@CommandAlias("admintool")
public class AdminToolGetCommand extends BaseCommand {

    @Subcommand("krijg")
    @CommandPermission("openminetopia.admintool.get")
    public void admintoolGet(Player player) {
        ItemBuilder item = new ItemBuilder(Material.NETHER_STAR)
                .setName("<dark_red>Admin<red>Tool")
                .addLoreLine("<yellow>Linkermuisknop <gold>om je eigen menu te openen")
                .addLoreLine(" ")
                .addLoreLine("<yellow>Rechtermuisknop <gold>op een speler om zijn menu te openen");

        ItemStack finalItem = PersistentDataUtil.set(item.toItemStack(), true, "openmt.admintool");
        player.getInventory().addItem(finalItem);
    }
}
