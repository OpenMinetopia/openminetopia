package nl.openminetopia.modules.color;

import com.craftmend.storm.api.enums.Where;
import com.jazzkuh.modulemanager.spigot.SpigotModule;
import com.jazzkuh.modulemanager.spigot.SpigotModuleManager;
import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.modules.data.DataModule;
import org.jetbrains.annotations.NotNull;
import nl.openminetopia.api.player.PlayerManager;
import nl.openminetopia.api.player.objects.MinetopiaPlayer;
import nl.openminetopia.configuration.components.ColorComponent;
import nl.openminetopia.modules.color.commands.ColorCommand;
import nl.openminetopia.modules.color.commands.subcommands.ColorAddCommand;
import nl.openminetopia.modules.color.commands.subcommands.ColorCreateCommand;
import nl.openminetopia.modules.color.commands.subcommands.ColorRemoveCommand;
import nl.openminetopia.modules.color.enums.OwnableColorType;
import nl.openminetopia.modules.color.models.ColorModel;
import nl.openminetopia.modules.color.objects.*;
import nl.openminetopia.modules.data.storm.StormDatabase;
import nl.openminetopia.modules.data.utils.StormUtils;
import nl.openminetopia.modules.player.models.PlayerModel;
import org.bukkit.Bukkit;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class ColorModule extends SpigotModule<@NotNull OpenMinetopia> {

    Collection<ColorModel> colorModels = new ArrayList<>();

    public ColorModule(SpigotModuleManager<@NotNull OpenMinetopia> moduleManager, DataModule dataModule) {
        super(moduleManager);
    }

    @Override
    public void onEnable() {
        registerComponent(new ColorCommand());
        registerComponent(new ColorAddCommand());
        registerComponent(new ColorRemoveCommand());
        registerComponent(new ColorCreateCommand());

        Bukkit.getScheduler().runTaskLater(OpenMinetopia.getInstance(), () -> {
            OpenMinetopia.getInstance().getLogger().info("Loading colors...");

            this.getColors().whenComplete((colorModels, throwable) -> {
                if (throwable != null) {
                    OpenMinetopia.getInstance().getLogger().severe("Failed to load colors: " + throwable.getMessage());
                    return;
                }

                this.colorModels = colorModels;
                OpenMinetopia.getInstance().getLogger().info("Loaded " + colorModels.size() + " colors.");
            });
        }, 20L);

        OpenMinetopia.getCommandManager().getCommandCompletions().registerCompletion("colorTypes", context ->
                Arrays.stream(OwnableColorType.values()).map(OwnableColorType::name).toList());

        OpenMinetopia.getCommandManager().getCommandCompletions().registerCompletion("colorIds", context ->
                OpenMinetopia.getColorsConfiguration().components().stream()
                        .map(ColorComponent::identifier)
                        .toList());

        OpenMinetopia.getCommandManager().getCommandCompletions().registerCompletion("playerColors", context -> {
            MinetopiaPlayer minetopiaPlayer = PlayerManager.getInstance().getOnlineMinetopiaPlayer(context.getPlayer());
            if (minetopiaPlayer == null) return new ArrayList<>();

            return minetopiaPlayer.getColors().stream()
                    .map(OwnableColor::getColorId)
                    .toList();
        });
    }



    public List<OwnableColor> getColorsFromPlayer(PlayerModel playerModel) {
        return playerModel.getColors().stream().map(colorModel -> switch (colorModel.getType()) {
            case OwnableColorType.PREFIX ->
                    new PrefixColor(colorModel.getId(), colorModel.getColorId(), colorModel.getExpiresAt());
            case OwnableColorType.CHAT ->
                    new ChatColor(colorModel.getId(), colorModel.getColorId(), colorModel.getExpiresAt());
            case OwnableColorType.NAME ->
                    new NameColor(colorModel.getId(), colorModel.getColorId(), colorModel.getExpiresAt());
            case OwnableColorType.LEVEL ->
                    new LevelColor(colorModel.getId(), colorModel.getColorId(), colorModel.getExpiresAt());
        }).collect(Collectors.toList());
    }

    public Optional<OwnableColor> getActiveColorFromPlayer(PlayerModel playerModel, OwnableColorType type) {
        int activeId = switch (type) {
            case PREFIX -> playerModel.getActivePrefixColorId();
            case CHAT -> playerModel.getActiveChatColorId();
            case NAME -> playerModel.getActiveNameColorId();
            case LEVEL -> playerModel.getActiveLevelColorId();
        };

        return getColorsFromPlayer(playerModel).stream()
                .filter(color -> color.getType() == type && color.getId() == activeId)
                .findFirst()
                .or(() -> Optional.of(type.defaultColor()));
    }

    public CompletableFuture<Integer> addColor(MinetopiaPlayer player, OwnableColor color) {
        ColorModel colorModel = new ColorModel();
        colorModel.setPlayerId(player.getPlayerModel().getId());
        colorModel.setColorId(color.getColorId());
        colorModel.setExpiresAt(color.getExpiresAt());
        colorModel.setType(color.getType());

        return StormDatabase.getInstance().saveStormModel(colorModel);
    }

    public CompletableFuture<Void> removeColor(OwnableColor color) {
        return StormUtils.deleteModelData(ColorModel.class,
                query -> query.where("id", Where.EQUAL, color.getId()));
    }

    public CompletableFuture<Collection<ColorModel>> getColors() {
        CompletableFuture<Collection<ColorModel>> completableFuture = new CompletableFuture<>();

        StormDatabase.getExecutorService().submit(() -> {
            try {
                Collection<ColorModel> colorModels = StormDatabase.getInstance().getStorm().buildQuery(ColorModel.class)
                        .execute().join();
                completableFuture.complete(colorModels);
            } catch (Exception e) {
                completableFuture.completeExceptionally(e);
            }
        });

        return completableFuture;
    }
}
