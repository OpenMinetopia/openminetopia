package nl.openminetopia.modules.admintool.commands.subcommands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Subcommand;
import com.destroystokyo.paper.profile.PlayerProfile;
import nl.openminetopia.modules.admintool.menus.AdminToolMenu;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

@CommandAlias("admintool")
public class AdminToolOpenCommand extends BaseCommand {

    @Subcommand("open")
    public void onOpen(Player player, OfflinePlayer offlinePlayer) {
        PlayerProfile profile = offlinePlayer.getPlayerProfile();
        AdminToolMenu menu = new AdminToolMenu(player, offlinePlayer);
        menu.open(player);
    }
}