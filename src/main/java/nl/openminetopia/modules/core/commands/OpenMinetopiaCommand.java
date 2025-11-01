package nl.openminetopia.modules.core.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.RootCommand;
import co.aikar.commands.annotation.*;
import lombok.SneakyThrows;
import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.configuration.DefaultConfiguration;
import nl.openminetopia.configuration.MessageConfiguration;
import nl.openminetopia.modules.banking.BankingModule;
import nl.openminetopia.modules.banking.configuration.BankingConfiguration;
import nl.openminetopia.modules.books.BooksModule;
import nl.openminetopia.modules.books.configuration.BooksConfiguration;
import nl.openminetopia.modules.color.ColorModule;
import nl.openminetopia.modules.color.configuration.ColorsConfiguration;
import nl.openminetopia.modules.fitness.FitnessModule;
import nl.openminetopia.modules.fitness.configuration.FitnessConfiguration;
import nl.openminetopia.modules.items.ItemsModule;
import nl.openminetopia.modules.labymod.LabymodModule;
import nl.openminetopia.modules.labymod.configuration.LabymodConfiguration;
import nl.openminetopia.modules.player.PlayerModule;
import nl.openminetopia.modules.player.configuration.LevelCheckConfiguration;
import nl.openminetopia.modules.plots.PlotModule;
import nl.openminetopia.modules.plots.configuration.PlotCalculateConfiguration;
import nl.openminetopia.utils.ChatUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.List;

@CommandAlias("openminetopia|sdb|minetopia|omt")
public class OpenMinetopiaCommand extends BaseCommand {

    @Subcommand("reload")
    @SneakyThrows
    @CommandPermission("openminetopia.reload")
    public void reload(CommandSender sender) {
        File dataFolder = OpenMinetopia.getInstance().getDataFolder();

        OpenMinetopia.setDefaultConfiguration(new DefaultConfiguration(dataFolder));
        OpenMinetopia.getDefaultConfiguration().saveConfiguration();

        OpenMinetopia.setMessageConfiguration(new MessageConfiguration(dataFolder));
        OpenMinetopia.getMessageConfiguration().saveConfiguration();

        PlayerModule playerModule = OpenMinetopia.getModuleManager().get(PlayerModule.class);
        playerModule.setConfiguration(new LevelCheckConfiguration(dataFolder));
        playerModule.getConfiguration().saveConfiguration();

        if (!OpenMinetopia.getDefaultConfiguration().isModuleDisabled(ColorModule.class)) {
            ColorModule colorModule = OpenMinetopia.getModuleManager().get(ColorModule.class);
            colorModule.setConfiguration(new ColorsConfiguration(dataFolder));
            colorModule.getConfiguration().saveConfiguration();
        }

        if (!OpenMinetopia.getDefaultConfiguration().isModuleDisabled(FitnessModule.class)) {
            FitnessModule fitnessModule = OpenMinetopia.getModuleManager().get(FitnessModule.class);
            fitnessModule.setConfiguration(new FitnessConfiguration(dataFolder));
            fitnessModule.getConfiguration().saveConfiguration();
        }

        if (!OpenMinetopia.getDefaultConfiguration().isModuleDisabled(BankingModule.class)) {
            BankingModule bankingModule = OpenMinetopia.getModuleManager().get(BankingModule.class);
            bankingModule.setConfiguration(new BankingConfiguration(dataFolder));
            bankingModule.getConfiguration().saveConfiguration();
        }

        if (!OpenMinetopia.getDefaultConfiguration().isModuleDisabled(PlotModule.class)) {
            PlotModule plotModule = OpenMinetopia.getModuleManager().get(PlotModule.class);
            plotModule.setCalculateConfiguration(new PlotCalculateConfiguration(dataFolder));
            plotModule.getCalculateConfiguration().saveConfiguration();
        }

        if (!OpenMinetopia.getDefaultConfiguration().isModuleDisabled(BooksModule.class)) {
            BooksModule booksModule = OpenMinetopia.getModuleManager().get(BooksModule.class);
            booksModule.setConfiguration(new BooksConfiguration(dataFolder));
            booksModule.getConfiguration().saveConfiguration();
        }

        if (!OpenMinetopia.getDefaultConfiguration().isModuleDisabled(LabymodModule.class)) {
            LabymodModule labymodModule = OpenMinetopia.getModuleManager().get(LabymodModule.class);
            labymodModule.setConfiguration(new LabymodConfiguration(dataFolder));
            labymodModule.getConfiguration().saveConfiguration();
        }

        if (!OpenMinetopia.getDefaultConfiguration().isModuleDisabled(ItemsModule.class)) {
            ItemsModule module = OpenMinetopia.getModuleManager().get(ItemsModule.class);
            module.reload();
        }

        sender.sendMessage(ChatUtils.color("<gold>De configuratiebestanden zijn succesvol herladen!"));
    }

    @Default
    public void onCommand(Player player) {
        player.sendMessage(ChatUtils.color(" "));
        player.sendMessage(ChatUtils.color("<gold>Deze server maakt gebruik van <yellow>OpenMinetopia <gold>versie <yellow>" + OpenMinetopia.getInstance().getPluginMeta().getVersion()));
        player.sendMessage(ChatUtils.color("<gold>Auteurs: <yellow>" + OpenMinetopia.getInstance().getPluginMeta().getAuthors().toString().replace("[", "").replace("]", "")));
        player.sendMessage(ChatUtils.color(" "));
    }

    @Subcommand("help")
    @CommandPermission("openminetopia.help")
    @Description("Laat alle commando's zien die beschikbaar zijn in OpenMinetopia")
    public void help(CommandSender sender, @Optional Integer page) {
        List<RootCommand> rootCommands = OpenMinetopia.getCommandManager().getRegisteredRootCommands().stream().toList();

        // paginated help
        int pageSize = 10;
        int pages = (int) Math.ceil(rootCommands.size() / (double) pageSize);
        int currentPage = page == null ? 1 : page;

        ChatUtils.sendMessage(sender, " ");
        ChatUtils.sendMessage(sender, "<gold>OpenMinetopia commando's" + " <gray>(<yellow>" + currentPage + "<gray>/<yellow>" + pages +  "<gray>)<gold>:");

        if (currentPage < 1 || currentPage > pages) {
            ChatUtils.sendMessage(sender, "<red>Deze pagina bestaat niet.");
            return;
        }

        int start = (currentPage - 1) * pageSize;
        int end = Math.min(start + pageSize, rootCommands.size());

        for (int i = start; i < end; i++) {
            RootCommand command = rootCommands.get(i);
            ChatUtils.sendMessage(sender, "<gray>/<yellow><click:suggest_command:'/" + command.getCommandName() + "'>" + command.getCommandName() + " <gray>- " + (command.getDescription().isEmpty() ? "Geen beschrijving" : command.getDescription()) + "</click>");
        }

        ChatUtils.sendMessage(sender, " ");
        ChatUtils.sendMessage(sender, "<gold>Gebruik <yellow>/openminetopia help <pagina> <gold>om naar een andere pagina te gaan.");
    }
}
