package nl.openminetopia.modules.restapi.verticles.player;

import io.vertx.core.Promise;
import io.vertx.ext.web.RoutingContext;
import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.modules.plots.PlotModule;
import nl.openminetopia.modules.restapi.base.BaseVerticle;
import nl.openminetopia.utils.WorldGuardUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.json.simple.JSONObject;

import java.util.UUID;

public class PlayerPlotsVerticle extends BaseVerticle {

    @Override
    public void start(Promise<Void> startPromise) {
        router.get("/api/player/:uuid/plots").handler(this::handleGetPlots);
        startPromise.complete();
    }

    @SuppressWarnings("unchecked")
    private void handleGetPlots(RoutingContext context) {
        try {
            UUID playerUuid = UUID.fromString(context.pathParam("uuid"));
            OfflinePlayer player = Bukkit.getOfflinePlayer(playerUuid);

            JSONObject jsonObject = new JSONObject();

            if (!player.isOnline() && !player.hasPlayedBefore()) {
                jsonObject.put("success", false);
                jsonObject.put("error", "Player has not played before.");
                context.response().end(jsonObject.toJSONString());
                return;
            }

            jsonObject.put("success", true);
            JSONObject plotsObject = new JSONObject();

            WorldGuardUtils.getProtectedRegions(priority -> priority >= 0).stream()
                    .filter(protectedRegion -> protectedRegion.getFlag(PlotModule.PLOT_FLAG) != null
                            && protectedRegion.getOwners().contains(player.getUniqueId()) || protectedRegion.getMembers().contains(player.getUniqueId()))
                    .forEach(protectedRegion -> {
                        JSONObject plotObject = new JSONObject();
                        plotObject.put("priority", protectedRegion.getPriority());
                        plotObject.put("owners", protectedRegion.getOwners().getUniqueIds().stream().map(UUID::toString).toList());
                        plotObject.put("members", protectedRegion.getMembers().getUniqueIds().stream().map(UUID::toString).toList());

                        JSONObject flagsObject = new JSONObject();
                        protectedRegion.getFlags().forEach((flag, value) -> {
                            flagsObject.put(flag.getName(), value.toString());
                        });
                        plotObject.put("flags", flagsObject);

                        String description = protectedRegion.getFlag(PlotModule.PLOT_DESCRIPTION);
                        if (description != null && !description.isEmpty()) {
                            plotObject.put("description", protectedRegion.getFlag(PlotModule.PLOT_DESCRIPTION));
                        }

                        JSONObject locationObject = new JSONObject();

                        JSONObject minLocationObject = new JSONObject();
                        minLocationObject.put("x", protectedRegion.getMinimumPoint().x());
                        minLocationObject.put("y", protectedRegion.getMinimumPoint().y());
                        minLocationObject.put("z", protectedRegion.getMinimumPoint().z());

                        JSONObject maxLocationObject = new JSONObject();
                        maxLocationObject.put("x", protectedRegion.getMaximumPoint().x());
                        maxLocationObject.put("y", protectedRegion.getMaximumPoint().y());
                        maxLocationObject.put("z", protectedRegion.getMaximumPoint().z());

                        locationObject.put("min", minLocationObject);
                        locationObject.put("max", maxLocationObject);

                        plotObject.put("location", locationObject);

                        if (protectedRegion.getMembers().contains(player.getUniqueId()) && !protectedRegion.getOwners().contains(player.getUniqueId())) {
                            plotObject.put("permission", "MEMBER");
                        } else {
                            plotObject.put("permission", "OWNER");
                        }

                        plotsObject.put(protectedRegion.getId(), plotObject);
                    });


            jsonObject.put("plots", plotsObject);

            context.response().end(jsonObject.toJSONString());
        } catch (Exception e) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("success", false);
            jsonObject.put("error", e.getMessage());
            context.response().end(jsonObject.toJSONString());
            OpenMinetopia.getInstance().getLogger().severe("An error occurred while handling a request: " + e.getMessage());
        }
    }
}