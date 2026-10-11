package nl.openminetopia.modules.books.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Subcommand;
import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.api.player.PlayerManager;
import nl.openminetopia.api.player.objects.MinetopiaPlayer;
import nl.openminetopia.configuration.MessageConfiguration;
import nl.openminetopia.modules.books.BooksModule;
import nl.openminetopia.modules.books.objects.CustomBook;
import nl.openminetopia.utils.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@CommandAlias("boek|book|boeken|books")
public class BooksCommand extends BaseCommand {

    @Subcommand("krijg|get")
    @CommandCompletion("@books")
    public void getBook(Player player, String book) {
        BooksModule booksModule = OpenMinetopia.getModuleManager().get(BooksModule.class);

        if (!player.hasPermission("openminetopia.books." + book)) {
            player.sendMessage(MessageConfiguration.component("books_no_permission"));
            return;
        }

        booksModule.getConfiguration().getCustomBooks().stream()
                .filter(customBook -> customBook.getIdentifier().equalsIgnoreCase(book))
                .findFirst()
                .ifPresentOrElse(customBook -> {
                    askForVariables(player, customBook, 0);
                }, () -> player.sendMessage(MessageConfiguration.component("books_not_found")));
    }

    private void askForVariables(Player player, CustomBook book, int index) {
        BooksModule booksModule = OpenMinetopia.getModuleManager().get(BooksModule.class);
        boolean vog = book.getIdentifier().equalsIgnoreCase(BooksModule.VOG_BOOK) && booksModule.getConfiguration().isVogAutomatic();
        // An automatic VOG gets its outcome from the criminal record, not from the officer.
        String[] keys = book.getVariables().keySet().stream()
                .filter(key -> !(vog && key.equals(BooksModule.VOG_STATUS)))
                .toArray(String[]::new);
        MinetopiaPlayer minetopiaPlayer = PlayerManager.getInstance().getOnlineMinetopiaPlayer(player);

        if (index >= keys.length && vog) {
            issueVog(player, minetopiaPlayer, book, booksModule.getVariableResponses().remove(player.getUniqueId()));
            return;
        }

        if (index >= keys.length) {
            // A VOG filled in by hand still shows until when it is valid.
            if (book.getIdentifier().equalsIgnoreCase(BooksModule.VOG_BOOK)) {
                long validUntil = System.currentTimeMillis() + TimeUnit.DAYS.toMillis(booksModule.getConfiguration().getVogValidDays());
                booksModule.getVariableResponses().computeIfAbsent(player.getUniqueId(), uuid -> new HashMap<>())
                        .put(BooksModule.VOG_VALID_UNTIL, booksModule.formatVogDate(validUntil));
            }

            // Alle variabelen zijn ingevuld, geef het boek
            Bukkit.getScheduler().runTask(OpenMinetopia.getInstance(), () -> {
                if (book.isCopy()) {
                    player.getInventory().addItem(book.getBookItem(booksModule.getVariableResponses().get(player.getUniqueId()), player, true));
                }
                player.getInventory().addItem(book.getBookItem(booksModule.getVariableResponses().get(player.getUniqueId()), player, false));
            });

            player.sendMessage(ChatUtils.format(minetopiaPlayer, MessageConfiguration.message("books_received")
                    .replace("<book>", book.getName())));
            return;
        }

        String key = keys[index];
        String explanation = book.getVariables().get(key);

        player.sendMessage(ChatUtils.format(minetopiaPlayer, explanation));

        OpenMinetopia.getChatInputHandler().waitForInput(player, response -> {
            Map<String, String> responded = new HashMap<>();
            if (booksModule.getVariableResponses().containsKey(player.getUniqueId())) {
                responded = booksModule.getVariableResponses().get(player.getUniqueId());
            }
            responded.put(key, response); // Sla het antwoord op in de map
            booksModule.getVariableResponses().put(player.getUniqueId(), responded); // Sla het antwoord op in de map
            askForVariables(player, book, index + 1); // Ga naar de volgende variabele
        });
    }

    /** Saves a VOG for the player named in the book and gives the officer the book with the outcome filled in. */
    private void issueVog(Player officer, MinetopiaPlayer officerMinetopiaPlayer, CustomBook book, Map<String, String> answers) {
        String playerName = answers == null ? null : answers.get("<player_name>");
        OfflinePlayer target = playerName == null ? null : Bukkit.getOfflinePlayerIfCached(playerName);
        if (target == null) {
            officer.sendMessage(MessageConfiguration.component("player_not_found"));
            return;
        }

        PlayerManager.getInstance().getMinetopiaPlayer(target).whenComplete((targetMinetopiaPlayer, throwable) -> {
            if (targetMinetopiaPlayer == null) {
                officer.sendMessage(MessageConfiguration.component("player_not_found"));
                return;
            }

            targetMinetopiaPlayer.issueVog(officer.getUniqueId()).whenComplete((vog, vogThrowable) -> {
                if (vog == null) return;

                Bukkit.getScheduler().runTask(OpenMinetopia.getInstance(), () -> {
                    if (!officer.isOnline()) return;
                    BooksModule booksModule = OpenMinetopia.getModuleManager().get(BooksModule.class);
                    booksModule.giveVogBook(officer, book, target.getName(), vog, answers);
                    officer.sendMessage(ChatUtils.format(officerMinetopiaPlayer, MessageConfiguration.message("books_received")
                            .replace("<book>", book.getName())));
                });
            });
        });
    }
}
