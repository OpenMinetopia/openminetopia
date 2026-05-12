package nl.openminetopia.modules.restapi.verticles.places;

import io.vertx.core.Promise;
import io.vertx.ext.web.RoutingContext;
import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.modules.places.PlacesModule;
import nl.openminetopia.modules.places.models.WorldModel;
import nl.openminetopia.modules.restapi.base.BaseVerticle;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.Optional;

@SuppressWarnings("unchecked")
public class PlacesVerticle extends BaseVerticle {

    @Override
    public void start(Promise<Void> startPromise) {
        router.get("/api/places/worlds").handler(this::handleGetWorlds);
        router.get("/api/places/cities").handler(this::handleGetCities);
        startPromise.complete();
    }

    private void handleGetWorlds(RoutingContext context) {
        JSONObject responseJson = new JSONObject();
        try {
            PlacesModule placesModule = OpenMinetopia.getModuleManager().get(PlacesModule.class);

            JSONArray worldsArray = new JSONArray();
            placesModule.getWorldModels().forEach(worldModel -> {
                JSONObject worldObject = new JSONObject();
                worldObject.put("name", worldModel.getName());
                worldObject.put("color", worldModel.getColor());
                worldObject.put("title", worldModel.getTitle());
                worldObject.put("loading_name", worldModel.getLoadingName());
                worldObject.put("temperature", worldModel.getTemperature());
                worldsArray.add(worldObject);
            });

            responseJson.put("success", true);
            responseJson.put("worlds", worldsArray);
            context.response().setStatusCode(200).end(responseJson.toJSONString());
        } catch (Exception e) {
            handleError(context, responseJson, "Failed to retrieve worlds.", 500, e);
        }
    }

    private void handleGetCities(RoutingContext context) {
        JSONObject responseJson = new JSONObject();
        try {
            PlacesModule placesModule = OpenMinetopia.getModuleManager().get(PlacesModule.class);

            JSONArray citiesArray = new JSONArray();
            placesModule.getCityModels().forEach(cityModel -> {
                JSONObject cityObject = new JSONObject();
                cityObject.put("name", cityModel.getName());
                cityObject.put("color", cityModel.getColor());
                cityObject.put("title", cityModel.getTitle());
                cityObject.put("loading_name", cityModel.getLoadingName());
                cityObject.put("temperature", cityModel.getTemperature());

                Optional<WorldModel> worldModel = placesModule.getWorldModels().stream()
                        .filter(world -> world.getId().equals(cityModel.getWorldId()))
                        .findFirst();

                cityObject.put("world", worldModel.map(WorldModel::getName).orElse("Unknown"));

                citiesArray.add(cityObject);
            });

            responseJson.put("success", true);
            responseJson.put("cities", citiesArray);
            context.response().setStatusCode(200).end(responseJson.toJSONString());
        } catch (Exception e) {
            handleError(context, responseJson, "Failed to retrieve cities.", 500, e);
        }
    }

    private void handleError(RoutingContext context, JSONObject responseJson, String errorMessage, int statusCode, Exception e) {
        responseJson.put("success", false);
        responseJson.put("error", errorMessage);
        context.response().setStatusCode(statusCode).end(responseJson.toJSONString());
        OpenMinetopia.getInstance().getLogger().severe("Error: " + errorMessage + " - " + e.getMessage());
    }
}