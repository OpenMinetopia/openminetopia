package nl.openminetopia.modules.places;

import com.craftmend.storm.api.enums.Where;
import com.jazzkuh.modulemanager.spigot.SpigotModule;
import com.jazzkuh.modulemanager.spigot.SpigotModuleManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import lombok.Getter;
import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.modules.data.DataModule;
import nl.openminetopia.modules.data.storm.StormDatabase;
import nl.openminetopia.modules.data.utils.StormUtils;
import nl.openminetopia.modules.places.commands.mtcity.MTCityCommand;
import nl.openminetopia.modules.places.commands.mtcity.subcommands.MTCityCreateCommand;
import nl.openminetopia.modules.places.commands.mtcity.subcommands.MTCityRemoveCommand;
import nl.openminetopia.modules.places.commands.mtcity.subcommands.MTCitySettingCommand;
import nl.openminetopia.modules.places.commands.mtworld.MTWorldCommand;
import nl.openminetopia.modules.places.commands.mtworld.subcommands.MTWorldCreateCommand;
import nl.openminetopia.modules.places.commands.mtworld.subcommands.MTWorldRemoveCommand;
import nl.openminetopia.modules.places.commands.mtworld.subcommands.MTWorldSettingCommand;
import nl.openminetopia.modules.places.listeners.PlayerJoinListener;
import nl.openminetopia.modules.places.listeners.PlayerMoveListener;
import nl.openminetopia.modules.places.listeners.PlayerTeleportListener;
import nl.openminetopia.modules.places.models.CityModel;
import nl.openminetopia.modules.places.models.WorldModel;
import nl.openminetopia.utils.FeatureUtils;
import nl.openminetopia.utils.WorldGuardUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Getter
public class PlacesModule extends SpigotModule<@NotNull OpenMinetopia> {

    public Collection<WorldModel> worldModels = new ArrayList<>();
    public Collection<CityModel> cityModels = new ArrayList<>();

    public PlacesModule(SpigotModuleManager<@NotNull OpenMinetopia> moduleManager, DataModule dataModule) {
        super(moduleManager);
    }

    @Override
    public void onEnable() {
        // Check if places feature is enabled
        if (FeatureUtils.isFeatureDisabled("places")) {
            getLogger().info("Places feature is disabled in config.yml");
            return;
        }

        registerComponent(new MTWorldCommand());
        registerComponent(new MTWorldCreateCommand());
        registerComponent(new MTWorldRemoveCommand());
        registerComponent(new MTWorldSettingCommand());

        registerComponent(new MTCityCommand());
        registerComponent(new MTCityCreateCommand());
        registerComponent(new MTCityRemoveCommand());
        registerComponent(new MTCitySettingCommand());

        registerComponent(new PlayerJoinListener());
        registerComponent(new PlayerTeleportListener());
        registerComponent(new PlayerMoveListener());

        Bukkit.getScheduler().runTaskLater(OpenMinetopia.getInstance(), () -> {
            OpenMinetopia.getInstance().getLogger().info("Loading worlds...");

            this.getWorlds().whenComplete((worldModels, throwable) -> {
                if (throwable != null) {
                    OpenMinetopia.getInstance().getLogger().severe("Failed to load worlds: " + throwable.getMessage());
                    return;
                }

                this.worldModels = worldModels;
                OpenMinetopia.getInstance().getLogger().info("Loaded " + worldModels.size() + " worlds.");
            });

            OpenMinetopia.getInstance().getLogger().info("Loading cities...");

            this.getCities().whenComplete((cityModels, throwable) -> {
                if (throwable != null) {
                    OpenMinetopia.getInstance().getLogger().severe("Failed to load cities: " + throwable.getMessage());
                    return;
                }

                this.cityModels = cityModels;
                OpenMinetopia.getInstance().getLogger().info("Loaded " + cityModels.size() + " cities.");
            });
        }, 20L);

        OpenMinetopia.getCommandManager().getCommandCompletions().registerCompletion("worldNames", c ->
                this.getWorldModels().stream().map(WorldModel::getName).toList());

        OpenMinetopia.getCommandManager().getCommandCompletions().registerCompletion("cityNames", c ->
                this.getCityModels().stream().map(CityModel::getName).toList());
    }



