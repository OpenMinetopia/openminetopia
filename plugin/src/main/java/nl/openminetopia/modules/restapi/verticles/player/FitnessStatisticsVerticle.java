package nl.openminetopia.modules.restapi.verticles.player;

import io.vertx.core.Promise;
import io.vertx.ext.web.RoutingContext;
import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.api.player.PlayerManager;
import nl.openminetopia.modules.restapi.base.BaseVerticle;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.json.simple.JSONObject;

import java.util.UUID;

public class FitnessStatisticsVerticle extends BaseVerticle {

    @Override
    public void start(Promise<Void> startPromise) {
        router.get("/api/player/:uuid/fitness").handler(this::handleGetFitness);
        startPromise.complete();
    }

    @SuppressWarnings("unchecked")
    private void handleGetFitness(RoutingContext context) {
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

            PlayerManager.getInstance().getMinetopiaPlayer(player).whenComplete((minetopiaPlayer, throwable) -> {
                if (throwable != null) {
                    throwable.printStackTrace();
                    jsonObject.put("success", false);
                    jsonObject.put("error", throwable.getMessage());
                }

                if (minetopiaPlayer == null) {
                    jsonObject.put("success", false);
                    jsonObject.put("error", "MinetopiaPlayer has not loaded.");
                } else {
                    jsonObject.put("success", true);

                    JSONObject statisticsObject = new JSONObject();

                    if (player.isOnline()) {
                        minetopiaPlayer.getFitness().getFitnessModule().getFitnessRunnable().forceMarkDirty(playerUuid);
                    } else {
                        minetopiaPlayer.getFitness().getFitnessModule().getFitnessRunnable().forceMarkDirty(playerUuid);
                    }

                    minetopiaPlayer.getFitness().getStatistics().forEach(statistic -> {
                        JSONObject statisticObject = new JSONObject();
                        statisticObject.put("gained", statistic.getFitnessGained());
                        statisticObject.put("maximum", statistic.getMaximum());
                        statisticObject.put("points", statistic.getPoints());
                        statisticObject.put("secondary_points", statistic.getSecondaryPoints());
                        statisticObject.put("tertiary_points", statistic.getTertiaryPoints());
                        statisticsObject.put(statistic.getType(), statisticObject);
                    });

                    jsonObject.put("statistics", statisticsObject);

                    jsonObject.put("total_fitness", minetopiaPlayer.getFitness().getTotalFitness());
                }

                context.response().end(jsonObject.toJSONString());
            }).join();
        } catch (Exception e) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("success", false);
            jsonObject.put("error", e.getMessage());
            context.response().end(jsonObject.toJSONString());
            OpenMinetopia.getInstance().getLogger().severe("An error occurred while handling a request: " + e.getMessage());
        }
    }
}