package nl.openminetopia.modules.restapi.verticles.banking;

import io.vertx.core.Promise;
import io.vertx.ext.web.RoutingContext;
import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.modules.banking.BankingModule;
import nl.openminetopia.modules.restapi.base.BaseVerticle;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

@SuppressWarnings("unchecked")
public class BankAccountsVerticle extends BaseVerticle {

    @Override
    public void start(Promise<Void> startPromise) {
        router.get("/api/bankaccounts/").handler(this::handleGetBankAccounts);
        startPromise.complete();
    }

    private void handleGetBankAccounts(RoutingContext context) {
        JSONObject responseJson = new JSONObject();

        BankingModule bankingModule = OpenMinetopia.getModuleManager().get(BankingModule.class);

        bankingModule.getBankAccounts().whenComplete((accounts, throwable) -> {
            if (throwable != null) {
                handleError(context, responseJson, "Internal server error.", 500);
                return;
            }

            if (accounts == null || accounts.isEmpty()) {
                handleError(context, responseJson, "No accounts found.", 404);
                return;
            }

            JSONArray accountsArray = new JSONArray();
            accounts.forEach(account -> {
                JSONObject accountObject = new JSONObject();
                accountObject.put("uuid", account.getUniqueId().toString());
                accountObject.put("type", account.getType().name());
                accountObject.put("name", account.getName());
                accountObject.put("frozen", account.getFrozen());
                accountObject.put("balance", account.getBalance());
                accountsArray.add(accountObject);
            });

            responseJson.put("success", true);
            responseJson.put("accounts", accountsArray);
            context.response().setStatusCode(200).end(responseJson.toJSONString());
        });
    }

    private void handleError(RoutingContext context, JSONObject responseJson, String errorMessage, int statusCode) {
        responseJson.put("success", false);
        responseJson.put("error", errorMessage);
        context.response().setStatusCode(statusCode).end(responseJson.toJSONString());
    }
}