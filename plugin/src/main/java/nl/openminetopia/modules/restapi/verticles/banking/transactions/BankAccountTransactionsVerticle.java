package nl.openminetopia.modules.restapi.verticles.banking.transactions;

import io.vertx.core.Promise;
import io.vertx.ext.web.RoutingContext;
import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.modules.banking.BankingModule;
import nl.openminetopia.modules.restapi.base.BaseVerticle;
import nl.openminetopia.modules.transactions.TransactionsModule;
import nl.openminetopia.modules.transactions.objects.TransactionModel;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@SuppressWarnings("unchecked")
public class BankAccountTransactionsVerticle extends BaseVerticle {

    @Override
    public void start(Promise<Void> startPromise) {
        router.get("/api/bankaccount/:uuid/transactions").handler(this::handleGetBankAccountTransactions);
        startPromise.complete();
    }

    private void handleGetBankAccountTransactions(RoutingContext context) {
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
        }).thenCompose(account -> {
            TransactionsModule transactionsModule = OpenMinetopia.getModuleManager().get(TransactionsModule.class);

            return transactionsModule.getAccountTransactions(TransactionsModule.LookupType.BANK_ACCOUNT, account.getUniqueId()).whenComplete(((transactionModels, throwable) -> {
                if (throwable != null) {
                    return;
                }

                List<TransactionModel> sortedTransactions = transactionModels.stream()
                        .sorted(Comparator.comparing(TransactionModel::getTime).reversed())
                        .toList();

                JSONArray transactionsArray = new JSONArray();
                sortedTransactions.forEach(transaction -> {
                    JSONObject transactionObject = new JSONObject();
                    transactionObject.put("username", transaction.getUsername());
                    transactionObject.put("uuid", transaction.getPlayer());
                    transactionObject.put("type", transaction.getType().name());
                    transactionObject.put("amount", transaction.getAmount());
                    transactionObject.put("time", transaction.getTime());
                    transactionObject.put("bank_account", transaction.getBankAccount());
                    transactionObject.put("description", transaction.getDescription());
                    transactionsArray.add(transactionObject);
                });

                responseJson.put("transactions", transactionsArray);
                responseJson.put("success", true);
                context.response().setStatusCode(200).end(responseJson.toJSONString());
            }));
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