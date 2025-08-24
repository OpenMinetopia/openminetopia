package nl.openminetopia.modules.scoreboard;

import com.jazzkuh.modulemanager.spigot.SpigotModule;
import com.jazzkuh.modulemanager.spigot.SpigotModuleManager;
import lombok.Getter;
import net.megavex.scoreboardlibrary.api.ScoreboardLibrary;
import net.megavex.scoreboardlibrary.api.exception.NoPacketAdapterAvailableException;
import net.megavex.scoreboardlibrary.api.noop.NoopScoreboardLibrary;
import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.configuration.DefaultConfiguration;
import nl.openminetopia.modules.data.DataModule;
import nl.openminetopia.modules.scoreboard.commands.ScoreboardCommand;
import nl.openminetopia.modules.scoreboard.listeners.PlayerJoinListener;
import nl.openminetopia.modules.scoreboard.listeners.PlayerQuitListener;
import nl.openminetopia.utils.FeatureUtils;
import org.jetbrains.annotations.NotNull;

@Getter
public class ScoreboardModule extends SpigotModule<@NotNull OpenMinetopia> {

    public ScoreboardModule(SpigotModuleManager<@NotNull OpenMinetopia> moduleManager, DataModule dataModule) {
        super(moduleManager);
    }

    private ScoreboardLibrary scoreboardLibrary;

    @Override
    public void onEnable() {
        DefaultConfiguration configuration = OpenMinetopia.getDefaultConfiguration();
        if (!configuration.isScoreboardEnabled()) return;

        // Check if scoreboard feature is enabled
        if (FeatureUtils.isFeatureDisabled("scoreboard")) {
            getLogger().info("Scoreboard feature is disabled in config.yml");
            return;
        }

        registerComponent(new PlayerJoinListener());
        registerComponent(new PlayerQuitListener());

        registerComponent(new ScoreboardCommand());

        try {
            scoreboardLibrary = ScoreboardLibrary.loadScoreboardLibrary(OpenMinetopia.getInstance());
        } catch (NoPacketAdapterAvailableException e) {
            // If no packet adapter was found, you can fallback to the no-op implementation:
            scoreboardLibrary = new NoopScoreboardLibrary();
            OpenMinetopia.getInstance().getLogger().info("No scoreboard packet adapter available!");
        }
    }

    @Override
    public void onDisable() {
        if (scoreboardLibrary != null) scoreboardLibrary.close();
    }
}
