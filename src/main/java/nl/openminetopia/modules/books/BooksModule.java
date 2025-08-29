package nl.openminetopia.modules.books;

import com.jazzkuh.modulemanager.spigot.SpigotModule;
import com.jazzkuh.modulemanager.spigot.SpigotModuleManager;
import lombok.Getter;
import lombok.Setter;
import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.modules.books.commands.BooksCommand;
import nl.openminetopia.modules.books.configuration.BooksConfiguration;
import nl.openminetopia.modules.books.objects.CustomBook;
import nl.openminetopia.utils.input.ChatInputHandler;
import nl.openminetopia.utils.FeatureUtils;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter @Setter
public class BooksModule extends SpigotModule<@NotNull OpenMinetopia> {
    public BooksModule(SpigotModuleManager<@NotNull OpenMinetopia> moduleManager) {
        super(moduleManager);
    }

    private BooksConfiguration configuration;
    private Map<UUID, Map<String, String>> variableResponses;

    @Override
    public void onEnable() {
        configuration = new BooksConfiguration(OpenMinetopia.getInstance().getDataFolder());
        variableResponses = new HashMap<>();

        // Check if books feature is enabled
        if (FeatureUtils.isFeatureDisabled("books")) {
            getLogger().info("Books feature is disabled in config.yml");
            return;
        }

        registerComponent(new BooksCommand());

        OpenMinetopia.getCommandManager().getCommandCompletions().registerAsyncCompletion("books", c -> {
            if (!(c.getSender() instanceof Player player)) return null;

            return configuration.getCustomBooks().stream()
                    .map(CustomBook::getIdentifier)
                    .filter(identifier -> player.hasPermission("openminetopia.books." + identifier))
                    .toList();
        });
    }
}
