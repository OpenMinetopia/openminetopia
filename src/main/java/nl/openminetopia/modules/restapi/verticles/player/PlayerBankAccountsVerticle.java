package nl.openminetopia.modules.restapi.verticles.player;

import io.vertx.core.Promise;
import io.vertx.ext.web.RoutingContext;
import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.modules.banking.BankingModule;
import nl.openminetopia.modules.restapi.base.BaseVerticle;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.json.simple.JSONObject;

import java.util.UUID;

public class PlayerBankAccountsVerticle extends BaseVerticle {

    @Override
    public void start(Promise<Void> startPromise) {
        router.get("/api/player/:uuid/bankaccounts").handler(this::handleGetBankAccounts);
        startPromise.complete();
    }

    @SuppressWarnings("unchecked")
    private void handleGetBankAccounts(RoutingContext context) {
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

            BankingModule bankingModule = OpenMinetopia.getModuleManager().get(BankingModule.class);

            JSONObject accountsObject = new JSONObject();

            bankingModule.getAccountsFromPlayer(playerUuid).forEach(account -> {
                jsonObject.put("success", true);

                JSONObject accountObject = new JSONObject();
                accountObject.put("type", account.getType().name());
                accountObject.put("name", account.getName());
                accountObject.put("frozen", account.getFrozen());
                accountObject.put("balance", account.getBalance());
                accountsObject.put(account.getUniqueId().toString(), accountObject);
            });

            bankingModule.getAccountByIdAsync(playerUuid).whenComplete((bankAccountModel, throwable) -> {
                if (throwable != null) {
                    throwable.printStackTrace();
                    jsonObject.put("success", false);
                    jsonObject.put("error", throwable.getMessage());
                }

                if (bankAccountModel == null) {
                    jsonObject.put("success", false);
                } else {
                    jsonObject.put("success", true);

                    JSONObject accountObject = new JSONObject();
                    accountObject.put("type", bankAccountModel.getType().name());
                    accountObject.put("name", bankAccountModel.getName());
                    accountObject.put("frozen", bankAccountModel.getFrozen());
                    accountObject.put("balance", bankAccountModel.getBalance());
                    accountsObject.put(bankAccountModel.getUniqueId().toString(), accountObject);
                }
            }).join();

            jsonObject.put("accounts", accountsObject);
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
