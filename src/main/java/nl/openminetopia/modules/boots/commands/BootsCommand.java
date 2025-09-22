package nl.openminetopia.modules.boots.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import nl.openminetopia.modules.boots.menus.BootTypeSelectionMenu;
import org.bukkit.entity.Player;

@CommandAlias("boots")
@CommandPermission("openmt.boots")
public class BootsCommand extends BaseCommand {

    @Default
    public void onBoots(Player player) {
        new BootTypeSelectionMenu().open(player);
    }
}
