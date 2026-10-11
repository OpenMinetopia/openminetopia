package nl.openminetopia.modules.restapi.verticles.player;

import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.api.player.PlayerManager;
import nl.openminetopia.modules.books.BooksModule;
import nl.openminetopia.modules.books.objects.CustomBook;
import nl.openminetopia.modules.police.models.VogModel;
import nl.openminetopia.modules.restapi.base.BaseVerticle;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.Comparator;
import java.util.UUID;

/**
 * The VOGs of a player, and issuing one for an officer: the plugin decides the outcome from the
 * criminal record and gives the online officer the VOG book, as with /boek krijg vog.
 * Errors carry a "code" next to "error" so the portal can tell them apart.
 */
@SuppressWarnings("unchecked")
public class VogsVerticle extends BaseVerticle {

    @Override
    public void start(Promise<Void> startPromise) {
        router.get("/api/player/:uuid/vogs").handler(this::handleGetVogs);
        router.post("/api/player/:uuid/vogs").handler(this::handleIssueVog);
        startPromise.complete();
    }

    private void handleGetVogs(RoutingContext context) {
        OfflinePlayer player = player(context);
        if (player == null) return;

        PlayerManager.getInstance().getMinetopiaPlayer(player).whenComplete((minetopiaPlayer, throwable) -> {
            if (minetopiaPlayer == null) {
                fail(context, 500, "player_not_loaded", "MinetopiaPlayer has not loaded.");
                return;
            }

            JSONArray vogs = new JSONArray();
            minetopiaPlayer.getVogs().stream()
                    .sorted(Comparator.comparing(VogModel::getDate).reversed())
                    .forEach(vog -> vogs.add(toJson(vog)));

            JSONObject response = new JSONObject();
            response.put("success", true);
            response.put("vogs", vogs);
            context.response().end(response.toJSONString());
        });
    }

    private void handleIssueVog(RoutingContext context) {
        OfflinePlayer player = player(context);
        if (player == null) return;

        UUID officerUuid;
        try {
            JsonObject body = context.body().asJsonObject();
            officerUuid = UUID.fromString(body.getString("officer"));
        } catch (Exception e) {
            fail(context, 400, "invalid_officer", "Invalid officer UUID.");
            return;
        }

        Player officer = Bukkit.getPlayer(officerUuid);
        if (officer == null || !officer.isOnline()) {
            fail(context, 409, "officer_offline", "The officer must be online to receive the VOG book.");
            return;
        }

        if (!officer.hasPermission("openminetopia.books." + BooksModule.VOG_BOOK)) {
            fail(context, 403, "no_permission", "The officer may not issue VOGs.");
            return;
        }

        BooksModule booksModule = OpenMinetopia.getModuleManager().get(BooksModule.class);
        CustomBook book = booksModule.getVogBook();
        if (book == null) {
            fail(context, 409, "book_disabled", "The VOG book is turned off in books.yml.");
            return;
        }

        PlayerManager.getInstance().getMinetopiaPlayer(player).whenComplete((minetopiaPlayer, throwable) -> {
            if (minetopiaPlayer == null) {
                fail(context, 500, "player_not_loaded", "MinetopiaPlayer has not loaded.");
                return;
            }

            minetopiaPlayer.issueVog(officerUuid).whenComplete((vog, vogThrowable) -> {
                if (vog == null) {
                    fail(context, 500, "not_saved", "The VOG could not be saved.");
                    return;
                }

                Bukkit.getScheduler().runTask(OpenMinetopia.getInstance(), () -> {
                    if (officer.isOnline()) {
                        booksModule.giveVogBook(officer, book, player.getName() == null ? "Onbekend" : player.getName(), vog, null);
                    }
                });

                JSONObject response = new JSONObject();
                response.put("success", true);
                response.put("vog", toJson(vog));
                context.response().end(response.toJSONString());
            });
        });
    }

    private OfflinePlayer player(RoutingContext context) {
        try {
            OfflinePlayer player = Bukkit.getOfflinePlayer(UUID.fromString(context.pathParam("uuid")));
            if (!player.isOnline() && !player.hasPlayedBefore()) {
                fail(context, 404, "player_not_found", "Player has not played before.");
                return null;
            }
            return player;
        } catch (IllegalArgumentException e) {
            fail(context, 400, "invalid_player", "Invalid UUID format.");
            return null;
        }
    }

    private JSONObject toJson(VogModel vog) {
        JSONObject json = new JSONObject();
        json.put("id", vog.getId());
        json.put("positive", vog.getPositive());
        json.put("record_count", vog.getRecordCount());
        json.put("officer", vog.getOfficerId() == null ? null : vog.getOfficerId().toString());
        json.put("date", vog.getDate());
        json.put("valid_until", vog.getValidUntil());
        return json;
    }

    private void fail(RoutingContext context, int status, String code, String error) {
        JSONObject response = new JSONObject();
        response.put("success", false);
        response.put("code", code);
        response.put("error", error);
        context.response().setStatusCode(status).end(response.toJSONString());
    }
}
