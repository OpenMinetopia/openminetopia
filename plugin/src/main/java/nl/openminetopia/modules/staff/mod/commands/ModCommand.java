package nl.openminetopia.modules.staff.mod.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.HelpCommand;

@CommandAlias("mod")
public class ModCommand extends BaseCommand {

    @HelpCommand
    public void help(CommandHelp help) {
        help.showHelp();
    }
}
