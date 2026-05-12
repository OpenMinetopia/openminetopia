package nl.openminetopia.modules.places.commands.mtworld;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.HelpCommand;

@CommandAlias("mtwereld|mtworld")
@CommandPermission("openminetopia.world")
public class MTWorldCommand extends BaseCommand {

    @HelpCommand
    public void onHelp(CommandHelp help) {
        help.showHelp();
    }
}
