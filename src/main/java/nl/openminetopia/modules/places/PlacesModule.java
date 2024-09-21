package nl.openminetopia.modules.places;

import nl.openminetopia.api.places.MTCityManager;
import nl.openminetopia.api.places.MTWorldManager;
import nl.openminetopia.modules.Module;
import nl.openminetopia.modules.places.commands.mtcity.MTCityCommand;
import nl.openminetopia.modules.places.commands.mtcity.subcommands.MTCityCreateCommand;
import nl.openminetopia.modules.places.commands.mtworld.MTWorldCommand;
import nl.openminetopia.modules.places.commands.mtworld.subcommands.MTWorldCreate;
import nl.openminetopia.modules.places.listeners.PlayerJoinListener;
import nl.openminetopia.modules.places.listeners.PlayerMoveListener;
import nl.openminetopia.modules.places.listeners.PlayerTeleportListener;

public class PlacesModule extends Module {

    @Override
    public void enable() {
        MTWorldManager.getInstance().loadWorlds();
        MTCityManager.getInstance().loadCities();

        registerCommand(new MTWorldCommand());
        registerCommand(new MTWorldCreate());

        registerCommand(new MTCityCommand());
        registerCommand(new MTCityCreateCommand());

        registerListener(new PlayerJoinListener());
        registerListener(new PlayerTeleportListener());
        registerListener(new PlayerMoveListener());
    }

    @Override
    public void disable() {

    }
}
