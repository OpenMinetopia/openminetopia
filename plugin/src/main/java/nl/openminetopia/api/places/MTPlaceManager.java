package nl.openminetopia.api.places;

import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.api.places.objects.MTPlace;
import nl.openminetopia.modules.places.PlacesModule;
import nl.openminetopia.modules.places.models.CityModel;
import org.bukkit.Location;

public class MTPlaceManager {

    private final PlacesModule placesModule = OpenMinetopia.getModuleManager().get(PlacesModule.class);
    private static MTPlaceManager instance;

    public static MTPlaceManager getInstance() {
        if (instance == null) {
            instance = new MTPlaceManager();
        }
        return instance;
    }

    public MTPlace getPlace(Location location) {
        CityModel city = placesModule.getCity(location);
        if (city == null) return placesModule.getWorld(location);
        return city;
    }
}