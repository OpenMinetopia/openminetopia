package nl.openminetopia.modules.restapi.verticles.banking;

import io.vertx.core.Promise;
import io.vertx.ext.web.RoutingContext;
import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.modules.banking.BankingModule;
import nl.openminetopia.modules.restapi.base.BaseVerticle;
import org.json.simple.JSONObject;

import java.util.UUID;

@SuppressWarnings("unchecked")
public class BankAccountUsersVerticle extends BaseVerticle {

    @Override
    public void start(Promise<Void> startPromise) {
        router.get("/api/bankaccount/:uuid/users").handler(this::handleGetUsers);
        startPromise.complete();
    }

    private void handleGetUsers(RoutingContext context) {
        JSONObject responseJson = new JSONObject();

        UUID accountUuid = parseUuid(context, responseJson);
        if (accountUuid == null) {
            context.response().setStatusCode(400).end(responseJson.toJSONString());
            return;
        }

        BankingModule bankingModule = OpenMinetopia.getModuleManager().get(BankingModule.class);

        bankingModule.getAccountByIdAsync(accountUuid).whenComplete((account, throwable) -> {
            if (throwable != null) {
                handleError(context, responseJson, "Internal server error.", 500);
                return;
            }

            if (account == null) {
                handleError(context, responseJson, "Account not found.", 404);
                return;
            }

            JSONObject usersObject = new JSONObject();
            account.getUsers().forEach((userUuid, permission) -> {
                JSONObject userObject = new JSONObject();
                userObject.put("permission", permission.name());
                usersObject.put(userUuid.toString(), userObject);
            });

            responseJson.put("success", true);
            responseJson.put("users", usersObject);
            context.response().setStatusCode(200).end(responseJson.toJSONString());
        });
    }

    private UUID parseUuid(RoutingContext context, JSONObject responseJson) {
        try {
            return UUID.fromString(context.pathParam("uuid"));
        } catch (IllegalArgumentException e) {
            responseJson.put("success", false);
            responseJson.put("error", "Invalid UUID format.");
            return null;
        }
    }

    private void handleError(RoutingContext context, JSONObject responseJson, String errorMessage, int statusCode) {
        responseJson.put("success", false);
        responseJson.put("error", errorMessage);
        context.response().setStatusCode(statusCode).end(responseJson.toJSONString());
    }
}