package nl.openminetopia.modules.restapi.verticles.player;

import io.vertx.core.Promise;
import io.vertx.ext.web.RoutingContext;
import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.api.player.PlayerManager;
import nl.openminetopia.modules.restapi.base.BaseVerticle;
import nl.openminetopia.utils.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.json.simple.JSONObject;

import java.util.UUID;

public class CriminalRecordsVerticle extends BaseVerticle {

    @Override
    public void start(Promise<Void> startPromise) {
        router.get("/api/player/:uuid/criminalrecords").handler(this::handleGetCriminalRecords);
    }

    @SuppressWarnings("unchecked")
    private void handleGetCriminalRecords(RoutingContext context) {
        try {
            UUID playerName = UUID.fromString(context.pathParam("uuid"));
            OfflinePlayer player = Bukkit.getOfflinePlayer(playerName);

            JSONObject jsonObject = new JSONObject();

            if (!player.hasPlayedBefore()) {
                jsonObject.put("success", false);
                context.response().end(jsonObject.toJSONString());
                return;
            }

            PlayerManager.getInstance().getMinetopiaPlayer(player).whenComplete((minetopiaPlayer, throwable) -> {
                if (throwable != null) {
                    throwable.printStackTrace();
                    jsonObject.put("success", false);
                }

                if (minetopiaPlayer == null) {
                    jsonObject.put("success", false);
                } else {
                    jsonObject.put("success", true);
                    JSONObject recordsObject = new JSONObject();

                    minetopiaPlayer.getCriminalRecords().forEach(criminalRecordModel -> {
                        JSONObject recordObject = new JSONObject();
                        recordObject.put("reason", criminalRecordModel.getDescription());
                        recordObject.put("date", criminalRecordModel.getDate());
                        recordObject.put("officer", criminalRecordModel.getOfficerId().toString());
                        recordsObject.put(criminalRecordModel, recordObject);
                    });

                    jsonObject.put("criminalrecords", recordsObject);
                }
                context.response().end(jsonObject.toJSONString());
            }).join();
        } catch (Exception e) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("success", false);
            context.response().end(jsonObject.toJSONString());
            OpenMinetopia.getInstance().getLogger().severe("An error occurred while handling a request: " + e.getMessage());
        }
    }
}