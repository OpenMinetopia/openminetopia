package nl.openminetopia.modules.books;

import nl.openminetopia.utils.modules.ExtendedSpigotModule;
import com.jazzkuh.modulemanager.spigot.SpigotModuleManager;
import lombok.Getter;
import lombok.Setter;
import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.configuration.MessageConfiguration;
import nl.openminetopia.modules.books.commands.BooksCommand;
import nl.openminetopia.modules.books.configuration.BooksConfiguration;
import nl.openminetopia.modules.books.objects.CustomBook;
import nl.openminetopia.modules.police.models.VogModel;
import nl.openminetopia.utils.input.ChatInputHandler;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter @Setter
public class BooksModule extends ExtendedSpigotModule {
    public BooksModule(SpigotModuleManager<@NotNull OpenMinetopia> moduleManager) {
        super(moduleManager);
    }

    /** The book whose <status> the plugin fills in from the criminal record instead of asking. */
    public static final String VOG_BOOK = "vog";
    public static final String VOG_STATUS = "<status>";
    public static final String VOG_VALID_UNTIL = "<valid_until>";

    private BooksConfiguration configuration;
    private Map<UUID, Map<String, String>> variableResponses;

    @Override
    public void onEnable() {
        configuration = new BooksConfiguration(OpenMinetopia.getInstance().getDataFolder());
        variableResponses = new HashMap<>();

        registerComponent(new BooksCommand());

        OpenMinetopia.getCommandManager().getCommandCompletions().registerAsyncCompletion("books", c -> {
            if (!(c.getSender() instanceof Player player)) return null;

            return configuration.getCustomBooks().stream()
                    .map(CustomBook::getIdentifier)
                    .filter(identifier -> player.hasPermission("openminetopia.books." + identifier))
                    .toList();
        });
    }

    /** The VOG book from books.yml, or null when the server turned it off. */
    public @Nullable CustomBook getVogBook() {
        return configuration.getCustomBooks().stream()
                .filter(book -> book.getIdentifier().equalsIgnoreCase(VOG_BOOK))
                .findFirst()
                .orElse(null);
    }

    /**
     * Gives the officer the VOG book (and its copy) for a VOG that was just issued. Run on the main thread.
     * Variables the officer did not answer, like the issuer when the VOG comes from the portal, get the officer's name.
     */
    public void giveVogBook(Player officer, CustomBook book, String playerName, VogModel vog, @Nullable Map<String, String> answers) {
        Map<String, String> variables = new HashMap<>();
        book.getVariables().keySet().forEach(key -> variables.put(key, officer.getName()));
        if (answers != null) variables.putAll(answers);
        variables.put("<player_name>", playerName);
        variables.put(VOG_STATUS, MessageConfiguration.message(vog.getPositive() ? "books_vog_positive" : "books_vog_negative"));
        variables.put(VOG_VALID_UNTIL, formatVogDate(vog.getValidUntil()));

        if (book.isCopy()) {
            officer.getInventory().addItem(book.getBookItem(variables, officer, true));
        }
        officer.getInventory().addItem(book.getBookItem(variables, officer, false));
    }

    public String formatVogDate(long millis) {
        return new SimpleDateFormat(MessageConfiguration.message("books_vog_date_format")).format(new Date(millis));
    }
}
