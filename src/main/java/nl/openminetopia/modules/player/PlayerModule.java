package nl.openminetopia.modules.player;

import com.craftmend.storm.api.enums.Where;
import com.jazzkuh.modulemanager.spigot.SpigotModule;
import com.jazzkuh.modulemanager.spigot.SpigotModuleManager;
import lombok.Getter;
import lombok.Setter;
import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.api.player.PlayerManager;
import nl.openminetopia.api.player.objects.MinetopiaPlayer;
import nl.openminetopia.modules.data.DataModule;
import nl.openminetopia.modules.data.storm.StormDatabase;
import nl.openminetopia.modules.player.commands.PlaytimeCommand;
import nl.openminetopia.modules.player.configuration.LevelCheckConfiguration;
import nl.openminetopia.modules.player.listeners.LevelcheckNpcListener;
import nl.openminetopia.modules.player.listeners.PlayerPreLoginListener;
import nl.openminetopia.modules.player.listeners.PlayerQuitListener;
import nl.openminetopia.utils.FeatureUtils;
import nl.openminetopia.modules.player.models.PlayerModel;
import nl.openminetopia.modules.player.runnables.PlaytimeRunnable;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.CompletableFuture;

@Setter @Getter
public class PlayerModule extends SpigotModule<@NotNull OpenMinetopia> {

    public PlayerModule(SpigotModuleManager<@NotNull OpenMinetopia> moduleManager, DataModule dataModule) {
        super(moduleManager);
    }

    private LevelCheckConfiguration configuration;
    private final Map<UUID, PlaytimeRunnable> playtimeRunnables = new HashMap<>();

    @Override
    public void onEnable() {
        configuration = new LevelCheckConfiguration(OpenMinetopia.getInstance().getDataFolder());
        configuration.saveConfiguration();

        // Check if player feature is enabled
        if (FeatureUtils.isFeatureDisabled("player")) {
            getLogger().info("Player feature is disabled in config.yml");
            return;
        }

        registerComponent(new PlayerPreLoginListener());
        registerComponent(new PlayerQuitListener());
        if (OpenMinetopia.getInstance().isNpcSupport()) registerComponent(new LevelcheckNpcListener());


        registerComponent(new PlaytimeCommand());

        Bukkit.getScheduler().runTaskTimerAsynchronously(OpenMinetopia.getInstance(), () -> {
            for (MinetopiaPlayer minetopiaPlayer : PlayerManager.getInstance().getOnlinePlayers().values()) {
                if (!(minetopiaPlayer instanceof MinetopiaPlayer onlineMinetopiaPlayer)) continue;
                onlineMinetopiaPlayer.save().whenComplete((unused, throwable) -> {
                    if (throwable != null) throwable.printStackTrace();
                });
            }
        }, 0, 20 * 60 * 5); // Save every 5 minutes
    }

    @Override
    public void onDisable() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            PlayerManager.getInstance().getMinetopiaPlayer(player).whenComplete((minetopiaPlayer, throwable) -> {
                if (minetopiaPlayer == null) return;
                minetopiaPlayer.save().whenComplete((unused, throwable1) -> {
                    if (throwable != null) throwable.printStackTrace();
                });
            });
        }
    }

    private CompletableFuture<Optional<PlayerModel>> findPlayerModel(@NotNull UUID uuid) {
        CompletableFuture<Optional<PlayerModel>> completableFuture = new CompletableFuture<>();
        StormDatabase.getExecutorService().submit(() -> {
            try {
                Collection<PlayerModel> playerModel = StormDatabase.getInstance().getStorm().buildQuery(PlayerModel.class)
                        .where("uuid", Where.EQUAL, uuid.toString())
                        .limit(1).execute().join();

                Bukkit.getScheduler().runTaskLaterAsynchronously(OpenMinetopia.getInstance(), () -> completableFuture.complete(playerModel.stream().findFirst()), 1L);
            } catch (Exception exception) {
                exception.printStackTrace();
                completableFuture.completeExceptionally(exception);
            }
        });
        return completableFuture;
    }

    public CompletableFuture<PlayerModel> getPlayerModel(UUID uuid) {
        CompletableFuture<PlayerModel> future = new CompletableFuture<>();

        findPlayerModel(uuid).thenAccept(playerModel -> {
            if (playerModel.isEmpty()) {
                PlayerModel createdModel = new PlayerModel();
                createdModel.setUniqueId(uuid);
                createdModel.setPlaytime(0);
                createdModel.setLevel(1);
                createdModel.setActivePrefixId(-1);
                createdModel.setActivePrefixColorId(-1);
                createdModel.setActiveChatColorId(-1);
                createdModel.setActiveNameColorId(-1);
                createdModel.setActiveLevelColorId(-1);
                createdModel.setStaffchatEnabled(false);
                createdModel.setCommandSpyEnabled(false);
                createdModel.setChatSpyEnabled(false);
                createdModel.setPrefixes(new ArrayList<>());
                createdModel.setColors(new ArrayList<>());
                createdModel.setFitnessReset(false);
                future.complete(createdModel);

                StormDatabase.getInstance().saveStormModel(createdModel);
                return;
            }

            future.complete(playerModel.get());
        });

        return future;
    }
}