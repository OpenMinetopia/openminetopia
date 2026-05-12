package nl.openminetopia.modules.restapi.verticles.banking.transactions;

import io.vertx.core.Promise;
import io.vertx.ext.web.RoutingContext;
import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.modules.banking.BankingModule;
import nl.openminetopia.modules.data.storm.StormDatabase;
import nl.openminetopia.modules.restapi.base.BaseVerticle;
import org.json.simple.JSONObject;

import java.util.UUID;

@SuppressWarnings("unchecked")
public class BankAccountTransactionVerticle extends BaseVerticle {

    @Override
    public void start(Promise<Void> startPromise) {
        router.post("/api/bankaccount/:uuid/withdraw").handler(this::handleWithdraw);
        router.post("/api/bankaccount/:uuid/deposit").handler(this::handleDeposit);
        startPromise.complete();
    }

    private void handleWithdraw(RoutingContext context) {
        processTransaction(context, TransactionType.WITHDRAW);
    }

    private void handleDeposit(RoutingContext context) {
        processTransaction(context, TransactionType.DEPOSIT);
    }

    private void processTransaction(RoutingContext context, TransactionType type) {
        JSONObject responseJson = new JSONObject();

        UUID accountUuid = parseUuid(context, responseJson);
        if (accountUuid == null) {
            context.response().setStatusCode(400).end(responseJson.toJSONString());
            return;
        }

        Double amount = parseAmount(context, responseJson);
        if (amount == null) {
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

            if (type == TransactionType.WITHDRAW && account.getBalance() < amount) {
                handleError(context, responseJson, "Insufficient balance.", 400);
                return;
            }

            double updatedBalance = (type == TransactionType.WITHDRAW)
                    ? account.getBalance() - amount
                    : account.getBalance() + amount;

            account.setBalance(updatedBalance);
            StormDatabase.getInstance().saveStormModel(account);

            responseJson.put("success", true);
            responseJson.put("newBalance", updatedBalance);
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

    private Double parseAmount(RoutingContext context, JSONObject responseJson) {
        try {
            double amount = Double.parseDouble(context.body().asJsonObject().getString("amount"));
            if (amount <= 0) {
                throw new IllegalArgumentException("Amount must be positive.");
            }
            return amount;
        } catch (Exception e) {
            responseJson.put("success", false);
            responseJson.put("error", "Invalid amount.");
            return null;
        }
    }

    private void handleError(RoutingContext context, JSONObject responseJson, String errorMessage, int statusCode) {
        responseJson.put("success", false);
        responseJson.put("error", errorMessage);
        context.response().setStatusCode(statusCode).end(responseJson.toJSONString());
    }

    private enum TransactionType {
        WITHDRAW, DEPOSIT
    }
}