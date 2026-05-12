package nl.openminetopia.modules.restapi.verticles.player;

import io.vertx.core.Promise;
import io.vertx.ext.web.RoutingContext;
import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.api.player.PlayerManager;
import nl.openminetopia.modules.color.enums.OwnableColorType;
import nl.openminetopia.modules.restapi.base.BaseVerticle;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.json.simple.JSONObject;

import java.util.UUID;

public class PlayerVerticle extends BaseVerticle {

    @Override
    public void start(Promise<Void> startPromise) {
        router.get("/api/player/:uuid").handler(this::handleGetPlayer);
        startPromise.complete();
    }

    @SuppressWarnings("unchecked")
    private void handleGetPlayer(RoutingContext context) {
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
                    jsonObject.put("uuid", player.getUniqueId().toString());
                    jsonObject.put("level", minetopiaPlayer.getLevel());
                    jsonObject.put("calculated_level", minetopiaPlayer.getCalculatedLevel());
                    jsonObject.put("fitness", minetopiaPlayer.getFitness().getTotalFitness());
                    jsonObject.put("active_prefix", minetopiaPlayer.getActivePrefix().getPrefix());
                    jsonObject.put("active_prefix_color", minetopiaPlayer.getActiveColor(OwnableColorType.PREFIX).displayName());
                    jsonObject.put("active_name_color", minetopiaPlayer.getActiveColor(OwnableColorType.NAME).displayName());
                    jsonObject.put("active_level_color", minetopiaPlayer.getActiveColor(OwnableColorType.LEVEL).displayName());
                    jsonObject.put("active_chat_color", minetopiaPlayer.getActiveColor(OwnableColorType.CHAT).displayName());
                    jsonObject.put("playtimeSeconds", minetopiaPlayer.getPlaytime() / 1000);
                    jsonObject.put("online", player.isOnline());
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