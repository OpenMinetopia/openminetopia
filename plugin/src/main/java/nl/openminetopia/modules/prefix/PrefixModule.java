package nl.openminetopia.modules.prefix;

import com.craftmend.storm.api.enums.Where;
import nl.openminetopia.utils.modules.ExtendedSpigotModule;
import com.jazzkuh.modulemanager.spigot.SpigotModuleManager;
import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.api.player.PlayerManager;
import nl.openminetopia.api.player.objects.MinetopiaPlayer;
import nl.openminetopia.modules.data.DataModule;
import nl.openminetopia.modules.data.storm.StormDatabase;
import nl.openminetopia.modules.data.utils.StormUtils;
import nl.openminetopia.modules.player.models.PlayerModel;
import nl.openminetopia.modules.prefix.commands.PrefixCommand;
import nl.openminetopia.modules.prefix.commands.subcommands.PrefixAddCommand;
import nl.openminetopia.modules.prefix.commands.subcommands.PrefixMenuCommand;
import nl.openminetopia.modules.prefix.commands.subcommands.PrefixRemoveCommand;
import nl.openminetopia.modules.prefix.models.PrefixModel;
import nl.openminetopia.modules.prefix.objects.Prefix;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class PrefixModule extends ExtendedSpigotModule {

    Collection<PrefixModel> prefixModels = new ArrayList<>();

    public PrefixModule(SpigotModuleManager<@NotNull OpenMinetopia> moduleManager, DataModule dataModule) {
        super(moduleManager);
    }

    @Override
    public void onEnable() {
        registerComponent(new PrefixCommand());
        registerComponent(new PrefixMenuCommand());
        registerComponent(new PrefixAddCommand());
        registerComponent(new PrefixRemoveCommand());

        Bukkit.getScheduler().runTaskLater(OpenMinetopia.getInstance(), () -> {
            OpenMinetopia.getInstance().getLogger().info("Loading prefixes...");

            this.getPrefixes().whenComplete((prefixModels, throwable) -> {
                if (throwable != null) {
                    OpenMinetopia.getInstance().getLogger().severe("Failed to load prefixes: " + throwable.getMessage());
                    return;
                }

                this.prefixModels = prefixModels;
                OpenMinetopia.getInstance().getLogger().info("Loaded " + prefixModels.size() + " prefixes.");
            });
        }, 20L);

        OpenMinetopia.getCommandManager().getCommandCompletions().registerAsyncCompletion("playerPrefixes", context -> {
            List<String> prefixes = new ArrayList<>();

            PlayerManager.getInstance().getMinetopiaPlayer(context.getPlayer()).whenComplete((minetopiaPlayer, throwable1) -> {
                if (minetopiaPlayer == null) return;

                prefixes.addAll(minetopiaPlayer.getPrefixes().stream()
                        .map(Prefix::getPrefix)
                        .toList());
            });

            return prefixes;
        });
    }



    public List<Prefix> getPrefixesFromPlayer(PlayerModel playerModel) {
        return playerModel.getPrefixes().stream().map(prefixModel ->
                new Prefix(prefixModel.getId(), prefixModel.getPrefix(), prefixModel.getExpiresAt())
        ).collect(Collectors.toList());
    }

    public Optional<Prefix> getActivePrefixFromPlayer(PlayerModel playerModel) {
        return playerModel.getPrefixes().stream().filter(prefixModel ->
                prefixModel.getId().equals(playerModel.getActivePrefixId()) && !prefixModel.isExpired()
        ).map(prefixModel -> new Prefix(prefixModel.getId(), prefixModel.getPrefix(), prefixModel.getExpiresAt())
        ).findFirst();
    }

    public CompletableFuture<Collection<PrefixModel>> getPrefixes() {
        CompletableFuture<Collection<PrefixModel>> completableFuture = new CompletableFuture<>();

        StormDatabase.getExecutorService().submit(() -> {
            try {
                Collection<PrefixModel> prefixModels = StormDatabase.getInstance().getStorm().buildQuery(PrefixModel.class)
                        .execute().join();
                completableFuture.complete(prefixModels);
            } catch (Exception e) {
                completableFuture.completeExceptionally(e);
            }
        });

        return completableFuture;
    }

    public CompletableFuture<Integer> addPrefix(MinetopiaPlayer player, Prefix prefix) {
        PrefixModel prefixModel = new PrefixModel();
        prefixModel.setPlayerId(player.getPlayerModel().getId());
        prefixModel.setPrefix(prefix.getPrefix());
        prefixModel.setExpiresAt(prefix.getExpiresAt());

        return StormDatabase.getInstance().saveStormModel(prefixModel);
    }

    public CompletableFuture<Void> removePrefix(Prefix prefix) {
        return StormUtils.deleteModelData(PrefixModel.class,
                query -> query.where("id", Where.EQUAL, prefix.getId()));
    }
}