    public WorldModel getWorld(Location location) {
        for (WorldModel worldModel : worldModels) {
            if (!worldModel.getName().equals(location.getWorld().getName())) continue;
            return worldModel;
        }
        return null;
    }

    public CityModel getCity(String cityName) {
        for (CityModel city : cityModels) {
            if (!city.getName().equalsIgnoreCase(cityName)) continue;
            return city;
        }
        return null;
    }

    public CityModel getCity(Location location) {
        List<ProtectedRegion> region = WorldGuardUtils.getProtectedRegions(location, priority -> priority >= 0);

        if (region == null) return null;

        for (CityModel city : cityModels) {
            for (ProtectedRegion protectedRegion : region) {
                if (!city.getName().equalsIgnoreCase(protectedRegion.getId())) continue;
                return city;
            }
        }
        return null;
    }

    public CompletableFuture<Collection<CityModel>> getCities() {
        CompletableFuture<Collection<CityModel>> completableFuture = new CompletableFuture<>();

        StormDatabase.getExecutorService().submit(() -> {
            try {
                Collection<CityModel> cityModels = StormDatabase.getInstance().getStorm().buildQuery(CityModel.class)
                        .execute().join();
                completableFuture.complete(cityModels);
            } catch (Exception e) {
                completableFuture.completeExceptionally(e);
            }
        });

        return completableFuture;
    }

    public CompletableFuture<Collection<WorldModel>> getWorlds() {
        CompletableFuture<Collection<WorldModel>> completableFuture = new CompletableFuture<>();

        StormDatabase.getExecutorService().submit(() -> {
            try {
                Collection<WorldModel> worldModels = StormDatabase.getInstance().getStorm().buildQuery(WorldModel.class)
                        .execute().join();
                completableFuture.complete(worldModels);
            } catch (Exception e) {
                completableFuture.completeExceptionally(e);
            }
        });

        return completableFuture;
    }

    public CompletableFuture<WorldModel> createWorld(String worldName, String title, String color, double temperature, String loadingName) {
        CompletableFuture<WorldModel> completableFuture = new CompletableFuture<>();

        StormDatabase.getExecutorService().submit(() -> {
            WorldModel worldModel = new WorldModel();
            worldModel.setName(worldName);
            worldModel.setTitle(title);
            worldModel.setTemperature(temperature);
            worldModel.setColor(color);
            worldModel.setLoadingName(loadingName);

            StormDatabase.getInstance().saveStormModel(worldModel);
            completableFuture.complete(worldModel);
        });

        return completableFuture;
    }

    public CompletableFuture<Void> deleteWorld(String worldName) {
        return StormUtils.deleteModelData(WorldModel.class,
                query -> query.where("world_name", Where.EQUAL, worldName)
        );
    }

    public CompletableFuture<CityModel> createCity(String cityName, String title, String color, double temperature, String loadingName) {
        CompletableFuture<CityModel> completableFuture = new CompletableFuture<>();

        StormDatabase.getExecutorService().submit(() -> {
            CityModel cityModel = new CityModel();
            cityModel.setName(cityName);
            cityModel.setTitle(title);
            cityModel.setTemperature(temperature);
            cityModel.setColor(color);
            cityModel.setLoadingName(loadingName);

            StormDatabase.getInstance().saveStormModel(cityModel);
            completableFuture.complete(cityModel);
        });

        return completableFuture;
    }

    public CompletableFuture<Void> deleteCity(CityModel city) {
        return StormUtils.deleteModelData(CityModel.class,
                query -> query.where("city_name", Where.EQUAL, city.getName())
        );
    }
}
