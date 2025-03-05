package nl.openminetopia.modules.restapi.verticles.plots;

import io.vertx.core.Promise;
import io.vertx.ext.web.RoutingContext;
import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.modules.plots.PlotModule;
import nl.openminetopia.modules.restapi.base.BaseVerticle;
import nl.openminetopia.utils.WorldGuardUtils;
import org.json.simple.JSONObject;

import java.util.UUID;

public class PlotsVerticle extends BaseVerticle {

    @Override
    public void start(Promise<Void> startPromise) {
        router.get("/api/plots").handler(this::handleGetPlots);
        router.get("/api/plots/:id").handler(this::handleGetPlot);

        router.post("/api/plots/:id/owners/add").handler(this::handleAddOwner);
        router.post("/api/plots/:id/owners/remove").handler(this::handleRemoveOwner);
        router.post("/api/plots/:id/members/add").handler(this::handleAddMember);
        router.post("/api/plots/:id/members/remove").handler(this::handleRemoveMember);
        startPromise.complete();
    }

    @SuppressWarnings("unchecked")
    private void handleGetPlot(RoutingContext context) {
        String regionId = context.pathParam("id");
        JSONObject responseJson = new JSONObject();

        try {
            // Validate region
            var region = WorldGuardUtils.getProtectedRegions(priority -> priority >= 0).stream()
                    .filter(protectedRegion -> protectedRegion.getId().equals(regionId))
                    .findFirst().orElse(null);

            if (region == null) {
                responseJson.put("success", false);
                responseJson.put("error", "Region not found.");
                context.response().setStatusCode(404).end(responseJson.toJSONString());
                return;
            }

            // Build the JSON response for the plot
            JSONObject plotObject = new JSONObject();
            plotObject.put("priority", region.getPriority());
            plotObject.put("owners", region.getOwners().getUniqueIds().stream().map(UUID::toString).toList());
            plotObject.put("members", region.getMembers().getUniqueIds().stream().map(UUID::toString).toList());

            JSONObject flagsObject = new JSONObject();
            region.getFlags().forEach((flag, value) -> {
                flagsObject.put(flag.getName(), value.toString());
            });
            plotObject.put("flags", flagsObject);

            String description = region.getFlag(PlotModule.PLOT_DESCRIPTION);
            if (description != null && !description.isEmpty()) {
                plotObject.put("description", description);
            }

            JSONObject locationObject = new JSONObject();

            JSONObject minLocationObject = new JSONObject();
            minLocationObject.put("x", region.getMinimumPoint().x());
            minLocationObject.put("y", region.getMinimumPoint().y());
            minLocationObject.put("z", region.getMinimumPoint().z());

            JSONObject maxLocationObject = new JSONObject();
            maxLocationObject.put("x", region.getMaximumPoint().x());
            maxLocationObject.put("y", region.getMaximumPoint().y());
            maxLocationObject.put("z", region.getMaximumPoint().z());

            locationObject.put("min", minLocationObject);
            locationObject.put("max", maxLocationObject);

            plotObject.put("location", locationObject);

            responseJson.put("success", true);
            responseJson.put("plot", plotObject);
            context.response().setStatusCode(200).end(responseJson.toJSONString());
        } catch (Exception e) {
            responseJson.put("success", false);
            responseJson.put("error", "An error occurred while retrieving the plot: " + e.getMessage());
            context.response().setStatusCode(500).end(responseJson.toJSONString());
            OpenMinetopia.getInstance().getLogger().severe("Error retrieving plot: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private void handleGetPlots(RoutingContext context) {
        try {
            JSONObject jsonObject = new JSONObject();

            jsonObject.put("success", true);
            JSONObject plotsObject = new JSONObject();

            WorldGuardUtils.getProtectedRegions(priority -> priority >= 0).stream()
                    .filter(protectedRegion -> protectedRegion.getFlag(PlotModule.PLOT_FLAG) != null)
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

    @SuppressWarnings("unchecked")
    private void handleAddOwner(RoutingContext context) {
        modifyRegionMembership(context, true, true);
    }

    @SuppressWarnings("unchecked")
    private void handleRemoveOwner(RoutingContext context) {
        modifyRegionMembership(context, true, false);
    }

    @SuppressWarnings("unchecked")
    private void handleAddMember(RoutingContext context) {
        modifyRegionMembership(context, false, true);
    }

    @SuppressWarnings("unchecked")
    private void handleRemoveMember(RoutingContext context) {
        modifyRegionMembership(context, false, false);
    }

    @SuppressWarnings("unchecked")
    private void modifyRegionMembership(RoutingContext context, boolean isOwner, boolean add) {
        JSONObject responseJson = new JSONObject();
        String regionId = context.pathParam("id");
        String uuidString = context.body().asJsonObject().getString("uuid");

        // Validate UUID
        UUID playerUuid;
        try {
            playerUuid = UUID.fromString(uuidString);
        } catch (IllegalArgumentException e) {
            responseJson.put("success", false);
            responseJson.put("error", "Invalid UUID format.");
            context.response().setStatusCode(400).end(responseJson.toJSONString());
            return;
        }

        // Validate region
        var region = WorldGuardUtils.getProtectedRegions(priority -> priority >= 0).stream()
                .filter(protectedRegion -> protectedRegion.getId().equals(regionId))
                .findFirst().orElse(null);

        if (region == null) {
            responseJson.put("success", false);
            responseJson.put("error", "Region not found.");
            context.response().setStatusCode(404).end(responseJson.toJSONString());
            return;
        }

        // Modify region membership
        try {
            if (isOwner) {
                if (add) {
                    region.getOwners().addPlayer(playerUuid);
                } else {
                    region.getOwners().removePlayer(playerUuid);
                }
            } else {
                if (add) {
                    region.getMembers().addPlayer(playerUuid);
                } else {
                    region.getMembers().removePlayer(playerUuid);
                }
            }

            responseJson.put("success", true);
            context.response().setStatusCode(200).end(responseJson.toJSONString());
        } catch (Exception e) {
            responseJson.put("success", false);
            responseJson.put("error", "An error occurred while modifying the region: " + e.getMessage());
            context.response().setStatusCode(500).end(responseJson.toJSONString());
            OpenMinetopia.getInstance().getLogger().severe("Error modifying region: " + e.getMessage());
        }
    }
}